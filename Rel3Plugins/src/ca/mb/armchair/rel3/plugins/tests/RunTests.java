package ca.mb.armchair.rel3.plugins.tests;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import ca.mb.armchair.rel3.tests.ClassPathHack;

public class RunTests {

	private static final String tests[] = { "ca.mb.armchair.rel3.plugins.tests.CSV",
											"ca.mb.armchair.rel3.plugins.tests.JDBC",
											"ca.mb.armchair.rel3.plugins.tests.XLS" };

	public static void main(String[] args) {
		try {
			ClassPathHack.addFile("commons-configuration-1.6.jar");
			ClassPathHack.addFile("commons-lang-2.4.jar");
			ClassPathHack.addFile("commons-logging-1.1.1.jar");
			ClassPathHack.addFile("hadoop-core-1.1.2.jar");
			ClassPathHack.addFile("habse-0.94.9.jar");
			ClassPathHack.addFile("log4j-1.2.15.jar");
			ClassPathHack.addFile("protobuf-java-2.2.0.jar");
			ClassPathHack.addFile("slf4j-api-1.4.3.jar");
			ClassPathHack.addFile("slf4j-log4j12-1.4.3.jar");
			ClassPathHack.addFile("zookeeper-3.4.5.jar");
			ClassPathHack.addFile("dom4j-1.6.1.jar");
			ClassPathHack.addFile("poi-3.9-20121203.jar");
			ClassPathHack.addFile("poi-ooxml-3.9-20121203.jar");
			ClassPathHack.addFile("stax-api-1.0.1jar");
			ClassPathHack.addFile("xmlbenas-2.3.0.jar");
		} catch (IOException ioe) {
			System.out.println(ioe.toString());
			return;
		}

		ArrayList<String> classes = new ArrayList<String>();

		try {
			CodeSource src = RunTests.class.getProtectionDomain().getCodeSource();
			URL jarURL = null;
			if (src != null && src.getLocation().toString().toUpperCase().endsWith(".JAR"))
				jarURL = src.getLocation();
			for (String testMe : tests)
				classes.addAll(getClassNames(jarURL, testMe));
		} catch (ClassNotFoundException e) {
			System.out.println(e.toString());
		} catch (IOException e) {
			System.out.println(e.toString());
		}

		System.out.println("Plugin tests are running.  Please wait.");

		org.junit.runner.JUnitCore.main(classes.toArray(new String[0]));
	}

	private static ArrayList<String> getClassNames(URL jar, String packageName) throws ClassNotFoundException, IOException {
		List<Class<?>> classes = getClasses(jar, packageName);
		ArrayList<String> names = new ArrayList<String>();
		for (Class<?> klass : classes)
			names.add(klass.getName());
		return names;
	}

	private static List<Class<?>> getClasses(URL jar, String packageName) throws ClassNotFoundException, IOException {
		if (jar == null)
			return getClassesFromDirectory(packageName);
		else
			return getClassesFromJAR(jar, packageName);
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
		for (File directory : dirs)
			classes.addAll(findClasses(directory, packageName));
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

	private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		if (!directory.exists())
			return classes;
		File[] files = directory.listFiles();
		for (File file : files)
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file, packageName + "." + file.getName()));
			} else if (file.getName().endsWith(".class"))
				classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
		return classes;
	}
}
