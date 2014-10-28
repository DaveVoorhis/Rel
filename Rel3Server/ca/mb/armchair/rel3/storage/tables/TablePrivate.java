package ca.mb.armchair.rel3.storage.tables;


import ca.mb.armchair.rel3.storage.RelDatabase;
import ca.mb.armchair.rel3.storage.relvars.RelvarHeading;

import com.sleepycat.je.*;

public class TablePrivate extends Table {

	private KeyTables rawTable;
	
	public TablePrivate(RelDatabase database, KeyTables rawTable, RelvarHeading headingDefinition) {
		super(database, headingDefinition);
		setTable(rawTable);
	}
	
	public void setTable(KeyTables rawTable) {
		this.rawTable = rawTable;
	}
	
	@Override
	public KeyTables getTable(Transaction txn) {
		return rawTable;
	}

}
