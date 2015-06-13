package org.reldb.dbrowser.ui.content.rel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;

public class QueryDropper extends DbTreeAction {

	public QueryDropper(RelPanel relPanel) {
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
