package org.reldb.dbrowser.ui.content.rel;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.custom.CTabItem;
import org.reldb.dbrowser.ui.content.rev.DatabaseAbstractionLayer;

public class QueryDropper extends DbTreeAction {

	public QueryDropper(RelPanel relPanel) {
		super(relPanel);
	}

	@Override
	public void go(DbTreeItem item) {
		String name = "Query: " + item.getName();
		CTabItem tab = relPanel.getTab(name);
		if (tab != null) {
			relPanel.getTabFolder().setSelection(tab);
			MessageDialog.openInformation(relPanel.getShell(), "Note", "You must close the '" + name + "' tab first.");
		} else {
			if (!MessageDialog.openConfirm(relPanel.getShell(), "Confirm DROP", "Are you sure you wish to drop " + item.getName() + "?"))
				return;
			DatabaseAbstractionLayer dal = new DatabaseAbstractionLayer(relPanel.getConnection());
			if (!dal.modelDelete(item.getName()))
				MessageDialog.openError(relPanel.getShell(), "Error", "Unable to delete query " + item.getName() + ". Check the system log for details.");
			else
				relPanel.redisplayed();
		}
	}

}
