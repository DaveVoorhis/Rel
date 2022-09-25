package org.reldb.rel.client.connection.stream;

import java.net.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;

public class ClientNetworkConnection extends ClientConnection {
	
	private Socket socket;
	private PrintStream outputToServer;
	
	/** Establish a connection with a server. */
	public ClientNetworkConnection(String serverName, int port) throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		socket = new Socket(serverName, port);
		outputToServer = new PrintStream(socket.getOutputStream());
		obtainInitialServerResponse();
	}

	public InputStream getServerResponseInputStream() throws IOException {
		return socket.getInputStream();
	}

	private void send(String src) throws IOException {
		if (src == null)
			throw new IllegalArgumentException("Attempt to execute null source code.");
		outputToServer.println(src);
		outputToServer.flush();
		if (outputToServer.checkError())
			throw new IOException("Lost connection to server.");
	}

	public void reset() throws IOException {
		send('R' + "<EOT>");
	}

	public void sendEvaluate(String source) throws IOException {
		send('E' + source + "<EOT>");
	}
	
	public void sendExecute(String source) throws IOException {
		send('X' + source + "<EOT>");
	}
	
	public void close() throws IOException {
		outputToServer.close();
		socket.close();
	}
}
