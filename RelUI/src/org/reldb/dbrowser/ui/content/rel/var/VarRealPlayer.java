package org.reldb.dbrowser.ui.content.rel.var;

import org.reldb.dbrowser.ui.content.rel.DbTreeAction;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.RelPanel;

public class VarRealPlayer extends DbTreeAction {

	public VarRealPlayer(RelPanel relPanel) {
		super(relPanel);
	}

	@Override
	public void go(DbTreeItem item) {
		RelvarEditorTab editor = new RelvarEditorTab(relPanel, item);
		relPanel.getTabFolder().setSelection(editor);
	}

}
