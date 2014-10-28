package ca.mb.armchair.rel3.client.string;

import java.io.IOException;

import ca.mb.armchair.rel3.client.stream.CrashHandler;

/* Usage:
 
public class Tester {
	public static void main(String args[]) {
		try {
			int port = Defaults.getDefaultPort();
			String host = "localhost";
			String s;

			StringReceiverClient client = new ClientNetwork(host, port);
			ErrorHandler errorHandler = new ErrorHandler() {
				public void process(Throwable t) {
					System.out.println("Crash due to: " + t.getMessage());
				}
			};
			
			// Get server announcement.
			while ((s = client.receive()) != null)
				System.out.println(s);

			client.sendEvaluate("3 + 4", errorHandler);
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
	public void sendEvaluate(String src, CrashHandler errorHandler) throws IOException;
	public void sendExecute(String src, CrashHandler errorHandler) throws IOException;
	public void close() throws IOException;
}
