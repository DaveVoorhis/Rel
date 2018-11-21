package org.reldb.dbrowser.ui.content.rel.welcome;

import org.eclipse.swt.custom.CTabItem;
import org.reldb.dbrowser.ui.content.rel.DbTreeAction;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.RelPanel;

public class WelcomeView extends DbTreeAction {
	
	public WelcomeView(RelPanel relPanel) {
		super(relPanel);
	}

	@Override
	public void go(DbTreeItem item, String imageName) {
		CTabItem tab = relPanel.getTab(item);
		if (tab == null)
			tab = new WelcomeTab(relPanel, item);
		relPanel.setTab(tab, imageName);
	}

}
