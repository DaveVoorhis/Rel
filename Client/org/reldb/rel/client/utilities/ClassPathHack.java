package org.reldb.rel.client.utilities;

/*
 * From http://forum.java.sun.com/thread.jsp?forum=32&thread=300557
 */

import java.lang.reflect.*;
import java.io.*;
import java.net.*;

public class ClassPathHack {

	private static final Class<?>[] parameters = new Class[] {URL.class};
	
	public static void addFile(String s) throws IOException {
		File f = new File(s);
		addFile(f);
	}
	
	public static void addFile(File f) throws IOException {
		URI uri = f.toURI();
		URL url = uri.toURL();
		addURL(url);
	}
	
	public static void addURL(URL u) throws IOException {
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