package org.reldb.rel.client.connection.string;

import java.io.IOException;

import org.reldb.rel.client.connection.Client;

/* Usage:
 
public class Tester {
	public static void main(String args[]) {
		try {
			int port = Defaults.getDefaultPort();
			String host = "localhost";
			String s;

			StringReceiverClient client = new ClientNetwork(host, port);
			
			// Get server announcement.
			while ((s = client.receive()) != null)
				System.out.println(s);

			client.sendEvaluate("3 + 4");
			while ((s = client.receive()) != null)
				System.out.println(s);

		} catch (IOException ioe) {
			System.out.println("Error: " + ioe);
		}
	}
}
*/

public interface StringReceiverClient extends Client {
	public String receive() throws IOException;
}
