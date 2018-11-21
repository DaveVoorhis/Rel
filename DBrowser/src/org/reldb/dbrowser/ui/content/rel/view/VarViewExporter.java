package org.reldb.dbrowser.ui.content.rel.view;

import org.reldb.dbrowser.ui.content.rel.DbTreeAction;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.ExporterDialog;
import org.reldb.dbrowser.ui.content.rel.RelPanel;

public class VarViewExporter extends DbTreeAction {

	public VarViewExporter(RelPanel relPanel) {
		super(relPanel);
	}

	@Override
	public void go(DbTreeItem item, String imageName) {
		String itemName = item.getName();
		ExporterDialog.runQueryToExport(relPanel.getShell(), relPanel.getConnection(), itemName, itemName);
	}

}
