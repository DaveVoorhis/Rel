 
package org.reldb.dbrowser.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.reldb.dbrowser.DBrowser;

public class ClearRecentlyUsed {
	@Execute
	public void execute() {
		DBrowser.clearRecentlyUsedDatabaseList();
	}
}