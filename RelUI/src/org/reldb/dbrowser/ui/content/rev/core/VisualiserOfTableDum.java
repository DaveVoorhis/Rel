package org.reldb.dbrowser.ui.content.rev.core;

public class VisualiserOfTableDum extends VisualiserOfOperator {
	
	public VisualiserOfTableDum(Rev rev) {
		super(rev, "DUM");
	}

	public String getQuery() {
		return "DUM"; 
	}
	
}
