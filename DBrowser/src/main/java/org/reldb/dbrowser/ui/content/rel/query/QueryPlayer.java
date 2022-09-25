package org.reldb.dbrowser.ui.content.rel.query;

import org.reldb.dbrowser.ui.content.rel.RelPanel;
import org.reldb.dbrowser.ui.content.rev.Rev;

public class QueryPlayer extends QueryView {	
	public QueryPlayer(RelPanel relPanel) {
		super(relPanel, Rev.READONLY);
	}
}
