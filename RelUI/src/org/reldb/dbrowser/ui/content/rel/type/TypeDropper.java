package org.reldb.dbrowser.ui.content.rel.type;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Image;
import org.reldb.dbrowser.ui.DbConnection;
import org.reldb.dbrowser.ui.content.rel.DbTreeAction;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.RelPanel;

public class TypeDropper extends DbTreeAction {

	public TypeDropper(RelPanel relPanel) {
		super(relPanel);
	}

	@Override
	public void go(DbTreeItem item, Image image) {
		CTabItem tab = relPanel.getTab(item.getTabName());
		if (tab != null) {
			relPanel.getTabFolder().setSelection(tab);
			MessageDialog.openInformation(relPanel.getShell(), "Note", "You must close the '" + item.getTabName() + "' tab first.");
		} else {
			if (!MessageDialog.openConfirm(relPanel.getShell(), "Confirm DROP", "Are you sure you wish to drop type " + item.getName() + "?"))
				return;
			DbConnection.ExecuteResult result = relPanel.getConnection().execute("DROP TYPE " + item.getName() + ";");
			if (result.failed())
				MessageDialog.openError(relPanel.getShell(), "Error", "Unable to drop type " + item.getName() + ": " + result.getErrorMessage());
			else
				relPanel.redisplayed();
		}
	}

}
