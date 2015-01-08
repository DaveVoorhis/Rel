package org.reldb.rel.storage.relvars;

import org.reldb.rel.storage.RelDatabase;
import org.reldb.rel.storage.tables.KeyTableNames;


public class RelvarRealMetadata extends RelvarMetadata {
	public static final long serialVersionUID = 0;
	
	private KeyTableNames tableName;
	
	public RelvarRealMetadata(RelDatabase database, RelvarHeading headingDefinition, String owner) {
		super(database, headingDefinition, owner);
	}
	
	public void setTableName(KeyTableNames tableName) {
		this.tableName = tableName;
	}
	
	public KeyTableNames getTableName() {
		return tableName;
	}
	
	public RelvarGlobal getRelvar(String name, RelDatabase database) {
		return new RelvarReal(name, database, this);
	}
	
	public void dropRelvar(RelDatabase database) {
	}
	
}
