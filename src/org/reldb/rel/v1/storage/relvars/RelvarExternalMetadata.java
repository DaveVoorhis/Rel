package org.reldb.rel.v1.storage.relvars;

import org.reldb.rel.v1.storage.RelDatabase;

public interface RelvarExternalMetadata {
	
	public RelvarGlobal getRelvar(String name, RelDatabase database);
	
	public void dropRelvar(RelDatabase database);
	
	public String getSourceDefinition();
}
