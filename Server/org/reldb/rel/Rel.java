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
		ClassPathHack.addFile("relshared.jar");
		ClassPathHack.addFile("rel0000.jar");		
		ClassPathHack.addFile("ecj-4.4.2.jar");
		ClassPathHack.addFile("commons-codec-1.4.jar");
		ClassPathHack.addFile("commons-configuration-1.6.jar");
		ClassPathHack.addFile("commons-lang-2.4.jar");
		ClassPathHack.addFile("commons-logging-1.1.1.jar");
		ClassPathHack.addFile("dom4j-1.6.1.jar");
		ClassPathHack.addFile("hadoop-core-1.1.2.jar");
		ClassPathHack.addFile("hbase-0.94.9.jar");
		ClassPathHack.addFile("httpclient-4.1.3.jar");
		ClassPathHack.addFile("httpclient-cache-4.1.3.jar");
		ClassPathHack.addFile("httpcore-4.1.4.jar");
		ClassPathHack.addFile("httpmime-4.1.3.jar");
		ClassPathHack.addFile("log4j-1.2.15.jar");
		ClassPathHack.addFile("mysql-connector-java-5.1.25-bin.jar");
		ClassPathHack.addFile("poi-3.9-20121203.jar");
		ClassPathHack.addFile("poi-ooxml-3.9-20121203.jar");
		ClassPathHack.addFile("poi-ooxml-schemas-3.9-20121203.jar");
		ClassPathHack.addFile("protobuf-java-2.2.0.jar");
		ClassPathHack.addFile("slf4j-api-1.4.3.jar");
		ClassPathHack.addFile("slf4j-log4j12-1.4.3.jar");
		ClassPathHack.addFile("stax-api-1.0.1.jar");
		ClassPathHack.addFile("xmlbeans-2.3.0.jar");
		ClassPathHack.addFile("zookeeper-3.4.5.jar");
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
