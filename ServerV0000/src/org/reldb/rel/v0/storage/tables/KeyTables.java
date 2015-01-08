package org.reldb.rel.v0.storage.tables;

import com.sleepycat.je.*;

/** A table (Database) and associated indexes (also DatabaseS) */
public class KeyTables {

	private Database[] tables;
	
	public KeyTables(int tableCount) throws DatabaseException {
		tables = new Database[tableCount];
	}

	public Database getDatabase(int i) {
		return tables[i];
	}
	
	public void setDatabase(int i, Database table) throws DatabaseException {
		tables[i] = table;
	}
	
	public KeyTableNames getDatabaseName() throws DatabaseException {
		KeyTableNames name = new KeyTableNames(tables.length);
		for (int i=0; i<name.size(); i++)
			name.setName(i, tables[i].getDatabaseName());
		return name;
	}
	
	public int size() {
		return tables.length;
	}
}
