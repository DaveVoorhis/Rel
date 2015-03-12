 
package org.reldb.relui.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.reldb.relui.dbui.DbMain;

public class OpenLocalDatabase {
	@Execute
	public void execute() {
		DbMain.openLocalDatabase();
	}
}