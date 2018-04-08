package org.reldb.dbrowser.ui.content.rel.var;

import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Image;
import org.reldb.dbrowser.ui.content.filtersorter.FilterSorter;
import org.reldb.dbrowser.ui.content.rel.DbTreeAction;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.RelPanel;

public class VarPlayer extends DbTreeAction {

	public VarPlayer(RelPanel relPanel) {
		super(relPanel);
	}

	@Override
	public void go(DbTreeItem item, Image image) {
		FilterSorter filterSorter = null;
		CTabItem tab = relPanel.getTab(item);
		if (tab != null) {
			if (tab instanceof VarEditorTab)
				filterSorter = ((VarEditorTab)tab).getFilterSorter();
			if (tab instanceof ExpressionResultViewerTab) {
				relPanel.setTab(tab);
				return;
			} else
				tab.dispose();
		}
		ExpressionResultViewerTab viewer = new ExpressionResultViewerTab(relPanel, item, filterSorter);
		relPanel.setTab(viewer, image);
	}

}
