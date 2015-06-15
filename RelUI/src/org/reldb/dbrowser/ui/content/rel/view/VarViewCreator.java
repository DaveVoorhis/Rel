package org.reldb.dbrowser.ui.content.rel.view;

import org.reldb.dbrowser.ui.content.rel.DbTreeAction;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.NaiveCreatorTab;
import org.reldb.dbrowser.ui.content.rel.RelPanel;

public class VarViewCreator extends DbTreeAction {

	public VarViewCreator(RelPanel relPanel) {
		super(relPanel);
	}

	@Override
	public void go(DbTreeItem item) {
		DbTreeItem newItem = new DbTreeItem(item, "New View");
		NaiveCreatorTab typetab = new NaiveCreatorTab(relPanel, newItem) {
			protected String getGeneratedCommand(String name, String definition) {
				return "VAR " + name + " VIRTUAL " + definition + ";";
			}
		};
		relPanel.getTabFolder().setSelection(typetab);
	}

}
