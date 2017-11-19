package org.reldb.dbrowser.ui.content.rel.var;

import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Image;
import org.reldb.dbrowser.ui.content.rel.DbTreeAction;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.RelPanel;

public class VarPlayer extends DbTreeAction {

	public VarPlayer(RelPanel relPanel) {
		super(relPanel);
	}

	@Override
	public void go(DbTreeItem item, Image image) {
		CTabItem tab = relPanel.getTab(item);
		if (tab != null) {
			if (tab instanceof ExpressionResultViewerTab) {
				relPanel.setTab(tab);
				return;
			} else
				tab.dispose();
		}
		ExpressionResultViewerTab viewer = new ExpressionResultViewerTab(relPanel, item);
		relPanel.setTab(viewer, image);
	}

}
