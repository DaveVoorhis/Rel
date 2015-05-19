package org.reldb.dbrowser.ui.content.rev.old_rev.visualisers;

import org.reldb.dbrowser.ui.content.rev.old_rev.Rev;

public class TableDum extends Operator {
	
	public TableDum(Rev rev) {
		super(rev, "DUM");
	}

	public String getQuery() {
		return "DUM"; 
	}
	
}
