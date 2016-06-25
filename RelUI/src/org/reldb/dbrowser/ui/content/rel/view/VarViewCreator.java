package org.reldb.dbrowser.ui.content.rel.view;

import org.eclipse.swt.graphics.Image;
import org.reldb.dbrowser.ui.content.rel.DbTreeAction;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.NaiveCreatorTab;
import org.reldb.dbrowser.ui.content.rel.RelPanel;

public class VarViewCreator extends DbTreeAction {

	public VarViewCreator(RelPanel relPanel) {
		super(relPanel);
	}

	@Override
	public void go(DbTreeItem item, Image image) {
		DbTreeItem newItem = new DbTreeItem(item, "New View");
		NaiveCreatorTab typetab = new NaiveCreatorTab(relPanel, newItem) {
			protected String getGeneratedCommand(String name, String definition) {
				return "VAR " + name + " VIRTUAL " + definition + ";";
			}
		};
		typetab.setImage(image);
		relPanel.getTabFolder().setSelection(typetab);
	}

}
