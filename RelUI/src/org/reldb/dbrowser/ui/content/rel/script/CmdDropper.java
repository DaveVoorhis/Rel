package org.reldb.dbrowser.ui.content.rel.script;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Image;
import org.reldb.dbrowser.ui.content.rel.DbTreeAction;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.RelPanel;
import org.reldb.dbrowser.ui.content.rev.RevDatabase;

public class CmdDropper extends DbTreeAction {

	public CmdDropper(RelPanel relPanel) {
		super(relPanel);
	}

	@Override
	public void go(DbTreeItem item, Image image) {
		CTabItem tab = relPanel.getTab(item.getTabName());
		if (tab != null) {
			relPanel.getTabFolder().setSelection(tab);
			MessageDialog.openInformation(relPanel.getShell(), "Note", "You must close the '" + item.getTabName() + "' tab first.");
		} else {
			if (!MessageDialog.openConfirm(relPanel.getShell(), "Confirm DROP", "Are you sure you wish to drop query " + item.getName() + "?"))
				return;
			RevDatabase database = new RevDatabase(relPanel.getConnection());
			if (!database.modelDelete(item.getName()))
				MessageDialog.openError(relPanel.getShell(), "Error", "Unable to drop query " + item.getName() + ". Check the system log for details.");
			else
				relPanel.redisplayed();
		}
	}

}
