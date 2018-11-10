package org.reldb.dbrowser.ui.content.rev.operators;

import org.reldb.dbrowser.ui.content.rev.Operator;
import org.reldb.dbrowser.ui.content.rev.Rev;

public class TupleFrom extends Operator {
	
	public TupleFrom(Rev rev, String id, int xpos, int ypos) {
		super(rev.getModel(), id, "TUPLE FROM", xpos, ypos);
		addParameter("Operand");
	}
	
	@Override
	public String getQuery() {
		String source = getQueryForParameterUnparenthesised(0);
		if (source == null)
			return null;
		return "TUPLE FROM " + source;		
	}

}
