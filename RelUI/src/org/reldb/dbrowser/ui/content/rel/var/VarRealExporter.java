package org.reldb.dbrowser.ui.content.rel.var;

import org.eclipse.swt.graphics.Image;
import org.reldb.dbrowser.ui.content.rel.DbTreeAction;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.ExporterDialog;
import org.reldb.dbrowser.ui.content.rel.RelPanel;
import org.reldb.rel.client.Value;

public class VarRealExporter extends DbTreeAction {

	public VarRealExporter(RelPanel relPanel) {
		super(relPanel);
	}

	@Override
	public void go(DbTreeItem item, Image image) {
		String itemName = item.getName();
		Value result = relPanel.getConnection().evaluate(itemName);
		(new ExporterDialog(relPanel.getShell(), itemName, result)).open();
	}

}
