package org.reldb.rel.tests;

/*
 * From http://forum.java.sun.com/thread.jsp?forum=32&thread=300557
 */

import java.lang.reflect.*;
import java.io.*;
import java.net.*;

public class ClassPathHack {

	private static final Class<?>[] parameters = new Class[] {URL.class};

	private static boolean osgiTested = false;
	private static boolean isInOSGI = false;
	
	private static boolean isInOSGI() {
		if (osgiTested)
			return isInOSGI;
		try {
			Class.forName("org.osgi.framework.BundleReference");
			isInOSGI = true;
		} catch (ClassNotFoundException e) {
		}
		osgiTested = true;
		return isInOSGI;
	}
	
	public static void addFile(String s) throws IOException {
		if (isInOSGI())
			return;
		File f = new File(s);
		addFile(f);
	}
	
	public static void addFile(File f) throws IOException {
		if (isInOSGI())
			return;
		if (!f.exists())
			System.out.println("Warning: can't find " + f + " so some functionality may be disabled.");
		URI uri = f.toURI();
		URL url = uri.toURL();
		addURL(url);
	}
	
	public static void addURL(URL u) throws IOException {
		if (isInOSGI())
			return;
		URLClassLoader sysloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
		Class<URLClassLoader> sysclass = URLClassLoader.class;	
		try {
			Method method = sysclass.getDeclaredMethod("addURL", parameters);
			method.setAccessible(true);
			method.invoke(sysloader, new Object[] {u});
		} catch (Throwable t) {
			t.printStackTrace();
			throw new IOException("Error, could not add URL to system classloader");
		}
	}

}