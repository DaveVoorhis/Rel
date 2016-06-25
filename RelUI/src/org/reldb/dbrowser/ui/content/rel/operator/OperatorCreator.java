package org.reldb.dbrowser.ui.content.rel.operator;

import org.eclipse.swt.graphics.Image;
import org.reldb.dbrowser.ui.content.rel.DbTreeAction;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.NaiveCreatorTab;
import org.reldb.dbrowser.ui.content.rel.RelPanel;

public class OperatorCreator extends DbTreeAction {

	public OperatorCreator(RelPanel relPanel) {
		super(relPanel);
	}

	@Override
	public void go(DbTreeItem item, Image image) {
		DbTreeItem newItem = new DbTreeItem(item, "New Operator");
		NaiveCreatorTab typetab = new NaiveCreatorTab(relPanel, newItem) {
			protected String getGeneratedCommand(String name, String definition) {
				return "OPERATOR " + name + " " + definition + ";";
			}
		};
		typetab.setImage(image);
		relPanel.getTabFolder().setSelection(typetab);
	}

}
