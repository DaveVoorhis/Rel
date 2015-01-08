package org.reldb.rel.client.stream;

import java.io.IOException;
import java.io.InputStream;

public abstract class InputStreamInterceptor extends InputStream {

	private InputStream inputStream;
	
	public InputStreamInterceptor(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
	public abstract void interceptedRead(int r);
	
	public int read() throws IOException {
		int r = inputStream.read();
		interceptedRead(r);
		return r;
	}

	public int read(byte b[]) throws IOException {
        int r = inputStream.read(b);
        for (byte x: b)
        	interceptedRead(x);
        return r;
    }
	
	public int read(byte b[], int off, int len) throws IOException {
		int r = inputStream.read(b, off, len);
		int c = 0;
		for (int i=off; c<len; i++, c++)
			interceptedRead(b[i]);
		return r;
	}
	
	public long skip(long n) throws IOException {
		return inputStream.skip(n);
	}
	
	public int available() throws IOException {
		return inputStream.available();
	}

	public void close() throws IOException {
		inputStream.close();
	}
	
	public synchronized void mark(int readlimit) {
		inputStream.mark(readlimit);
	}
	
	public synchronized void reset() throws IOException {
		inputStream.reset();
	}
	
	public boolean markSupported() {
		return inputStream.markSupported();
	}

}
