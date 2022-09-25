package org.reldb.dbrowser.ui.content.rel.view;

import org.reldb.dbrowser.ui.content.rel.DbTreeAction;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.NaiveShowTab;
import org.reldb.dbrowser.ui.content.rel.RelPanel;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;

public class VarViewDesigner extends DbTreeAction {

	public VarViewDesigner(RelPanel relPanel) {
		super(relPanel);
	}

	@Override
	public void go(DbTreeItem item, String imageName) {
		Tuples tuples = relPanel.getConnection().getTuples("(sys.Catalog WHERE Name='" + item.getName() + "' AND isVirtual) {Definition}");
		String definition = "???";
		if (tuples != null)
			for (Tuple tuple: tuples)
				definition = tuple.getAttributeValue("Definition").toString();
		NaiveShowTab typetab = new NaiveShowTab(relPanel, item, definition);
		relPanel.setTab(typetab, imageName);
	}

}
