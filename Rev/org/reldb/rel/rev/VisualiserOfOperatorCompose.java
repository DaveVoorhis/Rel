package org.reldb.rel.rev;

import org.reldb.rel.rev.graphics.Parameter;

public class VisualiserOfOperatorCompose extends VisualiserOfOperator { 
	private static final long serialVersionUID = 1L;
	
	private Parameter operand1;
	private Parameter operand2;

	public VisualiserOfOperatorCompose(Rev rev, String kind, String name, int xpos, int ypos) {
		super(rev, kind, name, xpos, ypos);
		/*variable=method(name that shows on Operator, Comment that shows when mouse is over operand)*/
		operand1 = addParameter("Operand 1", "First relation to be composed"); 
		operand2 = addParameter("Operand 2", "Second relation to be composed");
		
	}
	
	/*method for creating the query*/
	public String getQuery() {
		
		if (operand1 == null || operand1.getConnection(0) == null)
			return null;
		if (operand1.getConnection(0).getVisualiser() instanceof VisualiserOfOperand)
			return null;
		VisualiserOfRel connected1 = (VisualiserOfRel)operand1.getConnection(0).getVisualiser();
		if (connected1 == null)
			return null;
		
		if (operand2 == null || operand2.getConnection(0) == null)
			return null;
		if (operand2.getConnection(0).getVisualiser() instanceof VisualiserOfOperand)
			return null;
		VisualiserOfRel connected2 = (VisualiserOfRel)operand2.getConnection(0).getVisualiser();
		if (connected2 == null)
			return null;
		
		String connectedQuery1 = connected1.getQuery();
		String connectedQuery2 = connected2.getQuery();
		if (connectedQuery1 == null || connectedQuery2 == null)
			return null;
		
		return "((" + connectedQuery1 + ") COMPOSE (" + connectedQuery2 + "))";
	}
	
	/** Override to be notified that this Visualiser is being removed from the Model. */
	public void removing() {
		super.removing();
		DatabaseAbstractionLayer.removeOperator_Compose(getRev().getConnection(), getName(), getRev().getCrashHandler()); /*replaced removeOperator_Restrict with removeOperator_JOIN and created method in DatabaseAbstractionLayer*/
	}

}
