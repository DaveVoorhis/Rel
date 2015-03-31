package org.reldb.rel.client.connection;

import java.io.*;

public interface Client {
	public void sendEvaluate(String src) throws IOException;
	public void sendExecute(String src) throws IOException;
	public void close() throws IOException;
	public void reset() throws IOException;
	public String getServerAnnouncement() throws IOException;
}
