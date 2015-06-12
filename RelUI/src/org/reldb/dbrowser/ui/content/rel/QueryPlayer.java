package org.reldb.dbrowser.ui.content.rel;

public class QueryPlayer implements DbTreeAction {

	public QueryPlayer(RelPanel relPanel) {
	}

	@Override
	public void go(DbTreeItem item) {
		System.out.println("Show query " + item.getName());
	}

}
