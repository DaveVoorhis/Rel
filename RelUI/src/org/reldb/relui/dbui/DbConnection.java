package org.reldb.relui.dbui;

import java.io.IOException;

import org.reldb.rel.client.Connection;
import org.reldb.rel.client.Tuples;
import org.reldb.rel.client.Value;
import org.reldb.rel.client.Connection.HTMLReceiver;
import org.reldb.rel.client.connection.CrashHandler;

public class DbConnection {
	public static final int QUERY_WAIT_MILLISECONDS = 5000;

	private Connection connection;
	
	public DbConnection(String dbURL, CrashHandler crashHandler) {
		connection = new Connection(dbURL, false, crashHandler);
	}
	
	public boolean execute(String query) {
		try {
			connection.execute(query);
			return true;
		} catch (IOException e1) {
			System.out.println("DbTab: Error: " + e1);
			e1.printStackTrace();
			return false;
		}
	}

	public Tuples getTuples(String query) {
		Value response;
		try {
			response = connection.evaluate(query).awaitResult(QUERY_WAIT_MILLISECONDS);
		} catch (IOException e) {
			System.out.println("RelPanel: Error: " + e);
			e.printStackTrace();
			return null;
		}
		if (response instanceof org.reldb.rel.client.Error) {
			System.out.println("RelPanel: Query returns error. " + query + "\n");
			return null;
		}
		if (response == null) {
			System.out.println("RelPanel: Unable to obtain query results.");
			return null;
		}
		return (Tuples)response;		
	}

	public void evaluate(String query, HTMLReceiver htmlReceiver) {
		connection.evaluate(query, htmlReceiver);
	}

}
