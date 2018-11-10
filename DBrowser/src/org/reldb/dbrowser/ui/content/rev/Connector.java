package org.reldb.dbrowser.ui.content.rev;

public class Connector extends Visualiser {
	Connector(Parameter parameter) {
		super(parameter.getOperator().getModel(), 
				parameter.getOperator().getID() + "_" + parameter.getNumber(), 
				parameter.getName(), 
				parameter.getOperator().getBounds().x,
				parameter.getOperator().getBounds().y + parameter.getOperator().getBounds().height + 15 + 25 * (parameter.getNumber()));
		btnInfo.dispose();
		btnEdit.dispose();
		btnRun.dispose();
		pack();
	}
	
	public String getQuery() {
		return null;
	}
	
	public boolean isQueryable() {
		return false;
	}
	
	// Disable popup menu.
	protected void setupPopupMenu() {}
}
