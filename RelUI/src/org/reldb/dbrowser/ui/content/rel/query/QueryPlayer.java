package org.reldb.dbrowser.ui.content.rel.query;

import org.eclipse.swt.custom.CTabItem;
import org.reldb.dbrowser.ui.content.rel.DbTreeAction;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.RelPanel;

public class QueryPlayer extends DbTreeAction {
	
	public QueryPlayer(RelPanel relPanel) {
		super(relPanel);		
	}

	@Override
	public void go(DbTreeItem item) {
		CTabItem tab = relPanel.getTab(item.getTabName());
		if (tab == null)
			tab = new RevTab(relPanel, item);
		relPanel.getTabFolder().setSelection(tab);
	}

}
