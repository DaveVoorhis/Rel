package ca.mb.armchair.rel3.client.stream;

import java.io.IOException;
import java.io.InputStream;

public interface StreamReceiverClient {
	public InputStream getServerResponseInputStream() throws IOException;
	public void sendEvaluate(String src, CrashHandler errorHandler) throws IOException;
	public void sendExecute(String src, CrashHandler errorHandler) throws IOException;
	public void close() throws IOException;
}
