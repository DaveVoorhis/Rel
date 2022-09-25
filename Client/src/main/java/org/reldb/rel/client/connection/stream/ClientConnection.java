package org.reldb.rel.client.connection.stream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

abstract class ClientConnection implements StreamReceiverClient {
	protected StringBuffer initialServerResponse;

	protected void obtainInitialServerResponse() throws IOException {
		initialServerResponse = new StringBuffer();
		String r;
		final BufferedReader input = new BufferedReader(new InputStreamReader(getServerResponseInputStream()));
		while ((r = input.readLine()) != null) {
			if (r.equals("<EOT>"))
				break;
			else if (!r.equals("Ok.")) {
				initialServerResponse.append(r);
				initialServerResponse.append('\n');
			}
		}		
	}

	public String getServerAnnouncement() throws IOException {
		return initialServerResponse.toString();
	}
	
}
