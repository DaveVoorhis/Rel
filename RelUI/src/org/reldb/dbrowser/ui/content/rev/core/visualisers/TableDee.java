package org.reldb.dbrowser.ui.content.rev.core.visualisers;

import org.reldb.dbrowser.ui.content.rev.core.Rev;

public class TableDee extends Operator {
	public TableDee(Rev rev) {
		super(rev, "DEE");
	}
	
	public String getQuery() {
		return "DEE";
	}
	
}
