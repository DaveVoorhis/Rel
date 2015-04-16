package org.reldb.rel.v1.storage.relvars;

import org.reldb.rel.v1.storage.RelDatabase;

public abstract class RelvarCustomMetadata extends RelvarMetadata implements RelvarExternalMetadata {
	private static final long serialVersionUID = 0;

	public RelvarCustomMetadata(RelDatabase database,RelvarHeading headingDefinition, String owner) {
		super(database, headingDefinition, owner);
	}
	
	public abstract String tableClassName();
	
	public abstract String getType();

	@Override
	public abstract RelvarGlobal getRelvar(String name, RelDatabase database);

	@Override
	public abstract void dropRelvar(RelDatabase database);
	
	@Override
	public abstract String getSourceDefinition();
}
