package org.reldb.rel.client.string;

import java.io.*;

import org.reldb.rel.client.stream.Client;
import org.reldb.rel.client.stream.ClientNetworkConnection;
import org.reldb.rel.client.stream.CrashHandler;

public class ClientNetwork extends Client implements StringReceiverClient {
	
	private BufferedReader input;
	private ClientNetworkConnection connection;
	
	/** Establish a connection with a server. */
	public ClientNetwork(String serverName, int port) throws IOException {
		connection = new ClientNetworkConnection(serverName, port);
		input = new BufferedReader(new InputStreamReader(connection.getServerResponseInputStream())); 
	}

	public String receive() throws IOException {
		String received = input.readLine();
		if (received == null)
			return null;
		if (received.equals("<EOT>"))
			return null;
		return received;
	}
		
	public void sendEvaluate(String source, CrashHandler errorHandler) throws IOException {
		connection.sendEvaluate(source, errorHandler);
	}
	
	public void sendExecute(String source, CrashHandler errorHandler) throws IOException {
		connection.sendExecute(source, errorHandler);
	}
	
	public void close() throws IOException {
		input.close();
		connection.close();
	}
}
