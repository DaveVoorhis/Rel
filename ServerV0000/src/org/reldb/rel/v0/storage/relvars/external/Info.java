package org.reldb.rel.v0.storage.relvars.external;

public abstract class Info {
	// Keyword required for <identifier> in VAR <name> EXTERNAL <identifier> <connection string> [ DUP_REMOVE | DUP_COUNT | AUTOKEY ]
	public abstract String getIdentifier();

	// Short description of this kind of relvar.
	public abstract String getDescription();
	
	// Documentation for overall <connection string>
	public abstract String getConnectionStringDocumentation();
	
	// Get comma-separated components of connection string.
	public abstract InfoComponent[] getConnectionStringComponents();
}
