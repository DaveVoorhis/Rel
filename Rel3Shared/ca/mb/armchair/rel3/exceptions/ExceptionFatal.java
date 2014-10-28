package ca.mb.armchair.rel3.exceptions;

/**
 * This exception is thrown when fatal errors are encountered.
 */
public class ExceptionFatal extends Error {

	static final long serialVersionUID = 0;
	
	public ExceptionFatal(String message) {
		super(message);
	}
	
	public ExceptionFatal(String message, Throwable cause) {
		super(message, cause);
	}
}
