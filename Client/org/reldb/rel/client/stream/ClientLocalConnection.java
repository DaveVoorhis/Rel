package org.reldb.rel.client.stream;

import java.io.*;

import org.reldb.rel.client.utilities.ClassPathHack;
import org.reldb.rel.v0.engine.Rel;

public class ClientLocalConnection extends ClientConnection implements StreamReceiverClient {
	
	private Rel rel;

	/** Establish a connection with a server. */
	public ClientLocalConnection(String databaseDir, boolean createDbAllowed) throws IOException {
		ClassPathHack.addFile("Rel.jar");
		ClassPathHack.addFile("commons-codec-1.4.jar");
		ClassPathHack.addFile("commons-logging-1.1.1.jar");
		ClassPathHack.addFile("httpclient-4.1.3.jar");
		ClassPathHack.addFile("httpclient-cache-4.1.3.jar");
		ClassPathHack.addFile("httpcore-4.1.4.jar");
		ClassPathHack.addFile("httpmime-4.1.3.jar");
		rel = new Rel(databaseDir, createDbAllowed);
	}
	
	public InputStream getServerResponseInputStream() throws IOException {
		return rel.getServerResponseInputStream();
	}
	
	public void sendEvaluate(String source, CrashHandler errorHandler) {
		try {
			rel.sendEvaluate(source);
		} catch (Throwable t) {
			errorHandler.process(t);
		}
	}
	
	public void sendExecute(String source, CrashHandler errorHandler) {
		try {
			rel.sendExecute(source);
		} catch (Throwable t) {
			errorHandler.process(t);
		}
	}
	
	public void close() throws IOException {
	}
}
