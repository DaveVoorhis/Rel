package org.reldb.dbrowser.ui.content.rev.operators;

import org.reldb.dbrowser.ui.content.rev.OperatorWithControlPanel;
import org.reldb.dbrowser.ui.content.rev.Rev;

public class Unwrap extends OperatorWithControlPanel {

	public Unwrap(Rev rev, String name, int xpos, int ypos) {
		super(rev, name, "UNWRAP", xpos, ypos);
		addParameter("Operand"); 
	}

	@Override
	public String getQuery() {
		// TODO Auto-generated method stub
		return null;
	}

}
