package org.reldb.dbrowser.ui.content.rel.welcome;

import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Image;
import org.reldb.dbrowser.ui.content.rel.DbTreeAction;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.RelPanel;

public class WelcomeView extends DbTreeAction {
	
	public WelcomeView(RelPanel relPanel) {
		super(relPanel);
	}

	@Override
	public void go(DbTreeItem item, Image image) {
		CTabItem tab = relPanel.getTab(item);
		if (tab == null)
			tab = new WelcomeTab(relPanel, item);
		tab.setImage(image);
		relPanel.getTabFolder().setSelection(tab);
	}

}
