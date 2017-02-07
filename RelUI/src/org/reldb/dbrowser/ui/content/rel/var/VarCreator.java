package org.reldb.dbrowser.ui.content.rel.var;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Image;
import org.reldb.dbrowser.ui.RevDatabase;
import org.reldb.dbrowser.ui.content.rel.DbTreeAction;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.RelPanel;
import org.reldb.rel.client.Connection.ExecuteResult;

public class VarCreator extends DbTreeAction {

	public VarCreator(RelPanel relPanel) {
		super(relPanel);
	}

	@Override
	public void go(DbTreeItem item, Image image) {
		RevDatabase database = new RevDatabase(relPanel.getConnection());
		VarTypeAndName namer = new VarTypeAndName(database, relPanel.getShell(), "Variable" + database.getUniqueNumber());
		String typeString = namer.open();
		if (typeString == null)
			return;
		String varname = namer.getVariableName();
		if (database.relvarExists(varname)) {
			MessageDialog.openInformation(relPanel.getShell(), "Note", "A variable named " + varname + " already exists.");
			return;
		}
		if (typeString.equalsIgnoreCase("REAL")) {
			ExecuteResult result = relPanel.getConnection().execute("VAR " + varname + " REAL RELATION {} KEY {};");
			if (result.failed()) {
				MessageDialog.openError(relPanel.getShell(), "Error", "Unable to create var " + varname + ": " + result.getErrorMessage());
				return;
			}
			relPanel.redisplayed();
			DbTreeItem newItem = new DbTreeItem(item, varname);
			CTabItem tab = relPanel.getTab(newItem);
			if (tab != null)
				tab.dispose();
			VarRealDesignerTab varDesignTab = new VarRealDesignerTab(relPanel, newItem);
			varDesignTab.setImage(image);
			relPanel.getTabFolder().setSelection(varDesignTab);
		} else {
			System.out.println("VarCreator: Show panel for creating " + typeString);
		}
	}

}
