/*
 * ExternalExecutor.java
 *
 * Created on 18 August 2004, 05:28
 */

package ca.mb.armchair.rel3.external;

import java.io.*;

import ca.mb.armchair.rel3.exceptions.ExceptionSemantic;

/**
 * Mechanisms to run an external executable.
 *
 * @author  dave
 */
public class ExternalExecutor {
    
    /** Private constructor -- no instances needed as all methods are static. */
    private ExternalExecutor() {
    }
    
    /** Shutdown flag for threaded capture. */
    private static boolean endCapture = true;
    
    // Given an input stream, spew it to stdout.  Return the thread that
    // handles this capture.
    private static Thread captureStream(final InputStream s, final PrintStream out) {
        Thread t = new Thread() {
            public void run() {
                BufferedInputStream bs = new BufferedInputStream(s);
                while (!endCapture) {
                    try {
                    	int c = bs.read();
                    	if (c == -1)
                    		return;
                    	out.write(c);
                    } catch (IOException e) {
                        throw new ExceptionSemantic("RS0001: " + e.toString());
                    }
                }
            }
        };
        t.start();
        return t;
    }
    
    /** Run an external executable.  Output will be sent to the PrintStream.
     * Return the exitValue of the process used to invoke the executable.  
     * Throw exceptions if invocation failed. */
    public static int run(PrintStream ps, String command) {
        try {
            Process p = Runtime.getRuntime().exec(command);
            endCapture = false;
            captureStream(p.getInputStream(), ps);
            captureStream(p.getErrorStream(), ps);
            try {
             	p.waitFor();
                endCapture = true;
                return p.exitValue();
            } catch (InterruptedException x) {
                throw new ExceptionSemantic("RS0002: " + x.toString());
            }
        } catch (IOException e) {
            throw new ExceptionSemantic("RS0003: " + e.toString());
        } finally {
            endCapture = true;
        }
    }
    
}
