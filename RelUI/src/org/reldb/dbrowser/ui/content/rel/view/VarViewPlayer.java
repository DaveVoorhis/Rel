package org.reldb.dbrowser.ui.content.rel.view;

import org.eclipse.swt.graphics.Image;
import org.reldb.dbrowser.ui.content.rel.DbTreeAction;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.RelPanel;

public class VarViewPlayer extends DbTreeAction {

	public VarViewPlayer(RelPanel relPanel) {
		super(relPanel);
	}

	@Override
	public void go(DbTreeItem item, Image image) {
		QueryTable table = new QueryTable(relPanel, item);
		table.setImage(image);
		relPanel.getTabFolder().setSelection(table);
	}

}
