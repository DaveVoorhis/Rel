package org.reldb.dbrowser.handlers.file.recent;

import org.reldb.dbrowser.DBrowser;

public class ClearRecentlyUsed {
	public void execute() {
		DBrowser.clearRecentlyUsedDatabaseList();
	}
}