package org.reldb.dbrowser.ui.content.rel.query;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.graphics.Image;
import org.reldb.dbrowser.ui.RevDatabase;
import org.reldb.dbrowser.ui.content.rel.DbTreeAction;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.NewItemDialog;
import org.reldb.dbrowser.ui.content.rel.RelPanel;
import org.reldb.dbrowser.ui.content.rev.ModelChangeListener;
import org.reldb.dbrowser.ui.content.rev.Rev;

public class QueryCreator extends DbTreeAction {

	public QueryCreator(RelPanel relPanel) {
		super(relPanel);
	}

	@Override
	public void go(DbTreeItem item, Image image) {
		RevDatabase database = new RevDatabase(relPanel.getConnection());
		NewItemDialog namer = new NewItemDialog(relPanel.getShell(), "Query" + database.getUniqueNumber());
		if (namer.open() != NewItemDialog.OK)
			return;
		if (database.modelExists(namer.getName())) {
			MessageDialog.openInformation(relPanel.getShell(), "Note", "A query named " + namer.getName() + " already exists.");
			return;
		}
		DbTreeItem newItem = new DbTreeItem(item, namer.getName());
		CTabItem tab = relPanel.getTab(newItem);
		if (tab != null)
			tab.dispose();
		RevTab revtab = new RevTab(relPanel, newItem, Rev.EDITABLE);
		revtab.addModelChangeListener(new ModelChangeListener() {
			public void modelChanged() {
				relPanel.redisplayed();
			}
		});
		relPanel.setTab(revtab, image);
	}

}
