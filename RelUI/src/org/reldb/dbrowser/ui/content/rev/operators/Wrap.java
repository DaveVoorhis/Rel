package org.reldb.dbrowser.ui.content.rev.operators;

import org.reldb.dbrowser.ui.content.rev.OperatorWithControlPanel;
import org.reldb.dbrowser.ui.content.rev.Rev;

public class Wrap extends OperatorWithControlPanel {

	public Wrap(Rev rev, String name, int xpos, int ypos) {
		super(rev, name, "WRAP", xpos, ypos);
		addParameter("Operand"); 
	}

	@Override
	public String getQuery() {
		// TODO Auto-generated method stub
		return null;
	}

}
