package org.reldb.dbrowser.ui.content.rel.var;

import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Image;
import org.reldb.dbrowser.ui.content.rel.DbTreeAction;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.RelPanel;

public class VarDesigner extends DbTreeAction {

	public VarDesigner(RelPanel relPanel) {
		super(relPanel);
	}

	@Override
	public void go(DbTreeItem item, Image image) {
		CTabItem tab = relPanel.getTab(item);
		if (tab != null) {
<<<<<<< HEAD
			if (tab instanceof VarRealDesignerTab) {
				tab.getParent().setSelection(tab);
				return;
			} else
				tab.dispose();
		}
		VarRealDesignerTab varDesignTab = new VarRealDesignerTab(relPanel, item);
=======
			if (tab instanceof RelvarDesignerTab) {
				tab.getParent().setSelection(tab);
				return;
			} else
				tab.dispose();
		}
		RelvarDesignerTab varDesignTab = new RelvarDesignerTab(relPanel, item);
>>>>>>> refs/remotes/origin/master
		varDesignTab.setImage(image);
		relPanel.getTabFolder().setSelection(varDesignTab);
	}

}
