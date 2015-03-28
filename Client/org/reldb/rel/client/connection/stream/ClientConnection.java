package org.reldb.rel.client.connection.stream;

import java.io.*;

import org.reldb.rel.client.connection.Client;

public interface ClientConnection extends Client {
	public InputStream getServerResponseInputStream() throws IOException;
}
