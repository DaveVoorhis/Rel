import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class AllTests {
	
	private static final String tests[] = {
		"org.reldb.rel.tests.main",
		"org.reldb.rel.tests.external",
		"org.reldb.rel.tests.inheritance",
		"org.reldb.rel.tests.ext_relvar.csv",
		"org.reldb.rel.tests.ext_relvar.xls",
		"org.reldb.rel.tests.ext_relvar.jdbc"
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
