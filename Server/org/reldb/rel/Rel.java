package org.reldb.rel;

import java.io.IOException;
import java.io.InputStream;

/** Convenient access point for running an embedded or stand-alone interpreter. */

public class Rel {

	private org.reldb.rel.v0.engine.Rel rel;
	
	/** Convenient runner for a stand-alone Rel interpreter. */
	public static void main(String[] args) {
		try {
			ClassPathHack.addFile("rel0000.jar");
		} catch (IOException ioe) {
			System.out.println(ioe.toString());
			return;
		}
		org.reldb.rel.v0.engine.Rel.main(args);
	}
	
	/** Establish a connection with this server. */
	public Rel(String databaseDir, boolean createDbAllowed) throws IOException {
		ClassPathHack.addFile("rel0000.jar");
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
	
}
