package org.reldb.dbrowser.ui.content.rel.operator;

import org.eclipse.swt.graphics.Image;
import org.reldb.dbrowser.ui.content.rel.DbTreeAction;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.NaiveShowTab;
import org.reldb.dbrowser.ui.content.rel.RelPanel;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;

public class OperatorPlayer extends DbTreeAction {

	public OperatorPlayer(RelPanel relPanel) {
		super(relPanel);
	}

	@Override
	public void go(DbTreeItem item, Image image) {
		Tuples tuples = relPanel.getConnection().getTuples("(sys.Operators WHERE Name='" + item.getName() + "') {Definition}");
		String definition = "???";
		if (tuples != null)
			for (Tuple tuple: tuples)
				definition = tuple.getAttributeValue("Definition").toString();
		NaiveShowTab typetab = new NaiveShowTab(relPanel, item, definition);
		typetab.setImage(image);
		relPanel.getTabFolder().setSelection(typetab);
	}

}
