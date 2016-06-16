package org.reldb.rel.v0.storage.tables;


import org.reldb.rel.v0.storage.RelDatabase;
import org.reldb.rel.v0.storage.relvars.RelvarHeading;

import com.sleepycat.je.*;

public class TablePrivate extends Table {

	private Storage rawTable;
	
	public TablePrivate(RelDatabase database, Storage rawTable, RelvarHeading headingDefinition) {
		super(database, headingDefinition);
		setTable(rawTable);
	}
	
	public void setTable(Storage rawTable) {
		this.rawTable = rawTable;
	}
	
	@Override
	public Storage getTable(Transaction txn) {
		return rawTable;
	}

}
