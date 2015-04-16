package org.reldb.rel.v1.storage.tables;


import org.reldb.rel.v1.storage.RelDatabase;
import org.reldb.rel.v1.storage.relvars.RelvarHeading;

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
