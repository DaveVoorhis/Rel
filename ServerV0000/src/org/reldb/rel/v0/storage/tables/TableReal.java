package org.reldb.rel.v0.storage.tables;

import org.reldb.rel.v0.storage.RelDatabase;
import org.reldb.rel.v0.storage.relvars.RelvarHeading;

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
	protected Storage getTable(Transaction txn) throws DatabaseException {
		return getDatabase().getTable(txn, relvarName);
	}
}
