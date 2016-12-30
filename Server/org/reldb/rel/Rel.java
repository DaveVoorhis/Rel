package org.reldb.rel;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import org.reldb.rel.exceptions.DatabaseFormatVersionException;

/** Convenient access point for running an embedded or stand-alone interpreter. */

public class Rel {

	private org.reldb.rel.v0.engine.Rel rel;
	
	private static boolean classpathBuilt = false;
	
	private static void buildClasspath() throws IOException {
		if (classpathBuilt)
			return;
		ClassPathHack.addFile("lib/commons-cli-1.2.jar");
		ClassPathHack.addFile("lib/commons-codec-1.10.jar");
		ClassPathHack.addFile("lib/commons-collections4-4.1.jar");
		ClassPathHack.addFile("lib/commons-lang-2.6.jar");
		ClassPathHack.addFile("lib/commons-logging-1.2.jar");
		ClassPathHack.addFile("lib/curvesapi-1.04.jar");
		ClassPathHack.addFile("lib/ecj-4.6.1.jar");
		ClassPathHack.addFile("lib/fluent-hc-4.5.2.jar");
		ClassPathHack.addFile("lib/httpclient-4.5.2.jar");
		ClassPathHack.addFile("lib/httpclient-cache-4.5.2.jar");
		ClassPathHack.addFile("lib/httpclient-win-4.5.2.jar");
		ClassPathHack.addFile("lib/httpcore-4.4.4.jar");
		ClassPathHack.addFile("lib/httpcore-4.4.5.jar");
		ClassPathHack.addFile("lib/httpcore-ab-4.4.5.jar");
		ClassPathHack.addFile("lib/httpcore-nio-4.4.5.jar");
		ClassPathHack.addFile("lib/httpmime-4.5.2.jar");
		ClassPathHack.addFile("lib/jackcess-2.1.6.jar");
		ClassPathHack.addFile("lib/je-7.0.6.jar");
		ClassPathHack.addFile("lib/jna-4.1.0.jar");
		ClassPathHack.addFile("lib/jna-platform-4.1.0.jar");
		ClassPathHack.addFile("lib/junit.jar");
		ClassPathHack.addFile("lib/log4j-1.2.17.jar");
		ClassPathHack.addFile("lib/mysql-connector-java-5.1.40-bin.jar");
		ClassPathHack.addFile("lib/ojdbc7.jar");
		ClassPathHack.addFile("lib/poi-3.15.jar");
		ClassPathHack.addFile("lib/poi-excelant-3.15.jar");
		ClassPathHack.addFile("lib/poi-ooxml-3.15.jar");
		ClassPathHack.addFile("lib/poi-ooxml-schemas-3.15.jar");
		ClassPathHack.addFile("lib/poi-scratchpad-3.15.jar");
		ClassPathHack.addFile("lib/postgresql-9.4.1212.jar");
		ClassPathHack.addFile("lib/rel0000.jar");
		ClassPathHack.addFile("lib/relclient.jar");
		ClassPathHack.addFile("lib/relshared.jar");
		ClassPathHack.addFile("lib/sqljdbc42.jar");
		ClassPathHack.addFile("lib/xmlbeans-2.6.0.jar");
		classpathBuilt = true;
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
