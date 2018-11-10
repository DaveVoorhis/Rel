package org.reldb.dbrowser.handlers.file.recent;

import org.reldb.dbrowser.DBrowser;
import org.reldb.dbrowser.DecoratedMenuItem;

public class OpenRecentlyUsed {
	public void execute(DecoratedMenuItem item) {
		DBrowser.openDatabase(item.getText().substring("Open ".length()));
	}
}