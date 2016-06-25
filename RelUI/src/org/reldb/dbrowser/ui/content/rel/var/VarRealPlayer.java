package org.reldb.dbrowser.ui.content.rel.var;

import org.eclipse.swt.graphics.Image;
import org.reldb.dbrowser.ui.content.rel.DbTreeAction;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.RelPanel;

public class VarRealPlayer extends DbTreeAction {

	public VarRealPlayer(RelPanel relPanel) {
		super(relPanel);
	}

	@Override
	public void go(DbTreeItem item, Image image) {
		RelvarEditorTab editor = new RelvarEditorTab(relPanel, item);
		editor.setImage(image);
		relPanel.getTabFolder().setSelection(editor);
	}

}
