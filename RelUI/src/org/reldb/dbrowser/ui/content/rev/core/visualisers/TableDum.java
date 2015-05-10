package org.reldb.dbrowser.ui.content.rev.core.visualisers;

import org.reldb.dbrowser.ui.content.rev.core.Rev;

public class TableDum extends Operator {
	
	public TableDum(Rev rev) {
		super(rev, "DUM");
	}

	public String getQuery() {
		return "DUM"; 
	}
	
}
