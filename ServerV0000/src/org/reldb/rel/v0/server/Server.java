package org.reldb.rel.v0.server;

import java.io.*;
import java.util.*;
import java.net.*;

import org.reldb.rel.v0.interpreter.*;

/** A Server listens on a port for incoming connection requests, and spawns
 * SessionS to handle each connection.
 * 
 * @author scat070
 *
 */
public class Server {

	private final static int shutdownAttempts = 10;
	
	private int portNumber;
	private boolean running;
	private ServerSocket serverSocket;
	private HashMap<Long, Session> sessions;
	private Thread daemon;
	private Instance rel;

	public Server(Instance rel, int portNumber) {
		this.rel = rel;
		this.portNumber = portNumber;
		serverSocket = null;
		startup();
	}

	public Instance getInstance() {
		return rel;
	}
	
	/** Start up server. */
	void startup() {
		if (serverSocket != null)
			return;
		sessions = new HashMap<Long, Session>();
		try {
			serverSocket = new ServerSocket(portNumber);
		} catch (IOException ioe) {
			System.out.println("Server: " + ioe);
			return;
		}
		daemon = new Thread() {
			public void run() {
				running = true;
				InetAddress ip = serverSocket.getInetAddress();
				System.out.println("Server: Listening for connections on " + ip + ":" + portNumber + " (" + rel.getHost() + ")");
				while (running) {
					try {
						new Session(Server.this, serverSocket.accept());
					} catch (IOException ioe) {
						System.out.println("Server: " + ioe.getMessage());
						running = false;
						break;
					}
				}
				System.out.println("Server: Turned off.");
			}
		};
		daemon.start();
	}
	
	/** Shut down server. */
	public void shutdown() {
		System.out.println("Server: Shutting down...");
		if (serverSocket != null) {
			int i;
			// Make multiple attempts to shut down server
			for (i=0; i<shutdownAttempts && running; i++) {
				try {
					try {
						// Delay 1 second
						Thread.sleep(1000);
					} catch (InterruptedException ie) {
					}
					// Close the server socket
					serverSocket.close();
					// Kill all sessions
					killAllSessions();
				} catch (IOException ioe) {
					System.out.println("Server: " + ioe);
				}
			}
			// If all attempts have failed, forcibly terminate daemon
			if (i == shutdownAttempts) {
				System.out.println("Server: Forcing shutdown after " + shutdownAttempts + " attempts.");
				daemon.interrupt();
			}
			serverSocket = null;
		}
	}
	
	/* This is invoked by a Session when it terminates, to advise the
	 * Server that the Session should be removed.
	 */
	synchronized void removeSession(Session s) {
		sessions.remove(new Long(s.getId()));
	}

	/* This is invoked by a Session when it successfully starts, to advise the
	 * Server that it is managing the Session.
	 */
	synchronized void addSession(Session s) {
		sessions.put(new Long(s.getId()), s);
	}
	
	/** Kill all SessionS. */
	void killAllSessions() {
		Session[] s = sessions.values().toArray(new Session[0]);	// prevent concurrent modification exception
		for (Session session: s)
			session.kill();
	}
}
