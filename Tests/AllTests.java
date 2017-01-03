import java.io.*;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.reldb.rel.tests.ClassPathHack;
import org.reldb.rel.v0.version.Version;

public class AllTests {
	
	private static final String tests[] = {
		"org.reldb.rel.tests.main",
		"org.reldb.rel.tests.external",
		"org.reldb.rel.tests.inheritance",
		"org.reldb.rel.tests.ext_relvar.csv",
		"org.reldb.rel.tests.ext_relvar.xls"
	};
	
	/**
	 * Recursive method used to find all classes in a given directory and subdirs.
	 *
	 * @param directory   The base directory
	 * @param packageName The package name for classes found inside the base directory
	 * @return The classes
	 * @throws ClassNotFoundException
	 */
	private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
	    List<Class<?>> classes = new ArrayList<Class<?>>();
	    if (!directory.exists()) {
	        return classes;
	    }
	    File[] files = directory.listFiles();
	    for (File file : files) {
	        if (file.isDirectory()) {
	            assert !file.getName().contains(".");
	            classes.addAll(findClasses(file, packageName + "." + file.getName()));
	        } else if (file.getName().endsWith(".class")) {
	            classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
	        }
	    }
	    return classes;
	}
	
	private static List<Class<?>> getClassesFromDirectory(String packageName) throws ClassNotFoundException, IOException {
	    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	    assert classLoader != null;
	    String path = packageName.replace('.', '/');
	    Enumeration<URL> resources = classLoader.getResources(path);
	    List<File> dirs = new ArrayList<File>();
	    while (resources.hasMoreElements()) {
	        URL resource = resources.nextElement();
	        dirs.add(new File(resource.getFile()));
	    }
	    ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
	    for (File directory : dirs) {
	        classes.addAll(findClasses(directory, packageName));
	    }
	    return classes;
	}

	private static List<Class<?>> getClassesFromJAR(URL jarURL, String packageName) throws ClassNotFoundException, IOException {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		ZipInputStream jar = new ZipInputStream(jarURL.openStream());
	    ZipEntry ze = null;
	    while ((ze = jar.getNextEntry()) != null) {
	        String entryName = ze.getName().replace('/', '.');
	        if (entryName.startsWith(packageName) && entryName.endsWith(".class")) {
	        	String className = entryName.substring(0, entryName.length() - 6);
	            classes.add(Class.forName(className));
	        }
	    }
	    return classes;
	}
	
	/**
	 * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
	 *
	 * @param packageName The base package
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private static List<Class<?>> getClasses(URL jar, String packageName) throws ClassNotFoundException, IOException {
		if (jar == null)
			return getClassesFromDirectory(packageName);
		else
			return getClassesFromJAR(jar, packageName);
	}
	
	// As above, but get ArrayList of class names.
	private static ArrayList<String> getClassNames(URL jar, String packageName) throws ClassNotFoundException, IOException {
		List<Class<?>> classes = getClasses(jar, packageName);
		ArrayList<String> names = new ArrayList<String>();
		for (Class<?> klass: classes)
			names.add(klass.getName());
		return names;
	}
	
	public static void main(String args[]) {
		try {
			ClassPathHack.addFile("lib/junit.jar");
			ClassPathHack.addFile("lib/commons-cli-1.2.jar");
			ClassPathHack.addFile("lib/commons-codec-1.10.jar");
			ClassPathHack.addFile("lib/commons-collections4-4.1.jar");
			ClassPathHack.addFile("lib/commons-lang-2.6.jar");
			ClassPathHack.addFile("lib/commons-logging-1.2.jar");
			ClassPathHack.addFile("lib/curvesapi-1.04.jar");
			ClassPathHack.addFile("lib/ecj-4.6.1.jar");
			ClassPathHack.addFile("lib/fluent-hc-4.5.2.jar");
			ClassPathHack.addFile("lib/httpclient-4.5.2.jar");
			ClassPathHack.addFile("lib/httpclient-cache-4.5.2.jar");
			ClassPathHack.addFile("lib/httpclient-win-4.5.2.jar");
			ClassPathHack.addFile("lib/httpcore-4.4.4.jar");
			ClassPathHack.addFile("lib/httpcore-4.4.5.jar");
			ClassPathHack.addFile("lib/httpcore-ab-4.4.5.jar");
			ClassPathHack.addFile("lib/httpcore-nio-4.4.5.jar");
			ClassPathHack.addFile("lib/httpmime-4.5.2.jar");
			ClassPathHack.addFile("lib/jackcess-2.1.6.jar");
			ClassPathHack.addFile("lib/jna-4.1.0.jar");
			ClassPathHack.addFile("lib/jna-platform-4.1.0.jar");
			ClassPathHack.addFile("lib/junit.jar");
			ClassPathHack.addFile("lib/log4j-1.2.17.jar");
			ClassPathHack.addFile("lib/mariadb-java-client-1.5.6.jar");
			ClassPathHack.addFile("lib/ojdbc7.jar");
			ClassPathHack.addFile("lib/poi-3.15.jar");
			ClassPathHack.addFile("lib/poi-excelant-3.15.jar");
			ClassPathHack.addFile("lib/poi-ooxml-3.15.jar");
			ClassPathHack.addFile("lib/poi-ooxml-schemas-3.15.jar");
			ClassPathHack.addFile("lib/poi-scratchpad-3.15.jar");
			ClassPathHack.addFile("lib/postgresql-9.4.1212.jar");
			ClassPathHack.addFile("lib/rel0000.jar");
			ClassPathHack.addFile("lib/relclient.jar");
			ClassPathHack.addFile("lib/relshared.jar");
			ClassPathHack.addFile("lib/jtds-1.3.1.jar");
			ClassPathHack.addFile("lib/xmlbeans-2.6.0.jar");
			ClassPathHack.addFile("lib/" + Version.getBerkeleyDbJarFilename());
		} catch (IOException ioe) {
			System.out.println(ioe.toString());
			return;
		}
		
		ArrayList<String> classes = new ArrayList<String>();
		
		try {
			CodeSource src = AllTests.class.getProtectionDomain().getCodeSource();
			URL jarURL = null;
			if (src != null && src.getLocation().toString().toUpperCase().endsWith(".JAR"))
				jarURL = src.getLocation();
			for (String testMe: tests)
				classes.addAll(getClassNames(jarURL, testMe));
		} catch (ClassNotFoundException e) {
			System.out.println(e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println(e.toString());
			e.printStackTrace();
		}
		
		System.out.println("Rel tests are running.  Please wait.");
		
		org.junit.runner.JUnitCore.main(classes.toArray(new String[0]));
	}
}
