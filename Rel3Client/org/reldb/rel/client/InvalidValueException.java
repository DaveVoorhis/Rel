package org.reldb.rel.client;

public class InvalidValueException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public InvalidValueException() {
		super();
	}
	
	public InvalidValueException(String msg) {
		super(msg);
	}
	
    public InvalidValueException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public InvalidValueException(Throwable cause) {
        super(cause);
    }
	
}
