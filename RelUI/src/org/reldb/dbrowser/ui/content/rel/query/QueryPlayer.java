package org.reldb.dbrowser.ui.content.rel.query;

import org.eclipse.swt.SWT;
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
		String name = "Query: " + item.getName();
		CTabItem tab = relPanel.getTab(name);
		if (tab == null) {
			tab = new CTabItem(relPanel.getTabFolder(), SWT.NONE);
			tab.setText("Query: " + item.getName());
		}
		relPanel.getTabFolder().setSelection(tab);
	}

}
