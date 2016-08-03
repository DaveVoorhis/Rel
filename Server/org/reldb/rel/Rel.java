package org.reldb.rel;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import org.reldb.rel.exceptions.DatabaseFormatVersionException;

/** Convenient access point for running an embedded or stand-alone interpreter. */

public class Rel {

	private org.reldb.rel.v0.engine.Rel rel;
	
	private static void buildClasspath() throws IOException {
		ClassPathHack.addFile("relshared.jar");
		ClassPathHack.addFile("rel0000.jar");		
		ClassPathHack.addFile("ecj-4.4.2.jar");
	}
	
	/** Convenient runner for a stand-alone Rel interpreter. 
	 * @throws IOException */
	public static void main(String[] args) throws IOException {
		buildClasspath();
		org.reldb.rel.v0.engine.Rel.main(args);
	}
	
	/** Convert this database to the latest format, if necessary.  Throw exception if not necessary.  Normally only needed if invoking
	 * the constructor throws DatabaseFormatVersionException. */
	public static void convertToLatestFormat(String databaseDir, PrintStream conversionOutput, String[] additionalJars) throws DatabaseFormatVersionException, IOException {
		buildClasspath();
		org.reldb.rel.v0.engine.Rel.convertToLatestFormat(databaseDir, conversionOutput, additionalJars);
	}
	
	/** Establish a connection with this server. */
	public Rel(String databaseDir, boolean createDbAllowed, String[] additionalJars) throws IOException, DatabaseFormatVersionException {
		buildClasspath();
		rel = new org.reldb.rel.v0.engine.Rel(databaseDir, createDbAllowed, additionalJars);
	}
	
	public InputStream getServerResponseInputStream() throws IOException {
		return rel.getServerResponseInputStream();
	}
	
	public void sendEvaluate(String source) throws IOException {
		rel.sendEvaluate(source);
	}
	
	public void sendExecute(String source) throws IOException {
		rel.sendExecute(source);
	}

	public void reset() {
		rel.reset();
	}

	public void close() {
		rel.close();
	}
}
