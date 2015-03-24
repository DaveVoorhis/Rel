package org.reldb.rel.client.string;

import java.io.*;
import java.util.concurrent.LinkedBlockingQueue;

import org.reldb.rel.client.stream.Client;
import org.reldb.rel.client.stream.ClientLocalConnection;
import org.reldb.rel.client.stream.CrashHandler;

public class ClientLocal extends Client implements StringReceiverClient {
	
	private ClientLocalConnection connection;
	private LinkedBlockingQueue<String> outputStringQueue = new LinkedBlockingQueue<String>();
	private boolean receiverRunning;
	
	/** Establish a connection with a server. */
	public ClientLocal(String databaseDir, boolean createDbAllowed, CrashHandler crashHandler) throws IOException {
		connection = new ClientLocalConnection(databaseDir, createDbAllowed, crashHandler);
		final BufferedReader input = new BufferedReader(new InputStreamReader(connection.getServerResponseInputStream()));
		Thread receiver = new Thread() {
			public void run() {
				receiverRunning = true;
				while (receiverRunning) {
					try {
						String received = input.readLine();
						if (received != null)							
							outputStringQueue.add(received);
					} catch (IOException ioe) {
						if (!(ioe.getMessage().equals("Write end dead") || ioe.getMessage().equals("Pipe broken")))
							System.out.println("ClientLocal: " + ioe.getMessage());
						try {
							sleep(250);
						} catch (InterruptedException ie) {}
					}
				}
			}
		};
		receiver.setDaemon(true);
		receiver.start();
	}

	public String receive() throws IOException {
		String received = null;
		try {
			received = outputStringQueue.take();
		} catch (InterruptedException ie) {}
		if (received.equals("<EOT>"))
			return null;
		return received;
	}
	
	public void sendEvaluate(String source) throws IOException {
		connection.sendEvaluate(source);
	}
	
	public void sendExecute(String source) throws IOException {
		connection.sendExecute(source);
	}
	
	public void close() throws IOException {
		connection.close();
		receiverRunning = false;
	}
}
