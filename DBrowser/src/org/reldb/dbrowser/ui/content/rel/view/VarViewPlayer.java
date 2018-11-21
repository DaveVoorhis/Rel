package org.reldb.dbrowser.ui.content.rel.view;

import org.eclipse.swt.custom.CTabItem;
import org.reldb.dbrowser.ui.content.rel.DbTreeAction;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.RelPanel;
import org.reldb.dbrowser.ui.content.rel.var.ExpressionResultViewerTab;

public class VarViewPlayer extends DbTreeAction {

	public VarViewPlayer(RelPanel relPanel) {
		super(relPanel);
	}

	@Override
	public void go(DbTreeItem item, String imageName) {
		CTabItem tab = relPanel.getTab(item);
		if (tab != null) {
			if (tab instanceof ExpressionResultViewerTab) {
				relPanel.setTab(tab);
				return;
			} else
				tab.dispose();
		}
		ExpressionResultViewerTab viewer = new ExpressionResultViewerTab(relPanel, item, null);
		relPanel.setTab(viewer, imageName);
	}

}
