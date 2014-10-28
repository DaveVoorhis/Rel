package ca.mb.armchair.rel3.external;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import ca.mb.armchair.rel3.interpreter.ClassPathHack;
import ca.mb.armchair.rel3.exceptions.*;
import ca.mb.armchair.rel3.types.*;
import ca.mb.armchair.rel3.types.builtin.TypeBoolean;
import ca.mb.armchair.rel3.types.builtin.TypeCharacter;
import ca.mb.armchair.rel3.types.builtin.TypeInteger;
import ca.mb.armchair.rel3.types.builtin.TypeRational;
import ca.mb.armchair.rel3.storage.RelDatabase;
import ca.mb.armchair.rel3.generator.*;

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
	
	/** Return classpath to the Rel core. */
    private static String getLocalClasspath(RelDatabase database) {
        return "." + java.io.File.pathSeparatorChar + 
               "Rel.jar" + java.io.File.pathSeparatorChar +
               database.getJavaUserSourcePath();
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

    /** Obtain a Java compiler invocation string.  The file name of the
     * Java source file to be compiled will be appended to this string 
     * to form a compilation command for external execution. */
    private static String getJavacInvocation(RelDatabase database) {        
        String sysclasspath = cleanClassPath(System.getProperty("java.class.path"));
        String devclasspath = cleanClassPath(getLocalClasspath(database));
        return "javac -classpath " + sysclasspath + java.io.File.pathSeparatorChar + devclasspath;
    }
    
    /** Given an operator signature, return a Java method parameter definition. */
    private final static String getJavaMethodParmsForParameters(Generator generator, OperatorSignature os) {
        String s = "Context context";
        for (int i=0; i<os.getParmCount(); i++)
            s += ", " + getJavaTypeForType(generator, os.getParameterType(i)) + " " + os.getParameterName(i);
        return s;
    }
    
    /** Return a classpath cleaned of non-existent files.  Classpath
     * elements with spaces are converted to quote-delimited strings. */
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
            if (f.exists()) {
            	String fname = f.toString();
            	if (fname.indexOf(' ')>=0)
            		fname = '"' + fname + '"';
                outstr += ((outstr.length()>0) ? java.io.File.pathSeparator : "") + fname;
            }
        }
        return outstr;
    }
	private static String getToolsJarPath(String javaHome) {
        File toolsJar = new File(javaHome, "../lib/tools.jar"); 
        if (toolsJar.exists())
        	return toolsJar.toString();
        toolsJar = new File(javaHome, "lib/tools.jar");
        if (toolsJar.exists())
        	return toolsJar.toString();
        toolsJar = new File(javaHome, "tools.jar");
        if (toolsJar.exists())
        	return toolsJar.toString();
        return null;
	}
	
	private static String getToolsJarPath() {
		String javaHome = System.getProperty("java.home"); 
		if (javaHome != null) {
			 String toolsDir = getToolsJarPath(javaHome);
			 if (toolsDir != null)
				 return toolsDir;
		}
        javaHome = System.getenv("JAVA_HOME");
		if (javaHome != null) {
			 String toolsDir = getToolsJarPath(javaHome);
			 if (toolsDir != null)
				 return toolsDir;
		}
        javaHome = System.getenv("JDK_HOME");
		if (javaHome != null) {
			 String toolsDir = getToolsJarPath(javaHome);
			 if (toolsDir != null)
				 return toolsDir;
		}
		return null;
	}
	
	private JavaCompiler getSystemJavaCompiler() {
		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		if (compiler != null)
			return compiler;
		String toolsJarPath = getToolsJarPath();
		if (toolsJarPath != null) {
			notify("ForeignCompilerJava: Found tools.jar at " + toolsJarPath.toString());
			try {
				ClassPathHack.addFile(toolsJarPath.toString());
			} catch (IOException e) {
				notify("ForeignCompilerJava: Unable to update classpath to include Tools.jar due to I/O error.");
				return null;
			}
			notify("ForeignCompilerJava: Attempting to load compiler via ToolProvider.");
			compiler = ToolProvider.getSystemJavaCompiler();
			if (compiler != null)
				return compiler;
			try {
				notify("ForeignCompilerJava: Attempting to load compiler class.");
				@SuppressWarnings("unchecked")
				Class<JavaCompiler> compilerClass = (Class<JavaCompiler>) Class.forName("com.sun.tools.javac.api.JavacTool");
				notify("ForeignCompilerJava: Tools.jar loaded.");
				if (compilerClass != null) {
					notify("ForeignCompilerJava: compiler class loaded.");
					try {
						return compilerClass.newInstance();
					} catch (Exception e) {
						notify("ForeignCompilerJava: Unable to instantiate internal Java compiler: " + e.toString());
					}
				} else {
					notify("ForeignCompilerJava: compiler class load failed.");
				}
			} catch (ClassNotFoundException e1) {
				notify("ForeignCompilerJava: Tools.jar load failed: JavaCompiler class not found.");
			}
		} else
			notify("ForeignCompilerJava: tools.jar not found.");
		return null;
	}
	
	private static boolean initialised = false;
	private static JavaCompiler compiler = null;
	
    /** Compilation of foreign source code. */
    public void compileForeignCode(RelDatabase database, PrintStream printstream, String className, String src) {
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
        
        if (!initialised) {
       		compiler = getSystemJavaCompiler();
        	initialised = true;
        }

        if (compiler != null) {        	
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
            List<String> optionList = new ArrayList<String>();
            String classpath = 
            	System.getProperty("java.class.path") + 
            	java.io.File.pathSeparatorChar + 
            	cleanClassPath(getLocalClasspath(database));
            optionList.addAll(Arrays.asList("-classpath", classpath)); 
            compiler.getTask(null, fileManager, diagnostics, optionList, null, fileManager.getJavaFileObjects(sourcef)).call();
            String msg = "";
            for (Diagnostic<?> diagnostic : diagnostics.getDiagnostics()) {
            	msg += "Java error: " + diagnostic.getMessage(null) + ":\n" +
            			" in line " + diagnostic.getLineNumber() + " of:\n" +
                                diagnostic.getSource().toString() + "\n";
            }
        	if (msg.length() > 0)
        		throw new ExceptionSemantic("RS0004: " + msg);
        	return;				
        }
        
    	System.out.println("NOTE: A 'tools.jar' or internal Java compiler can't be found.");
    	System.out.println("      Make sure JAVA_HOME or JDK_HOME point to a JDK installation.");
    	System.out.println("      Trying to find an external javac compiler as an alternative.");
    	
    	try {
            // Compile source
            int retval = -1;
            String sourcefile = sourcef.getAbsolutePath();
            if (sourcefile.indexOf(' ')>=0)
            	sourcefile = '"' + sourcefile + '"';
           	String command = getJavacInvocation(database) + " " + sourcefile;
           	retval = ExternalExecutor.run(printstream, command);
            if (retval != 0) {
            	notify("? Compile failed.  Attempted with: " + command);
            	throw new ExceptionSemantic("RS0005: Java compilation failed due to errors.");
            }
        } catch (Throwable t) {
            throw new ExceptionSemantic("RS0289: " + t.toString());
        }
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
        
    private static Type getTypeForClassName(Generator generator, String className) {
        if (className.equals("ca.mb.armchair.rel3.values.ValueBoolean"))
            return TypeBoolean.getInstance();
        else if (className.equals("ca.mb.armchair.rel3.values.ValueCharacter"))
            return TypeCharacter.getInstance();
        else if (className.equals("ca.mb.armchair.rel3.values.ValueInteger"))
            return TypeInteger.getInstance();
        else if (className.equals("ca.mb.armchair.rel3.values.ValueRational"))
            return TypeRational.getInstance();
        else if (className.equals("ca.mb.armchair.rel3.values.ValueObject"))
            return TypeInteger.getInstance();
        else if (className.equals("ca.mb.armchair.rel3.values.ValueOperator"))
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
        String src = "\n\treturn new " + typeName + "(" + arguments + ");";  
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
            src = "\n\t" + returnStr + typeName + "." + m.getName() + "(" + arguments + ");";
    	else
            src = "\n\t" + returnStr + "px." + m.getName() + "(" + arguments + ");";
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
        String body = src;
        src = "package " + RelDatabase.getRelUserCodePackage() + ";\n" +
        	  "import ca.mb.armchair.rel3.vm.*;\n" +
              "import ca.mb.armchair.rel3.types.*;\n" + 
              "import ca.mb.armchair.rel3.types.builtin.*;\n" + 
              "import ca.mb.armchair.rel3.types.userdefined.*;\n" + 
			  "import ca.mb.armchair.rel3.values.*;\n" +
			  "import ca.mb.armchair.rel3.generator.Generator;\n" +
              "public class " + name + " extends ValueTypeJava {\n" + 
                  body +
              "\n}";
        compileForeignCode(generator.getDatabase(), generator.getPrintStream(), name, src);
        try {
        	Class<?> typeClass = generator.getDatabase().loadClass(name);
            if (typeClass == null)
                throw new ExceptionSemantic("RS0010: Despite having been compiled, " + name + " could not be loaded.");
            notify("> Java class " + typeClass.getName() + " loaded to implement type " + name);
            Constructor<?> ctor = typeClass.getConstructor(new Class[] {ca.mb.armchair.rel3.generator.Generator.class});
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
        	  "package " + RelDatabase.getRelUserCodePackage() + ";\n" +
			  "import ca.mb.armchair.rel3.types.*;\n" +
              "import ca.mb.armchair.rel3.types.builtin.*;\n" + 
              "import ca.mb.armchair.rel3.types.userdefined.*;\n" + 
			  "import ca.mb.armchair.rel3.values.*;\n" +
			  "import ca.mb.armchair.rel3.vm.Context;\n" +
              "public class " + getStrippedClassname(signature.getClassSignature()) + " {\n" +
              "private static final " + getJavaTypeForType(generator, signature.getReturnType()) + " " +
              "do_" + getStrippedName(signature.getName()) + "(" + getJavaMethodParmsForParameters(generator, signature) + ") {\n" + 
              src + "}\n" +
              "public static final void execute(Context context) {\n";
        String args = "context";
        Type[] parmTypes = signature.getParameterTypes();
        for (int i=parmTypes.length - 1; i >= 0; i--) {
            comp += "\t" + getJavaTypeForType(generator, parmTypes[i]) + " " + "p" + i + " = " + getJavaPopForType(generator, parmTypes[i]) + ";\n";
            args += ", p" + (parmTypes.length - i - 1);
        }
        if (signature.getReturnType() != null)
            comp += "\tcontext.push(" + "do_" + getStrippedName(signature.getName()) + "(" + args + "));\n";
        else
            comp += "\t" + "do_" + getStrippedName(signature.getName()) + "(" + args + ");\n";
        comp += "}\n}";
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
