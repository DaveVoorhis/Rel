package ca.mb.armchair.rel3.client.stream;

import java.io.*;

public abstract class ClientConnection extends Client {
	public abstract InputStream getServerResponseInputStream() throws IOException;
}
