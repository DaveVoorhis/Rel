package org.reldb.rel;

import java.io.IOException;
import java.io.InputStream;

/** Convenient access point for running an embedded or stand-alone interpreter. */

public class Rel {

	private org.reldb.rel.v0.engine.Rel rel;
	
	private static void buildClasspath() throws IOException {
		ClassPathHack.addFile("relshared.jar");
		ClassPathHack.addFile("rel0000.jar");		
	}
	
	/** Convenient runner for a stand-alone Rel interpreter. 
	 * @throws IOException */
	public static void main(String[] args) throws IOException {
		buildClasspath();
		org.reldb.rel.v0.engine.Rel.main(args);
	}
	
	/** Establish a connection with this server. */
	public Rel(String databaseDir, boolean createDbAllowed) throws IOException {
		buildClasspath();
		rel = new org.reldb.rel.v0.engine.Rel(databaseDir, createDbAllowed);
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
	
}
