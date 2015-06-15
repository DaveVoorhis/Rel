package org.reldb.dbrowser.ui.content.rel.type;

import org.eclipse.jface.dialogs.MessageDialog;
import org.reldb.dbrowser.ui.content.rel.DbTreeAction;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.RelPanel;

public class TypeDropper extends DbTreeAction {

	public TypeDropper(RelPanel relPanel) {
		super(relPanel);
	}

	@Override
	public void go(DbTreeItem item) {
		if (!MessageDialog.openConfirm(relPanel.getShell(), "Confirm DROP", "Are you sure you wish to drop type " + item.getName() + "?"))
			return;
		if (!relPanel.getConnection().execute("DROP TYPE " + item.getName() + ";"))
			MessageDialog.openError(relPanel.getShell(), "Error", "Unable to delete type " + item.getName() + ". Check the system log for details.");
		else
			relPanel.redisplayed();
	}

}
