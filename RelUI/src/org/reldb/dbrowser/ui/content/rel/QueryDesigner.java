package org.reldb.dbrowser.ui.content.rel;

public class QueryDesigner extends DbTreeAction {

	public QueryDesigner(RelPanel relPanel) {
		super(relPanel);
	}

	@Override
	public void go(DbTreeItem item) {
		System.out.println("Design query " + item.getName());
	}

}
