package org.reldb.rel.client.connection.stream;

import java.io.IOException;
import java.io.InputStream;

import org.reldb.rel.client.connection.Client;

public interface StreamReceiverClient extends Client {
	public InputStream getServerResponseInputStream() throws IOException;
}
