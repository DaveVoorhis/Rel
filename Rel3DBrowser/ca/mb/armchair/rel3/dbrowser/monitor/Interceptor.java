package ca.mb.armchair.rel3.dbrowser.monitor;

import java.io.OutputStream;
import java.io.PrintStream;

// Based on http://stackoverflow.com/questions/3228427/redirect-system-out-println
public class Interceptor extends PrintStream {
	private Logger log;
	private PrintStream orig;

	/**
	 * Initializes a new instance of the class Interceptor.
	 *
	 * @param out
	 *            the output stream to be assigned
	 * @param log
	 *            the logger
	 */
	public Interceptor(OutputStream out, Logger log) {
		super(out, true);
		this.log = log;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void finalize() throws Throwable {
		detachOut();
		super.finalize();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void print(String s) {
		orig.print(s);
		log.log(s);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void println(String s) {
		print(s + System.lineSeparator());
	}

	/**
	 * Attaches System.out to interceptor.
	 */
	public void attachOut() {
		orig = System.out;
		System.setOut(this);
	}

	/**
	 * Attaches System.err to interceptor.
	 */
	public void attachErr() {
		orig = System.err;
		System.setErr(this);
	}

	/**
	 * Detaches System.out.
	 */
	public void detachOut() {
		if (null != orig) {
			System.setOut(orig);
		}
	}

	/**
	 * Detaches System.err.
	 */
	public void detachErr() {
		if (null != orig) {
			System.setErr(orig);
		}
	}
}
