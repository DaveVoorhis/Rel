package org.reldb.dbrowser.ui.content.rev.operators;

import org.reldb.dbrowser.ui.content.rev.Operator;
import org.reldb.dbrowser.ui.content.rev.Rev;

public class TableDum extends Operator {

	public TableDum(Rev rev, String id, int xpos, int ypos) {
		super(rev.getModel(), id, "TABLE_DUM", xpos, ypos);
	}

	public String getQuery() {
		return "TABLE_DUM";
	}
	
}
