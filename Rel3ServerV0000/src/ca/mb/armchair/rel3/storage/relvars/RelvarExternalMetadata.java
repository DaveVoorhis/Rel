package ca.mb.armchair.rel3.storage.relvars;

import ca.mb.armchair.rel3.storage.RelDatabase;

public interface RelvarExternalMetadata {
	
	public RelvarGlobal getRelvar(String name, RelDatabase database);
	
	public void dropRelvar(RelDatabase database);
	
	public String getSourceDefinition();
}
