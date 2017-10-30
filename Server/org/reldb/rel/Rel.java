package org.reldb.rel;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import org.reldb.rel.exceptions.DatabaseFormatVersionException;
import org.reldb.rel.v0.version.Version;

/** Convenient access point for running an embedded or stand-alone interpreter. */

public class Rel {

	private org.reldb.rel.v0.engine.Rel rel;
	
	private static boolean classpathBuilt = false;
	
	private static void buildClasspath() throws IOException {
		if (classpathBuilt)
			return;
		org.reldb.rel.ClassPathHack.addFile("lib/commons-cli-1.2.jar");
		org.reldb.rel.ClassPathHack.addFile("lib/commons-codec-1.10.jar");
		org.reldb.rel.ClassPathHack.addFile("lib/commons-collections4-4.1.jar");
		org.reldb.rel.ClassPathHack.addFile("lib/commons-lang-2.6.jar");
		org.reldb.rel.ClassPathHack.addFile("lib/commons-logging-1.2.jar");
		org.reldb.rel.ClassPathHack.addFile("lib/curvesapi-1.04.jar");
		org.reldb.rel.ClassPathHack.addFile("lib/ecj-4.6.1.jar");
		org.reldb.rel.ClassPathHack.addFile("lib/fluent-hc-4.5.2.jar");
		org.reldb.rel.ClassPathHack.addFile("lib/httpclient-4.5.2.jar");
		org.reldb.rel.ClassPathHack.addFile("lib/httpclient-cache-4.5.2.jar");
		org.reldb.rel.ClassPathHack.addFile("lib/httpclient-win-4.5.2.jar");
		org.reldb.rel.ClassPathHack.addFile("lib/httpcore-4.4.4.jar");
		org.reldb.rel.ClassPathHack.addFile("lib/httpcore-4.4.5.jar");
		org.reldb.rel.ClassPathHack.addFile("lib/httpcore-ab-4.4.5.jar");
		org.reldb.rel.ClassPathHack.addFile("lib/httpcore-nio-4.4.5.jar");
		org.reldb.rel.ClassPathHack.addFile("lib/httpmime-4.5.2.jar");
		org.reldb.rel.ClassPathHack.addFile("lib/jackcess-2.1.6.jar");
		org.reldb.rel.ClassPathHack.addFile("lib/jna-4.1.0.jar");
		org.reldb.rel.ClassPathHack.addFile("lib/jna-platform-4.1.0.jar");
		org.reldb.rel.ClassPathHack.addFile("lib/junit.jar");
		org.reldb.rel.ClassPathHack.addFile("lib/log4j-1.2.17.jar");
		org.reldb.rel.ClassPathHack.addFile("lib/mariadb-java-client-1.5.6.jar");
		org.reldb.rel.ClassPathHack.addFile("lib/ojdbc7.jar");
		org.reldb.rel.ClassPathHack.addFile("lib/poi-3.15.jar");
		org.reldb.rel.ClassPathHack.addFile("lib/poi-excelant-3.15.jar");
		org.reldb.rel.ClassPathHack.addFile("lib/poi-ooxml-3.15.jar");
		org.reldb.rel.ClassPathHack.addFile("lib/poi-ooxml-schemas-3.15.jar");
		org.reldb.rel.ClassPathHack.addFile("lib/poi-scratchpad-3.15.jar");
		org.reldb.rel.ClassPathHack.addFile("lib/postgresql-9.4.1212.jar");
		org.reldb.rel.ClassPathHack.addFile("lib/rel0000.jar");
		org.reldb.rel.ClassPathHack.addFile("lib/relclient.jar");
		org.reldb.rel.ClassPathHack.addFile("lib/relshared.jar");
		org.reldb.rel.ClassPathHack.addFile("lib/jtds-1.3.1.jar");
		org.reldb.rel.ClassPathHack.addFile("lib/xmlbeans-2.6.0.jar");
		org.reldb.rel.ClassPathHack.addFile("lib/" + Version.getBerkeleyDbJarFilename());	// dependent on rel0000.jar!
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
	
	public void sendEvaluate(String source) throws Throwable {
		rel.sendEvaluate(source);
	}
	
	public void sendExecute(String source) throws Throwable {
		rel.sendExecute(source);
	}

	public void reset() {
		rel.reset();
	}

	public void close() {
		rel.close();
	}
}
