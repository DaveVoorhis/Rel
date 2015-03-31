package org.reldb.rel.client.connection.stream;

import java.io.*;

import org.reldb.rel.client.connection.CrashHandler;
import org.reldb.rel.client.utilities.ClassPathHack;
import org.reldb.rel.Rel;

public class ClientLocalConnection extends ClientConnection {
	
	private Rel rel;
	protected CrashHandler errorHandler;

	/** Establish a connection with a server. */
	public ClientLocalConnection(String databaseDir, boolean createDbAllowed, CrashHandler errorHandler) throws IOException {
		ClassPathHack.addFile("RelDBMS.jar");
		rel = new Rel(databaseDir, createDbAllowed);
		this.errorHandler = errorHandler;
		obtainInitialServerResponse();
		errorHandler.setInitialServerResponse(initialServerResponse.toString());
	}
	
	public InputStream getServerResponseInputStream() throws IOException {
		return rel.getServerResponseInputStream();
	}
	
	public void sendEvaluate(String source) {
		try {
			rel.sendEvaluate(source);
		} catch (Throwable t) {
			rel.reset();
			errorHandler.process(t, source);
		}
	}
	
	public void sendExecute(String source) {
		try {
			rel.sendExecute(source);
		} catch (Throwable t) {
			rel.reset();
			errorHandler.process(t, source);
		}
	}
	
	public void close() throws IOException {
		rel.reset();
	}

	public void reset() throws IOException {
		rel.reset();
	}
}
