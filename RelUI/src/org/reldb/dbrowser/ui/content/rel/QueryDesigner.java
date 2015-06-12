package org.reldb.dbrowser.ui.content.rel;

public class QueryDesigner implements DbTreeAction {

	public QueryDesigner(RelPanel relPanel) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void go(DbTreeItem item) {
		System.out.println("Design query " + item.getName());
	}

}
