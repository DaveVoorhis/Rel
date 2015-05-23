package org.reldb.dbrowser.ui.content.rev.operators;

import org.reldb.dbrowser.ui.content.rev.Operator;
import org.reldb.dbrowser.ui.content.rev.Rev;

public class Rename extends Operator {

	public Rename(Rev rev, String name, int xpos, int ypos) {
		super(rev.getModel(), name, "RENAME", xpos, ypos);
		addParameter("Operand", "Relation passed to " + getKind()); 
	}

	@Override
	public String getQuery() {
		String source = getQueryForParameter(0);
		if (source == null)
			return null;
		return "(" + source + " RENAME {})";
	}

}
