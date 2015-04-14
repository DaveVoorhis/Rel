 
package org.reldb.dbrowser.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.reldb.dbrowser.dbui.DbMain;

public class OpenRemoteDatabase {
	@Execute
	public void execute() {
		DbMain.openRemoteDatabase();
	}
}
