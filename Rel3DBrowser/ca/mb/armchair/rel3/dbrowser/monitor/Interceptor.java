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
	public void print(char s) {
		orig.print(s);
		log.log(Character.toString(s));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void print(int s) {
		orig.print(s);
		log.log(Integer.toString(s));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void print(long s) {
		orig.print(s);
		log.log(Long.toString(s));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void print(double s) {
		orig.print(s);
		log.log(Double.toString(s));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void print(float s) {
		orig.print(s);
		log.log(Float.toString(s));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void print(boolean s) {
		orig.print(s);
		log.log(Boolean.toString(s));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void print(char []s) {
		orig.print(s);
		log.log(new String(s));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void print(Object s) {
		orig.print(s);
		log.log(s.toString());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void println(String s) {
		print(s);
		print(System.lineSeparator());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void println(char s) {
		print(s);
		print(System.lineSeparator());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void println(int s) {
		print(s);
		print(System.lineSeparator());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void println(float s) {
		print(s);
		print(System.lineSeparator());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void println(double s) {
		print(s);
		print(System.lineSeparator());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void println(boolean s) {
		print(s);
		print(System.lineSeparator());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void println(long s) {
		print(s);
		print(System.lineSeparator());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void println(Object s) {
		print(s);
		print(System.lineSeparator());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void println(char []s) {
		print(s);
		print(System.lineSeparator());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void println() {
		print(System.lineSeparator());
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
