package org.reldb.dbrowser.ui.content.rev.operators;

import org.reldb.dbrowser.ui.content.rev.Operator;
import org.reldb.dbrowser.ui.content.rev.Rev;

public class TableDee extends Operator {

	public TableDee(Rev rev, String id, int xpos, int ypos) {
		super(rev.getModel(), id, "TABLE_DEE", xpos, ypos);
	}

	public String getQuery() {
		return "TABLE_DEE";
	}
	
}
