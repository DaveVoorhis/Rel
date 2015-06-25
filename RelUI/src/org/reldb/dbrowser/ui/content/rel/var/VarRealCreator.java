package org.reldb.dbrowser.ui.content.rel.var;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.custom.CTabItem;
import org.reldb.dbrowser.ui.content.rel.DbTreeAction;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.NewItemDialog;
import org.reldb.dbrowser.ui.content.rel.RelPanel;
import org.reldb.dbrowser.ui.content.rev.RevDatabase;

public class VarRealCreator extends DbTreeAction {

	public VarRealCreator(RelPanel relPanel) {
		super(relPanel);
	}

	@Override
	public void go(DbTreeItem item) {
		RevDatabase database = new RevDatabase(relPanel.getConnection());
		NewItemDialog namer = new NewItemDialog(relPanel.getShell(), "Variable" + database.getUniqueNumber());
		if (namer.open() != NewItemDialog.OK)
			return;
		if (database.relvarExists(namer.getName())) {
			MessageDialog.openInformation(relPanel.getShell(), "Note", "A variable named " + namer.getName() + " already exists.");
			return;
		}
		DbTreeItem newItem = new DbTreeItem(item, namer.getName());
		CTabItem tab = relPanel.getTab(newItem);
		if (tab != null)
			tab.dispose();
		RelvarDesignerTab varDesignTab = new RelvarDesignerTab(relPanel, newItem);
		relPanel.getTabFolder().setSelection(varDesignTab);
	}

}
