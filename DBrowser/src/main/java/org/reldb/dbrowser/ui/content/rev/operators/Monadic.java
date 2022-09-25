package org.reldb.dbrowser.ui.content.rev.operators;

import org.reldb.dbrowser.ui.content.rev.OperatorWithControlPanel;
import org.reldb.dbrowser.ui.content.rev.Rev;

public abstract class Monadic extends OperatorWithControlPanel {
	
	public Monadic(Rev rev, String name, String operatorName, int xpos, int ypos) {
		super(rev, name, operatorName, xpos, ypos);
		addParameter("Operand"); 
		load();
		pack();
	}

	protected void load() {}
	
    protected void delete() {
		getDatabase().removeOperator(getID());
    	super.delete();
    }

}
