package org.reldb.dbrowser.ui.content.rev.operators;

import org.reldb.dbrowser.ui.content.rev.Operator;
import org.reldb.dbrowser.ui.content.rev.Rev;

public class Order extends Operator {

	public Order(Rev rev, String name, int xpos, int ypos) {
		super(rev.getModel(), name, "GROUP", xpos, ypos);
		addParameter("Operand", "Relation passed to " + getKind()); 
	}

	@Override
	public String getQuery() {
		// TODO Auto-generated method stub
		return null;
	}

}
