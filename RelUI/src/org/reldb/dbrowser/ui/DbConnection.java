package org.reldb.dbrowser.ui;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;

import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.reldb.rel.client.Connection;
import org.reldb.rel.client.Tuples;
import org.reldb.rel.client.Value;
import org.reldb.rel.client.Connection.HTMLReceiver;
import org.reldb.rel.client.connection.CrashHandler;

public class DbConnection {
	public static final int QUERY_WAIT_MILLISECONDS = 5000;

	private Connection connection;

	private static String[] bundleJarPath = null;
	
	public static String[] getBundleJarPath(Class<?> klass) {
		if (bundleJarPath == null)
			try {
				Bundle bundle = FrameworkUtil.getBundle(klass);
				if (bundle == null) {
					System.out.println("DbConnection: Unable to retrieve bundle containing '" + klass + "', so some functionality might be unavailable.");
					return null;
				}
				Vector<String> jarPaths = new Vector<String>();
				System.out.println("DbConnection: Search for Rel core JAR files...");
				Enumeration<URL> urls = bundle.findEntries("/lib", "rel0*", true);
				if (urls != null)
					while (urls.hasMoreElements()) {
						URL fileURL = FileLocator.toFileURL(urls.nextElement());
						File file = new File(fileURL.toURI());
						System.out.println("DbConnection: found " + file.getPath());
						jarPaths.add(file.getAbsolutePath());
					}
				else
					System.out.println("DbConnection: found nothing.");
				bundleJarPath = jarPaths.toArray(new String[0]);
			} catch (Exception e) {
				System.out.println("DbConnection: Error in getBundleJarPath: " + e);
				e.printStackTrace();
				return null;
			}
		return bundleJarPath;
	}
	
	public DbConnection(String dbURL, CrashHandler crashHandler) {
		connection = new Connection(dbURL, false, crashHandler, getBundleJarPath(getClass()));
	}
	
	public boolean execute(String query) {
		try {
			connection.execute(query);
			return true;
		} catch (IOException e1) {
			System.out.println("DbTab: Error: " + e1);
			e1.printStackTrace();
			return false;
		}
	}

	public Tuples getTuples(String query) {
		Value response;
		try {
			response = connection.evaluate(query).awaitResult(QUERY_WAIT_MILLISECONDS);
		} catch (IOException e) {
			System.out.println("RelPanel: Error: " + e);
			e.printStackTrace();
			return null;
		}
		if (response instanceof org.reldb.rel.client.Error) {
			System.out.println("RelPanel: Query returns error. " + query + "\n");
			return null;
		}
		if (response == null) {
			System.out.println("RelPanel: Unable to obtain query results.");
			return null;
		}
		return (Tuples)response;		
	}

	public void evaluate(String query, HTMLReceiver htmlReceiver) {
		connection.evaluate(query, htmlReceiver);
	}

}
