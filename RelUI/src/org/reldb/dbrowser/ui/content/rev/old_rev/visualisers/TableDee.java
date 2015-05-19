package org.reldb.dbrowser.ui.content.rev.old_rev.visualisers;

import org.reldb.dbrowser.ui.content.rev.old_rev.Rev;

public class TableDee extends Operator {
	public TableDee(Rev rev) {
		super(rev, "DEE");
	}
	
	public String getQuery() {
		return "DEE";
	}
	
}
