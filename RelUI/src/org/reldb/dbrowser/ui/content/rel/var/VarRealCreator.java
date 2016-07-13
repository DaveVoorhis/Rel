package org.reldb.dbrowser.ui.content.rel.var;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Image;
import org.reldb.dbrowser.ui.DbConnection;
import org.reldb.dbrowser.ui.RevDatabase;
import org.reldb.dbrowser.ui.content.rel.DbTreeAction;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.NewItemDialog;
import org.reldb.dbrowser.ui.content.rel.RelPanel;

public class VarRealCreator extends DbTreeAction {

	public VarRealCreator(RelPanel relPanel) {
		super(relPanel);
	}

	@Override
	public void go(DbTreeItem item, Image image) {
		RevDatabase database = new RevDatabase(relPanel.getConnection());
		NewItemDialog namer = new NewItemDialog(relPanel.getShell(), "Variable" + database.getUniqueNumber());
		if (namer.open() != NewItemDialog.OK)
			return;
		String varname = namer.getName();
		if (database.relvarExists(varname)) {
			MessageDialog.openInformation(relPanel.getShell(), "Note", "A variable named " + varname + " already exists.");
			return;
		}
		DbConnection.ExecuteResult result = relPanel.getConnection().execute("VAR " + varname + " REAL RELATION {} KEY {};");
		if (result.failed()) {
			MessageDialog.openError(relPanel.getShell(), "Error", "Unable to create var " + varname + ": " + result.getErrorMessage());
			return;
		}
		relPanel.redisplayed();
		DbTreeItem newItem = new DbTreeItem(item, varname);
		CTabItem tab = relPanel.getTab(newItem);
		if (tab != null)
			tab.dispose();
		RelvarDesignerTab varDesignTab = new RelvarDesignerTab(relPanel, newItem);
		varDesignTab.setImage(image);
		relPanel.getTabFolder().setSelection(varDesignTab);
	}

}
