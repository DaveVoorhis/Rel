package org.reldb.dbrowser.ui.content.rel.script;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Image;
import org.reldb.dbrowser.ui.RevDatabase;
import org.reldb.dbrowser.ui.content.rel.DbTreeAction;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.NewItemDialog;
import org.reldb.dbrowser.ui.content.rel.RelPanel;
import org.reldb.dbrowser.ui.content.rev.Rev;

public class ScriptCreator extends DbTreeAction {

	public ScriptCreator(RelPanel relPanel) {
		super(relPanel);
	}

	@Override
	public void go(DbTreeItem item, Image image) {
		RevDatabase database = new RevDatabase(relPanel.getConnection());
		NewItemDialog namer = new NewItemDialog(relPanel.getShell(), "Script" + database.getUniqueNumber());
		if (namer.open() != NewItemDialog.OK)
			return;
		if (database.scriptExists(namer.getName())) {
			MessageDialog.openInformation(relPanel.getShell(), "Note", "A script named " + namer.getName() + " already exists.");
			return;
		}
		boolean result = database.createScript(namer.getName());
		if (!result) {
			MessageDialog.openError(relPanel.getShell(), "Error", "Unable to create script " + namer.getName() + ".");
			return;
		}
		relPanel.redisplayed();
		DbTreeItem newItem = new DbTreeItem(item, namer.getName());
		CTabItem tab = relPanel.getTab(newItem);
		if (tab != null)
			tab.dispose();
		ScriptTab revtab = new ScriptTab(relPanel, newItem, Rev.EDITABLE);
		relPanel.setTab(revtab, image);
	}

}
