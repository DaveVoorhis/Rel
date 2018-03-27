package org.reldb.dbrowser.ui;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.reldb.rel.client.Attribute;
import org.reldb.rel.client.Connection;
import org.reldb.rel.client.Error;
import org.reldb.rel.client.Heading;
import org.reldb.rel.client.NullTuples;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;
import org.reldb.rel.client.Value;
import org.reldb.rel.client.Connection.ExecuteResult;
import org.reldb.rel.client.Connection.HTMLReceiver;
import org.reldb.rel.client.connection.CrashHandler;
import org.reldb.rel.client.connection.string.ClientFromURL;
import org.reldb.rel.client.connection.string.StringReceiverClient;
import org.reldb.rel.exceptions.DatabaseFormatVersionException;

public class DbConnection {
	public static final int QUERY_WAIT_MILLISECONDS = 5000;

	private Connection connection;

	private static String[] bundleJarPath = null;
	
	private static String[] getBundleJarPath(Class<?> klass) {
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
						File file = new File(fileURL.getFile());
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
	
	public DbConnection(String dbURL, boolean createDatabaseIfNotExists, CrashHandler crashHandler) throws NumberFormatException, MalformedURLException, IOException, DatabaseFormatVersionException {
		connection = new Connection(dbURL, createDatabaseIfNotExists, crashHandler, getBundleJarPath(getClass()));		
	}
	
	public DbConnection(String dbURL, CrashHandler crashHandler) throws NumberFormatException, MalformedURLException, IOException, DatabaseFormatVersionException {
		this(dbURL, false, crashHandler);
	}

	private static class Bundler {
	}
	
	public static void convertToLatestFormat(String dbURL, PrintStream conversionOutput) throws DatabaseFormatVersionException, IOException {
		Connection.convertToLatestFormat(dbURL, conversionOutput, getBundleJarPath((new Bundler()).getClass()));
	}

	public String getDbURL() {
		return connection.getDbURL();
	}

	public StringReceiverClient obtainStringReceiverClient() {
		try {
			return ClientFromURL.openConnection(connection.getDbURL(), false, connection.getCrashHandler(), connection.getAdditionalJars());
		} catch (Exception e) {
			System.out.println("DbConnection: Unable to obtain StringReceiverClient for a live DbConnection: " + e);
			return null;
		}
	}
		
	public ExecuteResult execute(String query) {
		return connection.exec(query);
	}
	
	public Tuples getTuples(String query) {
		return connection.getTuples(query, QUERY_WAIT_MILLISECONDS);
	}

	public void evaluate(String query, HTMLReceiver htmlReceiver) {
		connection.evaluate(query, htmlReceiver);
	}

	public Value evaluate(String query) {
		try {
			Value result = connection.evaluate(query).awaitResult(QUERY_WAIT_MILLISECONDS);
			if (result instanceof Error) {
				MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", result.toString());
				return new NullTuples();
			}
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			return new NullTuples();
		}
	}
	
	public int hasRevExtensions() {
		String query = "sys.rev.Version";
		try {
			Value response = (Value)connection.evaluate(query).awaitResult(QUERY_WAIT_MILLISECONDS);
			if (response instanceof Tuples) {
				int version = -1;
				for (Tuple tuple: (Tuples)response)
					version = tuple.get("ver").toInt();
				return version;
			}
			return -1;
		} catch (IOException e) {
			System.out.println("Unable to obtain version of Rev extensions.  Are they not installed?");
			return -1;
		}
	}

	private String[] keywordCache = null;
	
	public String[] getKeywords() {
		if (keywordCache == null) {
			String query = "sys.Keywords";
			try {
				System.out.println("Obtaining keyword list from database.");
				Value response = (Value)connection.evaluate(query).awaitResult(QUERY_WAIT_MILLISECONDS);
				if (response instanceof Tuples) {
					Vector<String> keywords = new Vector<String>();
					for (Tuple tuple: (Tuples)response) {
						String keyword = tuple.get("Keyword").toString();
						keywords.add(keyword);
					}
					keywordCache = keywords.toArray(new String[0]);
				} else
					keywordCache = new String[0];
			} catch (IOException e) {
				System.out.println("Unable to obtain keywords from database.");
				keywordCache = new String[0];				
			}
		}
		return keywordCache;
	}

	public Vector<String> getAttributesOf(String query) {
		Vector<String> attributeNames = new Vector<String>();		
		try (Connection conn = new Connection(connection.getDbURL())) {
			Tuples tuples = conn.getTuples("REL SAME_HEADING_AS(" + query + ") {}", QUERY_WAIT_MILLISECONDS);
			Heading heading = tuples.getHeading();
			Iterator<Attribute> attributes = heading.getAttributes();
			while (attributes.hasNext())
				attributeNames.add(attributes.next().getName());
		} catch (Exception e) {
			System.out.println("Unable to obtain attributes for " + query);
		}
		return attributeNames;
	}

}
