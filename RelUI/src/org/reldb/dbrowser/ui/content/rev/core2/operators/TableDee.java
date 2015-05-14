package org.reldb.dbrowser.ui.content.rev.core2.operators;

import org.reldb.dbrowser.ui.content.rev.core2.Operator;
import org.reldb.dbrowser.ui.content.rev.core2.Rev;

public class TableDee extends Operator {

	public TableDee(Rev rev, String id, int xpos, int ypos) {
		super(rev.getModel(), id, "TABLE_DEE", xpos, ypos);
	}

	public String getQuery() {
		return "TABLE_DEE";
	}
	
}
