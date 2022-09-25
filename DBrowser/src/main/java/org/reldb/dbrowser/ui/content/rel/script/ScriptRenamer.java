package org.reldb.dbrowser.ui.content.rel.script;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.custom.CTabItem;
import org.reldb.dbrowser.ui.RevDatabase;
import org.reldb.dbrowser.ui.content.rel.DbTreeAction;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.NewItemDialog;
import org.reldb.dbrowser.ui.content.rel.RelPanel;
import org.reldb.dbrowser.ui.content.rel.RenameItemDialog;

public class ScriptRenamer extends DbTreeAction {

	public ScriptRenamer(RelPanel relPanel) {
		super(relPanel);
	}

	@Override
	public void go(DbTreeItem item, String imageName) {
		RevDatabase database = new RevDatabase(relPanel.getConnection());
		CTabItem tab = relPanel.getTab(item.getTabName());
		if (tab != null) {
			relPanel.getTabFolder().setSelection(tab);
			MessageDialog.openInformation(relPanel.getShell(), "Note", "You must close the '" + item.getTabName() + "' tab first.");
		} else {
			RenameItemDialog namer = new RenameItemDialog(relPanel.getShell(), item.getName());
			if (namer.open() != NewItemDialog.OK)
				return;
			if (database.scriptExists(namer.getName())) {
				MessageDialog.openInformation(relPanel.getShell(), "Note", "A script named " + namer.getName() + " already exists.");
				return;
			}
			boolean result = database.renameScript(item.getName(), namer.getName());
			if (!result) {
				MessageDialog.openError(relPanel.getShell(), "Error", "Unable to rename script " + item.getName() + " to " + namer.getName() + ".");
				return;
			}
			relPanel.redisplayed();
		}
	}

}
