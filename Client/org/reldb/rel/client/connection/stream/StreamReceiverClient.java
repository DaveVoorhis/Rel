package org.reldb.rel.client.connection.stream;

import java.io.IOException;
import java.io.InputStream;

public interface StreamReceiverClient {
	public InputStream getServerResponseInputStream() throws IOException;
	public void sendEvaluate(String src) throws IOException;
	public void sendExecute(String src) throws IOException;
	public void close() throws IOException;
}
