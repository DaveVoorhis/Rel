package ca.mb.armchair.rel3.interpreter;
/*
 * From http://forum.java.sun.com/thread.jsp?forum=32&thread=300557
 */

import java.lang.reflect.*;
import java.io.*;
import java.net.*;

public class ClassPathHack {

	@SuppressWarnings("rawtypes")
	private static final Class[] parameters = new Class[]{URL.class};
	
	public static void addFile(String s) throws IOException {
		File f = new File(s);
		addFile(f);
	}
	
	@SuppressWarnings("deprecation")
	public static void addFile(File f) throws IOException {
		addURL(f.toURL());
	}
	
	@SuppressWarnings("unchecked")
	public static void addURL(URL u) throws IOException {
			
		URLClassLoader sysloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
		@SuppressWarnings("rawtypes")
		Class sysclass = URLClassLoader.class;
	
		try {
			Method method = sysclass.getDeclaredMethod("addURL",parameters);
			method.setAccessible(true);
			method.invoke(sysloader,new Object[]{ u });
		} catch (Throwable t) {
				t.printStackTrace();
				throw new IOException("Error, could not add URL to system classloader");
		}			
	}
		
}