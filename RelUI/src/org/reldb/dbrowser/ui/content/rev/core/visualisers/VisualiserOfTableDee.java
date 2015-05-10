package org.reldb.dbrowser.ui.content.rev.core.visualisers;

import org.reldb.dbrowser.ui.content.rev.core.Rev;

public class VisualiserOfTableDee extends Operator {
	public VisualiserOfTableDee(Rev rev) {
		super(rev, "DEE");
	}
	
	public String getQuery() {
		return "DEE";
	}
	
}
