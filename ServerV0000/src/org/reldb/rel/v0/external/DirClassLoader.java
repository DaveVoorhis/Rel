/*
 * DirClassLoader.java
 *
 * Created on 21 August 2004, 21:02
 */

package org.reldb.rel.v0.external;

import java.io.*;
import java.util.*;

import org.reldb.rel.exceptions.*;

/**
 * A class loader to load named classes from a specified directory.  With
 * class unload and class caching.
 *
 * @author  dave
 */
public class DirClassLoader extends ClassLoader {

	private static HashMap<String, Class<?>> loaded = new HashMap<String, Class<?>>();

	private String dir;

	public DirClassLoader(String dir) {
		this.dir = dir;
	}
	
	/** Unload a given Class. */
	public void unload(String name) {
		loaded.remove(name);
	}

	public Class<?> findClass(String name) {
		try {
			return Class.forName(name);
		} catch (ClassNotFoundException cnfe) {
			Class<?> c = (Class<?>) loaded.get(name);
			if (c == null) {
				byte[] b = loadClassData(name);
				c = defineClass(name, b, 0, b.length);
				loaded.put(name, c);
			}
			return c;
		}
	}

	protected synchronized Class<?> loadClass(String name, boolean resolve)
			throws ClassNotFoundException {
		Class<?> c = findClass(name);
		if (resolve)
			resolveClass(c);
		return c;
	}

	private File getClassFileName(String name) {
		name = name.replace('.', File.separatorChar);
		if (dir.endsWith(File.separator))
			return new File(dir + name + ".class");
		else
			return new File(dir + File.separator + name + ".class");
	}
	
	private byte[] loadClassData(String name) {
		File f = getClassFileName(name);
		BytestreamOutputArray bytes = new BytestreamOutputArray();
		try {
			FileInputStream reader = new FileInputStream(f);
			byte[] b = new byte[65535];
			while (true) {
				int read = reader.read(b);
				if (read < 0)
					break;
				bytes.put(b, 0, read);
			}
			reader.close();
		} catch (FileNotFoundException fnfe) {
			throw new ExceptionFatal("RS0290: File " + f + " not found.");
		} catch (IOException ioe) {
			throw new ExceptionFatal("RS0291: Error reading " + f + ": " + ioe.toString());
		}
		return bytes.getBytes();
	}		
	
	/** Get Class for given name.  Will check the system loader first, then the specified directory. */
	public Class<?> forName(final String name) throws ClassNotFoundException {
		// Creation of new ClassLoader allows same class name to be reloaded, as when user
		// drops and then re-creates a given user-defined Java-based type.
		return new DirClassLoader(dir).loadClass(name);
	}

}
