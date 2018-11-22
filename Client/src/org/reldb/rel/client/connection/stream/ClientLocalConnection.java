package org.reldb.rel.client.connection.stream;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.reldb.rel.client.connection.CrashHandler;
import org.reldb.rel.exceptions.DatabaseFormatVersionException;
//import org.reldb.rel.Rel;

public class ClientLocalConnection extends ClientConnection {
	
	protected CrashHandler errorHandler;
	
	// private Rel rel;
	private Object rel;
	
	// Dynamically bound methods allow us to deploy a standalone DBrowser for only connecting to remote databases.
	// Also avoids cyclic dependency.
	private Method getServerResponseInputStream;
	private Method sendEvaluate;
	private Method sendExecute;
	private Method close;
	private Method reset;
	private static Method convertToLatestFormat;
	
	/** Establish a connection with a local server. */
	public ClientLocalConnection(String databaseDir, boolean createDbAllowed, CrashHandler errorHandler, String[] additionalJars) throws IOException, ClassNotFoundException, DatabaseFormatVersionException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		this.errorHandler = errorHandler;
		Class<?> relClass = Class.forName("org.reldb.rel.Rel");
		Constructor<?> relCtor = relClass.getConstructor(String.class, boolean.class, String[].class);
		// rel = new Rel(databaseDir, createDbAllowed, additionalJars);
		rel = relCtor.newInstance(databaseDir, createDbAllowed, additionalJars);
		getServerResponseInputStream = relClass.getMethod("getServerResponseInputStream", (Class<?>[])null);
		sendEvaluate = relClass.getMethod("sendEvaluate", new Class<?>[] {String.class});
		sendExecute = relClass.getMethod("sendExecute", new Class<?>[] {String.class});
		close = relClass.getMethod("close", (Class<?>[])null);
		reset = relClass.getMethod("reset", (Class<?>[])null);
		convertToLatestFormat = relClass.getMethod("convertToLatestFormat", new Class<?>[] {String.class, PrintStream.class, String[].class});
		obtainInitialServerResponse();
		errorHandler.setInitialServerResponse(initialServerResponse.toString());
	}
	
	public InputStream getServerResponseInputStream() throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		// return rel.getServerResponseInputStream();
		return (InputStream)getServerResponseInputStream.invoke(rel, (Object [])null);
	}
	
	public void sendEvaluate(String source) {
		try {
			// rel.sendEvaluate(source);
			sendEvaluate.invoke(rel, source);
		} catch (Throwable t) {
			reset();
			errorHandler.process(t, source);
		}
	}
	
	public void sendExecute(String source) {
		try {
			// rel.sendExecute(source);
			sendExecute.invoke(rel, source);
		} catch (Throwable t) {
			reset();
			errorHandler.process(t, source);
		}
	}
	
	public void close() throws IOException {
		// rel.close();
		try {
			close.invoke(rel, (Object [])null);
		} catch (Throwable t) {
			reset();
			errorHandler.process(t, "");
		}
	}

	public void reset() {
		// rel.reset();
		try {
			reset.invoke(rel, (Object [])null);
		} catch (Throwable t) {
			errorHandler.process(t, "");
		}
	}

	public static void convertToLatestFormat(String dbURL, PrintStream conversionOutput, String[] additionalJars) throws DatabaseFormatVersionException, IOException {
		// Rel.convertToLatestFormat(dbURL, conversionOutput, additionalJars);
		try {
			convertToLatestFormat.invoke(null, dbURL, conversionOutput, additionalJars);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
}
