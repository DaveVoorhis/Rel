package org.reldb.dbrowser.ui.content.rel.var;

import org.reldb.dbrowser.ui.content.rel.DbTreeAction;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.RelPanel;

public class VarRealDesigner extends DbTreeAction {

	public VarRealDesigner(RelPanel relPanel) {
		super(relPanel);
	}

	@Override
	public void go(DbTreeItem item) {
		RelvarDesignerTab varDesignTab = new RelvarDesignerTab(relPanel, item);
		relPanel.getTabFolder().setSelection(varDesignTab);
	}

}
