package org.reldb.rel.v0.external;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Enumeration;

import org.eclipse.jdt.core.compiler.CompilationProgress;
import org.reldb.rel.exceptions.*;
import org.reldb.rel.v0.generator.*;
import org.reldb.rel.v0.storage.RelDatabase;
import org.reldb.rel.v0.types.*;
import org.reldb.rel.v0.types.builtin.TypeBoolean;
import org.reldb.rel.v0.types.builtin.TypeCharacter;
import org.reldb.rel.v0.types.builtin.TypeInteger;
import org.reldb.rel.v0.types.builtin.TypeRational;
import org.reldb.rel.v0.version.Version;

/**
 * @author Dave
 *
 */
public class ForeignCompilerJava {
	
	private Generator generator;
	private boolean verbose;
	
	public ForeignCompilerJava(Generator generator, boolean verbose) {
		this.generator = generator;
		this.verbose = verbose;
	}

	private static final String MANIFEST = "META-INF/MANIFEST.MF";
	
	private static String relCoreJar = null;
	
	// This bit of hackery is used to get a Rel core jar file so we can compile user Java code under a Web Start environment.
	// From https://weblogs.java.net/blog/2005/05/27/using-java-compiler-your-web-start-application
	private synchronized String getLocalWebStartRelJarName() {
		if (relCoreJar != null)
			return relCoreJar;
		try {
			for (Enumeration<?> e = getClass().getClassLoader().getResources(MANIFEST); e.hasMoreElements();) {
				URL url = (URL) e.nextElement();
				if (url.getFile().contains(Version.getCoreJarFilename())) {
					String relCoreJarName = getLocalJarFilename(url);
					if (relCoreJarName != null) {
						relCoreJar = relCoreJarName;
						return relCoreJar;
					}
				}
			}
		} catch (IOException exc) {
			exc.printStackTrace();
		}
		return null;
	}
	
	private static String getLocalJarFilename(URL remoteManifestFileName) {
		// remove trailing
		String urlStrManifest = remoteManifestFileName.getFile();
		String urlStrJar = urlStrManifest.substring(0, urlStrManifest.length() - MANIFEST.length() - 2);
		InputStream inputStreamJar = null;
		File tempJar;
		FileOutputStream fosJar = null;
		try {
			URL urlJar = new URL(urlStrJar);
			inputStreamJar = urlJar.openStream();
			String strippedName = urlStrJar;
			int dotIndex = strippedName.lastIndexOf('.');
			if (dotIndex >= 0) {
				strippedName = strippedName.substring(0, dotIndex);
				strippedName = strippedName.replace("/", File.separator);
				strippedName = strippedName.replace("\\", File.separator);
				int slashIndex = strippedName.lastIndexOf(File.separator);
				if (slashIndex >= 0) {
					strippedName = strippedName.substring(slashIndex + 1);
				}
			}
			tempJar = File.createTempFile(strippedName, ".jar");
			tempJar.deleteOnExit();
			fosJar = new FileOutputStream(tempJar);
			byte[] ba = new byte[1024];
			while (true) {
				int bytesRead = inputStreamJar.read(ba);
				if (bytesRead < 0) {
					break;
				}
				fosJar.write(ba, 0, bytesRead);
			}
			return tempJar.getAbsolutePath();
		} catch (Exception ioe) {
			System.out.println(ioe.getMessage());
			ioe.printStackTrace();
		} finally {
			try {
				if (inputStreamJar != null) {
					inputStreamJar.close();
				}
			} catch (IOException ioe) {}
			try {
				if (fosJar != null) {
					fosJar.close();
				}
			} catch (IOException ioe) {}
		}
		return null;
	}
	
	/** Return the package to which this entire Rel core belongs. */
	private String getPackagePrefix() {
		String thisPackageName = getClass().getPackage().getName();
		String relPackageName = thisPackageName.replace(".external", "");
		return relPackageName;
	}
    
    /** Return a classpath cleaned of non-existent files and Web Start's deploy.jar.  
     * Classpath elements with spaces are converted to quote-delimited strings. */
    private final static String cleanClassPath(String s) {
    	if (java.io.File.separatorChar == '/')
    		s = s.replace('\\', '/');
    	else
    		s = s.replace('/', '\\');
        String outstr = "";
        java.util.StringTokenizer st = new java.util.StringTokenizer(s, java.io.File.pathSeparator);
        while (st.hasMoreElements()) {
            String element = (String)st.nextElement();
            java.io.File f = new java.io.File(element);
            if (f.exists() && !element.contains("deploy.jar")) {
            	String fname = f.toString();
            	if (fname.indexOf(' ')>=0)
            		fname = '"' + fname + '"';
                outstr += ((outstr.length()>0) ? java.io.File.pathSeparator : "") + fname;
            }
        }
        return outstr;
    }
	
	/** Return classpath to the Rel core. */
    private String getLocalClasspath(RelDatabase database) {
        String classPath = System.getProperty("user.dir") + 
        	   java.io.File.pathSeparatorChar + Version.getCoreJarFilename() + 
        	   java.io.File.pathSeparatorChar + database.getJavaUserSourcePath() +
        	   java.io.File.pathSeparatorChar + database.getHomeDir();
        if (database.getAdditionalJarsForJavaCompilerClasspath() != null)
        	for (String path: database.getAdditionalJarsForJavaCompilerClasspath()) {
       			notify("ForeignCompilerJava: extra jar for classpath: " + path);
	    		classPath += java.io.File.pathSeparator + path;
        	}
        else
       		notify("ForeignCompilerJava: no extra jars for classpath");
       	notify("ForeignCompilerJava: raw classpath is " + classPath);
        return classPath;
    }

    /** Given a Type, return the name of the equivalent Java type. */
    private final static String getJavaTypeForType(Generator generator, Type t) {
        if (t == null)
            return "void";
        else 
            return t.getValueClassname(generator);
    }

    /** Given a Type, return a Java expression to pop a Value from the operand
     * stack and convert it to an equivalent Java type. */
    private final static String getJavaPopForType(Generator generator, Type t) {
        if (t == null)
            throw new ExceptionFatal("RS0292: Got null type in getJavaPopForType()");
        return "(" + getJavaTypeForType(generator, t) + ")context.pop()";
    }
    
    /** Given an operator signature, return a Java method parameter definition. */
    private final static String getJavaMethodParmsForParameters(Generator generator, OperatorSignature os) {
        String s = "Context context";
        for (int i=0; i<os.getParmCount(); i++)
            s += ", " + getJavaTypeForType(generator, os.getParameterType(i)) + " " + os.getParameterName(i);
        return s;
    }
    
    /** Compile foreign code using Eclipse JDT compiler. */
    private void compileForeignCode(RelDatabase database, PrintStream stream, String className, String src) {
    	ByteArrayOutputStream messageStream = new ByteArrayOutputStream();
    	ByteArrayOutputStream warningStream = new ByteArrayOutputStream();
    	String warningSetting = new String("allDeprecation,"
    			+ "allJavadoc," + "assertIdentifier," + "charConcat,"
    			+ "conditionAssign," + "constructorName," + "deprecation,"
    			+ "emptyBlock," + "fieldHiding," + "finalBound,"
    			+ "finally," + "indirectStatic," + "intfNonInherited,"
    			+ "javadoc," + "localHiding," + "maskedCatchBlocks,"
    			+ "noEffectAssign," + "pkgDefaultMethod," + "serial,"
    			+ "semicolon," + "specialParamHiding," + "staticReceiver,"
    			+ "syntheticAccess," + "unqualifiedField,"
    			+ "unnecessaryElse," + "uselessTypeCheck," + "unsafe,"
    			+ "unusedArgument," + "unusedImport," + "unusedLocal,"
    			+ "unusedPrivate," + "unusedThrown");

    	String classpath = 
    			cleanClassPath(System.getProperty("java.class.path")) + 
    			java.io.File.pathSeparatorChar + 
    			cleanClassPath(getLocalClasspath(database));
    	String webclasspath = getLocalWebStartRelJarName();
    	if (webclasspath != null)
    		classpath += File.pathSeparatorChar + webclasspath;

        // If resource directory doesn't exist, create it.
        File resourceDir = new File(database.getJavaUserSourcePath()); 
        if (!(resourceDir.exists()))
            resourceDir.mkdirs();
    	File sourcef;
    	try {
    		// Write source to a Java source file
    		sourcef = new File(database.getJavaUserSourcePath() + java.io.File.separator + getStrippedClassname(className) + ".java");
    		PrintStream sourcePS = new PrintStream(new FileOutputStream(sourcef));
    		sourcePS.print(src);
    		sourcePS.close();
    	} catch (IOException ioe) {
    		throw new ExceptionFatal("RS0293: Unable to save Java source: " + ioe.toString());
    	}

   		notify("ForeignCompilerJava: classpath = " + classpath);
    	
    	// Start compilation using JDT
   		String commandLine = "-1.9 -source 1.9 -warn:" + 
    			warningSetting + " " + 
    			"-cp " + classpath + " \"" + sourcef + "\"";
    	boolean compiled = org.eclipse.jdt.core.compiler.batch.BatchCompiler.compile(
    			commandLine,
    			new PrintWriter(messageStream), new PrintWriter(warningStream), 
    			new CompilationProgress() {
					@Override
					public void begin(int arg0) {
					}
					@Override
					public void done() {
					}
					@Override
					public boolean isCanceled() {
						return false;
					}
					@Override
					public void setTaskName(String arg0) {
						ForeignCompilerJava.this.notify(arg0);
					}
					@Override
					public void worked(int arg0, int arg1) {
					}
    			}
    	);

    	String compilerMessages = "";
    	// Parse the messages and the warnings.
    	BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(messageStream.toByteArray())));
    	while (true) {
    		String str = null;
    		try {
    			str = br.readLine();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    		if (str == null) {
    			break;
    		}
    		compilerMessages += str + '\n';
    	}
    	BufferedReader brWarnings = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(warningStream.toByteArray())));
    	while (true) {
    		String str = null;
    		try {
    			str = brWarnings.readLine();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    		if (str == null) {
    			break;
    		}
    		compilerMessages += str + '\n';
    	}

    	if (!compiled)
        	throw new ExceptionSemantic("RS0005: Compilation failed due to errors: \n" + compilerMessages + "\n");    		    
    }
    
    /** Get a stripped name.  Only return text after the final '.' */
    private static String getStrippedName(String name) {
        int lastDot = name.lastIndexOf('.');
        if (lastDot >= 0)
            return name.substring(lastDot + 1);
        else
            return name;
    }
    
    /** Get stripped Java Class name. */
    private static String getStrippedClassname(String name) {
    	return getStrippedName(name);
    }
        
    private Type getTypeForClassName(Generator generator, String className) {
        if (className.equals(getPackagePrefix() + ".values.ValueBoolean"))
            return TypeBoolean.getInstance();
        else if (className.equals(getPackagePrefix() + ".values.ValueCharacter"))
            return TypeCharacter.getInstance();
        else if (className.equals(getPackagePrefix() + ".values.ValueInteger"))
            return TypeInteger.getInstance();
        else if (className.equals(getPackagePrefix() + ".values.ValueRational"))
            return TypeRational.getInstance();
        else if (className.equals(getPackagePrefix() + ".values.ValueObject"))
            return TypeInteger.getInstance();
        else if (className.equals(getPackagePrefix() + ".values.ValueOperator"))
            return TypeOperator.getInstance();
        else 
            return generator.locateType(getStrippedClassname(className));
    }
    
    private void notify(String s) {
    	if (verbose)
    		System.out.println(s);
    }
    
    /** Given a Java class, return the Type for which it is a Value, if there is one.
     * Return null if there isn't one. */
	private Type getTypeForClass(Class<?> c) {
    	return getTypeForClassName(generator, c.getName());
    }
    
    /** Given a class, and one of its constructors, define a selector operator. */
	private void addSelectorForClass(Class<?> c, java.lang.reflect.Constructor<?> con) {
    	String name = getTypeForClass(c).getValueClassname(generator);
    	OperatorSignature sig = new OperatorSignature(name);
        Class<?>[] rawParameters = con.getParameterTypes();
        if (rawParameters.length == 0)
            return;
        sig.setReturnType(getTypeForClass(c));
        if (sig.getReturnType() == null)
            throw new ExceptionSemantic("RS0006:  Unable to create selector for TYPE " + c.getName());
        String arguments = "context.getGenerator()";
        if (!rawParameters[0].toString().equals("class " + Generator.class.getCanonicalName())) {
    		notify("x Constructor " + con + " of " + c + " not implemented as OPERATOR due to missing first parameter of type " + Generator.class.getCanonicalName());
    		return;
    	}
        for (int i=1; i<rawParameters.length; i++) {
            if (rawParameters[i].isPrimitive()) {
                notify("x Constructor " + con + " of " + c + " not implemented as selector due to primitive parameter type.");
                return;
            }
            Type t = getTypeForClass(rawParameters[i]);
            if (t == null) {
                notify("x Constructor " + con + " of " + c + " not implemented as selector due to unrecognised parameter type " + rawParameters[i].toString());
                return;     // type not found -- not implementable
            }
            String parmName = "p" + i;
            sig.addParameter("p" + i, t);
            arguments += ((arguments.length() > 0) ? ", " : "") + parmName;
        }
        String language = "Java";
        String typeName = getStrippedClassname(c.getName());
        String src = "return new " + typeName + "(" + arguments + ");";  
        notify("* Constructor " + con + " of " + c + " implemented as selector OPERATOR " + sig);
        OperatorDefinition newOp = compileForeignOperator(sig, language, src);
        newOp.setCreatedByType(typeName);
        newOp.getReferences().addReferenceToType(typeName);
        generator.addOperator(newOp);
    }
    
    /** Given a class, and one of its methods, define an operator. */
	private void addOperatorForClass(Class<?> c, java.lang.reflect.Method m) {
        String name;
    	if (Modifier.isStatic(m.getModifiers()))
    		name = m.getName();
    	else
        	name = "THE_" + m.getName();
    	OperatorSignature sig = new OperatorSignature(name);
        Class<?>[] rawParameters = m.getParameterTypes();
        Class<?> rawReturnType = m.getReturnType();
        if (rawReturnType.isPrimitive()) {
            notify("x Method " + m + " of " + c + " not implemented as OPERATOR due to primitive return type.");
            return;
        }
        Type returnType = getTypeForClass(rawReturnType);
        if (returnType == null) {
            notify("x Method " + m + " of " + c + " not implemented as OPERATOR due to unrecognised return type.");
            return;
        }
        sig.setReturnType(returnType);
        Type instanceType = getTypeForClass(c);
        if (instanceType == null)
            throw new ExceptionSemantic("RS0007: Java type " + c + " not recognised.");
        String arguments = "context.getGenerator()";
    	if (!Modifier.isStatic(m.getModifiers()))
    		sig.addParameter("px", getTypeForClass(c));
    	else if (!rawParameters[0].toString().equals("class " + Generator.class.getCanonicalName())) {
    		notify("x Method " + m + " of " + c + " not implemented as OPERATOR due to missing first parameter of type " + Generator.class.getCanonicalName());
    		return;
    	}
        for (int i=1; i<rawParameters.length; i++) {
            String parmName = "p" + i;
            if (rawParameters[i].isPrimitive()) {
                notify("x Method " + m + " of " + c + " not implemented as OPERATOR due to primitive parameter type.");
                return;
            }
            Type t = getTypeForClass(rawParameters[i]);
            if (t == null) {
                notify("x Method " + m + " of " + c + " not implemented as OPERATOR due to unrecognised parameter type " + rawParameters[i].toString());
                return;     // type not found -- not implementable
            }
            sig.addParameter(parmName, t);
            arguments += ((arguments.length() > 0) ? ", " : "") + parmName;
        }
        String language = "Java";
        String src;
        String typeName = getStrippedClassname(c.getName());
        String returnStr = (returnType == null) ? "" : "return ";
        if (Modifier.isStatic(m.getModifiers()))
            src = returnStr + typeName + "." + m.getName() + "(" + arguments + ");";
    	else
            src = returnStr + "px." + m.getName() + "(" + arguments + ");";
        notify("* Method " + m + " of " + c + " implemented as OPERATOR " + sig);
        OperatorDefinition newOp = compileForeignOperator(sig, language, src);
        newOp.setCreatedByType(typeName);
        newOp.getReferences().addReferenceToType(typeName);
        generator.addOperator(newOp);
    }
    
    /** Given a Java Class, generate Rel Operators to access it. */
	private void addOperatorsForClass(Class<?> c) {
        try {
            for (java.lang.reflect.Constructor<?> constructor: c.getDeclaredConstructors())
                addSelectorForClass(c, constructor);
            for (java.lang.reflect.Method method: c.getDeclaredMethods())
       			addOperatorForClass(c, method);
        } catch (Throwable t) {
            throw new ExceptionSemantic("RS0008: Unable to add operators for " + c.getName() + ": " + t.toString());
        }
    }
    
    /** Compile a user-defined Java-based type. */
	public void compileForeignType(String name, String language, String src) {
        if (!language.equals("Java"))
            throw new ExceptionSemantic("RS0009: " + language + " is not recognised as a foreign language.");
        String body = src.replace("\n", "\n\t");
        src = "package " + RelDatabase.getRelUserCodePackage() + ";\n\n" +
        	  "import " + getPackagePrefix() + ".vm.*;\n" +
              "import " + getPackagePrefix() + ".types.*;\n" + 
              "import " + getPackagePrefix() + ".types.builtin.*;\n" + 
              "import " + getPackagePrefix() + ".types.userdefined.*;\n" + 
			  "import " + getPackagePrefix() + ".values.*;\n" +
			  "import " + getPackagePrefix() + ".generator.Generator;\n\n" +
              "public class " + name + " extends ValueTypeJava {\n" + 
                  body +
              "\n}";
        compileForeignCode(generator.getDatabase(), generator.getPrintStream(), name, src);
        try {
        	Class<?> typeClass = generator.getDatabase().loadClass(name);
            if (typeClass == null)
                throw new ExceptionSemantic("RS0010: Despite having been compiled, " + name + " could not be loaded.");
            notify("> Java class " + typeClass.getName() + " loaded to implement type " + name);
            Constructor<?> ctor = typeClass.getConstructor(new Class[] {Generator.class});
            if (ctor == null)
            	throw new ExceptionSemantic("RS0011: Unable to find a constructor of the form " + name + "(Generator generator)");
            generator.addTypeInProgress(name, (Type)ctor.newInstance(generator));
            addOperatorsForClass(typeClass);
        } catch (Throwable thrown) {
            throw new ExceptionSemantic("RS0012: Creation of type " + name + " failed: " + thrown.toString());
        }
    }

    /** Compilation of foreign operator. */
    public OperatorDefinition compileForeignOperator(OperatorSignature signature, String language, String src) {
        if (!language.equals("Java"))
            throw new ExceptionSemantic("RS0013: " + language + " is not recognised as a foreign language.");
        // Modify src here to include class definition and appropriate methods
        // One of these must be 'public static void execute(Session s)'
        String comp = 
        	  "package " + RelDatabase.getRelUserCodePackage() + ";\n\n" +
			  "import " + getPackagePrefix() + ".types.*;\n" +
              "import " + getPackagePrefix() + ".types.builtin.*;\n" + 
              "import " + getPackagePrefix() + ".types.userdefined.*;\n" + 
			  "import " + getPackagePrefix() + ".values.*;\n" +
			  "import " + getPackagePrefix() + ".vm.Context;\n\n" +
              "public class " + getStrippedClassname(signature.getClassSignature()) + " {\n" +
              "\tprivate static final " + getJavaTypeForType(generator, signature.getReturnType()) + " " +
              "do_" + getStrippedName(signature.getName()) + "(" + getJavaMethodParmsForParameters(generator, signature) + ") {\n" + 
              "\t\t" + src.replace("\n", "\n\t\t") + "\n\t}\n\n" +
              "\tpublic static final void execute(Context context) {\n";
        String args = "context";
        Type[] parmTypes = signature.getParameterTypes();
        for (int i=parmTypes.length - 1; i >= 0; i--) {
            comp += "\t\t" + getJavaTypeForType(generator, parmTypes[i]) + " " + "p" + i + " = " + getJavaPopForType(generator, parmTypes[i]) + ";\n";
            args += ", p" + (parmTypes.length - i - 1);
        }
        if (signature.getReturnType() != null)
            comp += "\t\tcontext.push(" + "do_" + getStrippedName(signature.getName()) + "(" + args + "));\n";
        else
            comp += "\t" + "do_" + getStrippedName(signature.getName()) + "(" + args + ");\n";
        comp += "\t}\n}";
        // compile source
        compileForeignCode(generator.getDatabase(), generator.getPrintStream(), signature.getClassSignature(), comp);
        notify("> Java class " + signature.getClassSignature() + " compiled to implement OPERATOR " + signature);
        // construct operator definition
        OperatorDefinitionNative o;
        Method method = generator.getDatabase().getMethod(signature);
        if (signature.getReturnType() != null)
        	o = new OperatorDefinitionNativeFunctionExternal(signature, method);
        else
        	o = new OperatorDefinitionNativeProcedureExternal(signature, method);
        o.setSourceCode(signature.getOperatorDeclaration() + " " + language + " FOREIGN " + src + "\nEND OPERATOR;");
        return o;
    }
    
}
