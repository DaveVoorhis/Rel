 
package org.reldb.dbrowser.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.reldb.dbrowser.dbui.DbMain;

public class OpenLocalDatabase {
	@Execute
	public void execute() {
		DbMain.openLocalDatabase();
	}
}