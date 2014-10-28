/**
 * 
 */
package ca.mb.armchair.rel3.tests;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class StringStream extends PrintStream {	 
    private ByteArrayOutputStream out;
    public StringStream() {
        super(System.out);
        out = new ByteArrayOutputStream();
    }
    public void write(byte buf[], int off, int len) {
        out.write(buf, off, len);
    }
    public void flush() {
       super.flush();
    }
    public String toString() {
    	return out.toString();
    }
}