package org.reldb.dbrowser.ui.content.rev.core2.operators;

import org.reldb.dbrowser.ui.content.rev.core2.Operator;
import org.reldb.dbrowser.ui.content.rev.core2.Rev;

public class Diadic extends Operator {

	public Diadic(Rev rev, String name, String kind, int xpos, int ypos) {
		super(rev.getModel(), name, kind, xpos, ypos);
		addParameter("Operand 1", "First relation passed to " + kind); 
		addParameter("Operand 2", "Second relation passed to " + kind);		
	}

}
