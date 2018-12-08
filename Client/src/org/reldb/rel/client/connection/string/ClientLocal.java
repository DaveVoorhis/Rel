package org.reldb.rel.client.connection.string;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.LinkedBlockingQueue;

import org.reldb.rel.client.connection.CrashHandler;
import org.reldb.rel.client.connection.stream.ClientLocalConnection;
import org.reldb.rel.exceptions.DatabaseFormatVersionException;

public class ClientLocal implements StringReceiverClient {
	
	private ClientLocalConnection connection;
	private LinkedBlockingQueue<String> outputStringQueue = new LinkedBlockingQueue<String>();
	private boolean receiverRunning;
	
	/** Establish a connection with a server. */
	public ClientLocal(String databaseDir, boolean createDbAllowed, CrashHandler crashHandler, String[] additionalJars) throws IOException, DatabaseFormatVersionException, ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		connection = new ClientLocalConnection(databaseDir, createDbAllowed, crashHandler, additionalJars);
		final BufferedReader input = new BufferedReader(new InputStreamReader(connection.getServerResponseInputStream()));
		Thread receiver = new Thread(() -> {
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
						Thread.sleep(250);
					} catch (InterruptedException ie) {}
				}
			}
		});
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
	
	public void reset() throws IOException {
		connection.reset();
	}

	@Override
	public String getServerAnnouncement() throws IOException {
		return connection.getServerAnnouncement();
	}
}
