package org.reldb.dbrowser.handlers.file;

import org.eclipse.e4.core.di.annotations.Execute;
import org.reldb.dbrowser.DBrowser;

public class NewDatabase {
	@Execute
	public void execute() {
		DBrowser.newDatabase();
	}
}