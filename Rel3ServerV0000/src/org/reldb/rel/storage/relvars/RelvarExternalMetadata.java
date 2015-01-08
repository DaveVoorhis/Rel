package org.reldb.rel.storage.relvars;

import org.reldb.rel.storage.RelDatabase;

public interface RelvarExternalMetadata {
	
	public RelvarGlobal getRelvar(String name, RelDatabase database);
	
	public void dropRelvar(RelDatabase database);
	
	public String getSourceDefinition();
}
