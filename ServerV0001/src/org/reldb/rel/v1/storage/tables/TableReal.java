package org.reldb.rel.v1.storage.tables;

import org.reldb.rel.v1.storage.RelDatabase;
import org.reldb.rel.v1.storage.relvars.RelvarHeading;

import com.sleepycat.je.*;

public class TableReal extends Table {

	private String relvarName;
	
	public TableReal(RelDatabase database, String relvarName, RelvarHeading keyDefinition) {
		super(database, keyDefinition);
		setTable(relvarName);
	}
	
	public void setTable(String relvarName) {
		this.relvarName = relvarName;
	}
	
	@Override
	protected KeyTables getTable(Transaction txn) throws DatabaseException {
		return getDatabase().getTable(txn, relvarName);
	}
}
