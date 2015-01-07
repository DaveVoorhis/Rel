package ca.mb.armchair.rel3.storage.tables;

import ca.mb.armchair.rel3.storage.RelDatabase;
import ca.mb.armchair.rel3.storage.relvars.RelvarHeading;

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
