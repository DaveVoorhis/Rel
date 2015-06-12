package org.reldb.dbrowser.ui.content.rel;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabItem;

public class QueryPlayer implements DbTreeAction {

	private RelPanel relPanel;
	
	public QueryPlayer(RelPanel relPanel) {
		this.relPanel = relPanel;
	}

	@Override
	public void go(DbTreeItem item) {
		CTabItem tab = new CTabItem(relPanel.getTabFolder(), SWT.NONE);
		tab.setText(item.getName());
	}

}
