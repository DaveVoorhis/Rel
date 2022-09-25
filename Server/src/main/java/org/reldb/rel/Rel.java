package org.reldb.rel;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import org.reldb.rel.exceptions.DatabaseFormatVersionException;

/** Convenient access point for running an embedded or stand-alone interpreter. */

public class Rel {

	private org.reldb.rel.v0.engine.Rel rel;
	
	/** Convenient runner for a stand-alone Rel interpreter. 
	 * @throws IOException */
	public static void main(String[] args) throws IOException {
		org.reldb.rel.v0.engine.Rel.main(args);
	}
	
	/** Convert this database to the latest format, if necessary.  Throw exception if not necessary.  Normally only needed if invoking
	 * the constructor throws DatabaseFormatVersionException. */
	public static void convertToLatestFormat(String databaseDir, PrintStream conversionOutput, String[] additionalJars) throws DatabaseFormatVersionException, IOException {
		org.reldb.rel.v0.engine.Rel.convertToLatestFormat(databaseDir, conversionOutput, additionalJars);
	}
	
	/** Establish a connection with this server. */
	public Rel(String databaseDir, boolean createDbAllowed, String[] additionalJars) throws IOException, DatabaseFormatVersionException {
		rel = new org.reldb.rel.v0.engine.Rel(databaseDir, createDbAllowed, additionalJars);
	}
	
	public InputStream getServerResponseInputStream() throws IOException {
		return rel.getServerResponseInputStream();
	}
	
	public void sendEvaluate(String source) throws Throwable {
		if (source == null)
			throw new IllegalArgumentException("Attempt to evaluate null source code.");
		rel.sendEvaluate(source);
	}
	
	public void sendExecute(String source) throws Throwable {
		if (source == null)
			throw new IllegalArgumentException("Attempt to execute null source code.");
		rel.sendExecute(source);
	}

	public void reset() {
		rel.reset();
	}

	public void close() {
		rel.close();
	}
}
