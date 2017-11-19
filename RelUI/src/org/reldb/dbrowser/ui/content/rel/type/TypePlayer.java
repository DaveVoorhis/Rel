package org.reldb.dbrowser.ui.content.rel.type;

import org.eclipse.swt.graphics.Image;
import org.reldb.dbrowser.ui.content.rel.DbTreeAction;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.NaiveShowTab;
import org.reldb.dbrowser.ui.content.rel.RelPanel;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;

public class TypePlayer extends DbTreeAction {

	public TypePlayer(RelPanel relPanel) {
		super(relPanel);
	}

	@Override
	public void go(DbTreeItem item, Image image) {
		Tuples tuples = relPanel.getConnection().getTuples("(sys.Types WHERE Name='" + item.getName() + "') {Definition}");
		String definition = "???";
		if (tuples != null)
			for (Tuple tuple: tuples)
				definition = tuple.getAttributeValue("Definition").toString();
		NaiveShowTab typetab = new NaiveShowTab(relPanel, item, definition);
		relPanel.setTab(typetab, image);
	}

}
