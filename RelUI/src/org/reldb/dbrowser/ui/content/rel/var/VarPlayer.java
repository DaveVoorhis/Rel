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
<<<<<<< HEAD
			if (tab instanceof VarEditorTab) {
				tab.getParent().setSelection(tab);
				return;
			} else
				tab.dispose();
		}
		VarEditorTab editor = new VarEditorTab(relPanel, item);
=======
			if (tab instanceof RelvarEditorTab) {
				tab.getParent().setSelection(tab);
				return;
			} else
				tab.dispose();
		}
		RelvarEditorTab editor = new RelvarEditorTab(relPanel, item);
>>>>>>> refs/remotes/origin/master
		editor.setImage(image);
		relPanel.getTabFolder().setSelection(editor);
	}

}
