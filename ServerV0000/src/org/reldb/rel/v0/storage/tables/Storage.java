package org.reldb.rel.v0.storage.tables;

import com.sleepycat.je.*;

/** A tuple store (Berkeley Database) and associated indexes (also Berkeley DatabaseS) */
public class Storage {

	private Database[] berkeleyDBs;
	
	public Storage(int count) throws DatabaseException {
		berkeleyDBs = new Database[count];
	}

	public Database getDatabase(int i) {
		return berkeleyDBs[i];
	}
	
	public void setDatabase(int i, Database table) throws DatabaseException {
		berkeleyDBs[i] = table;
	}
	
	public StorageNames getStorageNames() throws DatabaseException {
		StorageNames name = new StorageNames(berkeleyDBs.length);
		for (int i=0; i<name.size(); i++)
			name.setName(i, berkeleyDBs[i].getDatabaseName());
		return name;
	}
	
	public int size() {
		return berkeleyDBs.length;
	}
}
