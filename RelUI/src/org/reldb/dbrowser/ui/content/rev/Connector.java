package org.reldb.dbrowser.ui.content.rev;

public class Connector extends Visualiser {
	Connector(Operator operator) {
		super(operator.getModel(), 
				operator.getID() + "_" + operator.getParameterCount(), 
				"<<parameter " + operator.getParameterCount() + ">>", 
				operator.getBounds().x,
				operator.getBounds().y + operator.getBounds().height + 15 + 25 * (operator.getParameterCount() - 1));
		btnInfo.dispose();
		btnEdit.dispose();
		btnRun.dispose();
		pack();
	}
	
	public String getQuery() {
		return null;
	}
	
	// Disable popup menu.
	protected void setupPopupMenu() {}
}
