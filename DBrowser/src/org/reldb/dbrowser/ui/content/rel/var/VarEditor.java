package org.reldb.dbrowser.ui.content.rel.var;

import org.eclipse.swt.custom.CTabItem;
import org.reldb.dbrowser.ui.content.filtersorter.FilterSorter;
import org.reldb.dbrowser.ui.content.rel.DbTreeAction;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.RelPanel;

public class VarEditor extends DbTreeAction {

	public VarEditor(RelPanel relPanel) {
		super(relPanel);
	}

	@Override
	public void go(DbTreeItem item, String imageName) {
		FilterSorter filterSorter = null;		
		CTabItem tab = relPanel.getTab(item);
		if (tab != null) {
			if (tab instanceof ExpressionResultViewerTab)
				filterSorter = ((ExpressionResultViewerTab)tab).getFilterSorter();
			if (tab instanceof VarEditorTab) {
				relPanel.setTab(tab);
				return;
			} else
				tab.dispose();
		}
		VarEditorTab editor = new VarEditorTab(relPanel, item, filterSorter);
		relPanel.setTab(editor, imageName);
	}

}
