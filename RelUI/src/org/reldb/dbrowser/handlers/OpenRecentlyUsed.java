 
package org.reldb.dbrowser.handlers;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.menu.MDirectMenuItem;
import org.reldb.dbrowser.DBrowser;

public class OpenRecentlyUsed {
	@Execute
	public void execute(MDirectMenuItem item) {
		DBrowser.openDatabase(item.getLabel().substring("Open ".length()));
	}
}