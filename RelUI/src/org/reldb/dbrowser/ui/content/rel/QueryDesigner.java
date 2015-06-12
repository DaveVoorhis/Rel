package org.reldb.dbrowser.ui.content.rel;

public class QueryDesigner implements DbTreeAction {

	public QueryDesigner(RelPanel relPanel) {
	}

	@Override
	public void go(DbTreeItem item) {
		System.out.println("Design query " + item.getName());
	}

}
