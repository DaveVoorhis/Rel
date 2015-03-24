package org.reldb.rel.client.stream;

import java.io.*;

public abstract class Client {
	public abstract void sendEvaluate(String src) throws IOException;
	public abstract void sendExecute(String src) throws IOException;
	public abstract void close() throws IOException;
}