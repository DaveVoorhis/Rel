package ca.mb.armchair.rel3.client.stream;

import java.io.*;

public abstract class Client {
	public abstract void sendEvaluate(String src, CrashHandler errorHandler) throws IOException;
	public abstract void sendExecute(String src, CrashHandler errorHandler) throws IOException;
	public abstract void close() throws IOException;
}