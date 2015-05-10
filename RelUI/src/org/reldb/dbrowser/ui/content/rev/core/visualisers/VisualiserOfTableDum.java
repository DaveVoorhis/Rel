package org.reldb.dbrowser.ui.content.rev.core.visualisers;

import org.reldb.dbrowser.ui.content.rev.core.Rev;

public class VisualiserOfTableDum extends Operator {
	
	public VisualiserOfTableDum(Rev rev) {
		super(rev, "DUM");
	}

	public String getQuery() {
		return "DUM"; 
	}
	
}
