package org.reldb.rel.exceptions;

public class DatabaseFormatVersionException extends Exception {
	private static final long serialVersionUID = 1L;
	private int oldVersion;
	public DatabaseFormatVersionException(String msg, int oldVersion) {
		super(msg);
		this.oldVersion = oldVersion;
	}
	public DatabaseFormatVersionException(String msg) {
		super(msg);
		this.oldVersion = -1;
	}
	public int getOldVersion() {return oldVersion;}
}