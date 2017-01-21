package org.reldb.rel.v0.storage.relvars.external;

public abstract class Info {
	// Keyword required for <identifier> in VAR <name> EXTERNAL <identifier> <connection string> [ DUP_REMOVE | DUP_COUNT | AUTOKEY ]
	public abstract String getIdentifier();
	
	// Documentation for <connection string>
	public abstract String getConnectionStringDocumentation();
	
	// True if <connection string> specifies a file
	public abstract boolean isConnectionStringAFile();
	
	// Return suggested file extensions. Null if any file is allowable or if isConnectionStringAFile() returns false.
	public abstract String[] getAppropriateFileExtension();
}
