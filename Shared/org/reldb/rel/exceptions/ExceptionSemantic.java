package org.reldb.rel.exceptions;

/**
 * This exception is thrown when semantic errors are encountered.
 */
public class ExceptionSemantic extends Error {

	static final long serialVersionUID = 0;
	
	public ExceptionSemantic(String message) {
		super(message);
	}
	
	public ExceptionSemantic(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ExceptionSemantic(Throwable cause) {
		super(cause);
	}

}
