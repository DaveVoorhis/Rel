package org.reldb.rel.client.string;

import java.io.IOException;

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

public interface StringReceiverClient {
	public String receive() throws IOException;
	public void sendEvaluate(String src) throws IOException;
	public void sendExecute(String src) throws IOException;
	public void close() throws IOException;
}
