package org.reldb.dbrowser.ui.content.rel.var;

import org.eclipse.swt.custom.CTabItem;
import org.reldb.dbrowser.ui.content.rel.DbTreeAction;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.RelPanel;

public class VarDesigner extends DbTreeAction {

	public VarDesigner(RelPanel relPanel) {
		super(relPanel);
	}

	@Override
	public void go(DbTreeItem item, String imageName) {
		CTabItem tab = relPanel.getTab(item);
		if (tab != null) {
			if (tab instanceof VarRealDesignerTab) {
				relPanel.setTab(tab);
				return;
			} else
				tab.dispose();
		}
		VarRealDesignerTab varDesignTab = new VarRealDesignerTab(relPanel, item);
		relPanel.setTab(varDesignTab, imageName);
	}

}
