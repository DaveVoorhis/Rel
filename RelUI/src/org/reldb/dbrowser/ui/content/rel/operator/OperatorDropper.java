package org.reldb.dbrowser.ui.content.rel.operator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.custom.CTabItem;
import org.reldb.dbrowser.ui.DbConnection;
import org.reldb.dbrowser.ui.content.rel.DbTreeAction;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.RelPanel;

public class OperatorDropper extends DbTreeAction {

	public OperatorDropper(RelPanel relPanel) {
		super(relPanel);
	}

	@Override
	public void go(DbTreeItem item) {
		CTabItem tab = relPanel.getTab(item.getTabName());
		if (tab != null) {
			relPanel.getTabFolder().setSelection(tab);
			MessageDialog.openInformation(relPanel.getShell(), "Note", "You must close the '" + item.getTabName() + "' tab first.");
		} else {
			if (!MessageDialog.openConfirm(relPanel.getShell(), "Confirm DROP", "Are you sure you wish to drop operator " + item.getName() + "?"))
				return;
			// get specific operator signature here
			DbConnection.ExecuteResult result = relPanel.getConnection().execute("DROP OPERATOR " + item.getName() + ";");
			if (result.failed())
				MessageDialog.openError(relPanel.getShell(), "Error", "Unable to drop operator " + item.getName() + ": " + result.getErrorMessage());
			else
				relPanel.redisplayed();
		}
	}

}
