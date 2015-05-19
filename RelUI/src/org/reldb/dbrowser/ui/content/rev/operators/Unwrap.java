package org.reldb.dbrowser.ui.content.rev.operators;

import org.reldb.dbrowser.ui.content.rev.Operator;
import org.reldb.dbrowser.ui.content.rev.Rev;

public class Unwrap extends Operator {

	public Unwrap(Rev rev, String name, int xpos, int ypos) {
		super(rev.getModel(), name, "UNWRAP", xpos, ypos);
		addParameter("Operand", "Relation passed to " + getKind()); 
	}

	@Override
	public String getQuery() {
		// TODO Auto-generated method stub
		return null;
	}

}
