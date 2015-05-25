package org.reldb.dbrowser.ui.content.rev.operators;

import org.reldb.dbrowser.ui.content.rev.OperatorWithControlPanel;
import org.reldb.dbrowser.ui.content.rev.Rev;

public class Extend extends OperatorWithControlPanel {

	public Extend(Rev rev, String name, int xpos, int ypos) {
		super(rev, name, "EXTEND", xpos, ypos);
		addParameter("Operand"); 
	}

	@Override
	public String getQuery() {
		// TODO Auto-generated method stub
		return null;
	}

}
