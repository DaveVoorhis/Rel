package org.reldb.dbrowser.ui.content.rel.script;

import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Image;
import org.reldb.dbrowser.ui.content.rel.DbTreeAction;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.RelPanel;
import org.reldb.dbrowser.ui.content.rev.Rev;

public class ScriptView extends DbTreeAction {
	
	public ScriptView(RelPanel relPanel, int revstyle) {
		super(relPanel);
	}

	@Override
	public void go(DbTreeItem item, Image image) {
		CTabItem tab = relPanel.getTab(item);
		if (tab == null) {
			ScriptTab revtab = new ScriptTab(relPanel, item, Rev.EDITABLE);
			tab = revtab;
		}
		tab.setImage(image);
		relPanel.getTabFolder().setSelection(tab);
	}

}
