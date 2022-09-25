package org.reldb.rel.v0.server;

import java.net.*;
import java.io.*;

import org.reldb.rel.v0.interpreter.Interpreter;

/** A Session represents an active connection to a client. */
public class Session {
	private static long sessionidgenerator = 0;

	private Server server;
	private Socket socket;
	private boolean running;
	private long sessionid;
	
	/** Create a Session given an open Socket. */
	public Session(final Server owner, Socket channel) {
		sessionid = sessionidgenerator++;
		InetAddress ip = channel.getInetAddress();
		ip.getHostName();
		int remotePort = channel.getPort();
		System.out.println("Session: Open session " + sessionid + " to " + ip + ":" + remotePort);
		server = owner;
		socket = channel;
		(new Thread(() -> {
			try {
				server.addSession(Session.this);
				PrintStream output = new PrintStream(socket.getOutputStream());
				Interpreter interpreter = new Interpreter(owner.getInstance().getDatabase(), output);
				running = true;
				owner.getInstance().announceActive(output);
				output.println("<EOT>");
				output.flush();
				while (running) {
					try {
						int prefix = socket.getInputStream().read();
						if (prefix < 0)
							break;
						if (Character.isWhitespace((char)prefix))
							continue;
						if (prefix == 'E') {
							interpreter.evaluate(socket.getInputStream()).toStream(output);
							output.println();
						}
						else if (prefix == 'X') {
							interpreter.interpret(socket.getInputStream());
							output.println("\nOk.");
						} 
						else if (prefix == 'R') {
							interpreter.reset();
							output.println("\nCancel.");
						}
						else
							output.println("ERROR: Rel server protocol error: expected 'R', 'E' or 'X', but got '" + (char)prefix + "'.");
					} catch (SocketException se) {
						break;
					} catch (Throwable t) {
						interpreter.reset();
						output.println("ERROR: " + t.getMessage());
					}
					output.println("<EOT>");
					output.flush();
				}
				output.close();
			} catch (IOException ioe) {
				System.out.println("Session: " + ioe);
			}
			kill();
		})).start();
	}

	public long getId() {
		return sessionid;
	}
	
	/** Kill this Session. */
	public synchronized void kill() {
		running = false;
		server.removeSession(this);
		if (socket == null)
			return;
		try {
			System.out.println("Session: Close session " + sessionid);
			socket.close();
			socket = null;
		} catch (IOException ioe) {
			System.out.println("Session: Unable to close socket " + socket);
		}
	}
	
}
