package org.reldb.dbrowser.ui.content.rel.query;

import org.reldb.dbrowser.ui.content.rel.DbTreeAction;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.RelPanel;

public class QueryDesigner extends DbTreeAction {

	public QueryDesigner(RelPanel relPanel) {
		super(relPanel);
	}

	@Override
	public void go(DbTreeItem item) {
		System.out.println("Design query " + item.getName());
	}

}
