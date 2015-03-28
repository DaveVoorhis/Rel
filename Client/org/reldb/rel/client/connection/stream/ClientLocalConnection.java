package org.reldb.rel.client.connection.stream;

import java.io.*;

import org.reldb.rel.client.connection.CrashHandler;
import org.reldb.rel.client.utilities.ClassPathHack;
import org.reldb.rel.Rel;

public class ClientLocalConnection implements StreamReceiverClient {
	
	private Rel rel;
	private CrashHandler errorHandler;

	/** Establish a connection with a server. */
	public ClientLocalConnection(String databaseDir, boolean createDbAllowed, CrashHandler errorHandler) throws IOException {
		ClassPathHack.addFile("RelDBMS.jar");
		rel = new Rel(databaseDir, createDbAllowed);
		this.errorHandler = errorHandler;
	}
	
	public InputStream getServerResponseInputStream() throws IOException {
		return rel.getServerResponseInputStream();
	}
	
	public void sendEvaluate(String source) {
		try {
			rel.sendEvaluate(source);
		} catch (Throwable t) {
			errorHandler.process(t, source);
		}
	}
	
	public void sendExecute(String source) {
		try {
			rel.sendExecute(source);
		} catch (Throwable t) {
			errorHandler.process(t, source);
		}
	}
	
	public void close() throws IOException {
	}
}
