package org.reldb.dbrowser.ui.content.rel.type;

import org.reldb.dbrowser.ui.content.rel.DbTreeAction;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.NaiveCreatorTab;
import org.reldb.dbrowser.ui.content.rel.RelPanel;

public class TypeCreator extends DbTreeAction {

	public TypeCreator(RelPanel relPanel) {
		super(relPanel);
	}

	@Override
	public void go(DbTreeItem item) {
		DbTreeItem newItem = new DbTreeItem(item, "New Type");
		NaiveCreatorTab typetab = new NaiveCreatorTab(relPanel, newItem) {
			protected String getGeneratedCommand(String name, String definition) {
				return "TYPE " + name + " " + definition + ";";
			}			
		};
		relPanel.getTabFolder().setSelection(typetab);
	}

}
