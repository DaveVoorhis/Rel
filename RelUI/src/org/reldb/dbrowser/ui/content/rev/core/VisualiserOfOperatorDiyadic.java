package org.reldb.dbrowser.ui.content.rev.core;

import org.reldb.dbrowser.ui.content.rev.core.graphics.Parameter;

public class VisualiserOfOperatorDiyadic extends VisualiserOfOperator { 
	private Parameter operand1;
	private Parameter operand2;

	public VisualiserOfOperatorDiyadic(Rev rev, String operatorName) {
		super(rev, operatorName);
		operand1 = addParameter("Operand 1", "First relation passed to " + operatorName); 
		operand2 = addParameter("Operand 2", "Second relation passed to " + operatorName);		
	}
	
	public VisualiserOfOperatorDiyadic(Rev rev, String visualiserName, String operatorName) {
		super(rev, operatorName, visualiserName);
		operand1 = addParameter("Operand 1", "First relation passed to " + operatorName); 
		operand2 = addParameter("Operand 2", "Second relation passed to " + operatorName);		
	}
	
	public String getQuery() {		
		if (operand1 == null || operand1.getConnection(0) == null)
			return null;
		if (operand1.getConnection(0).getVisualiser() instanceof VisualiserOfOperand)
			return null;
		VisualiserOfRelation connected1 = (VisualiserOfRelation)operand1.getConnection(0).getVisualiser();
		if (connected1 == null)
			return null;
		
		if (operand2 == null || operand2.getConnection(0) == null)
			return null;
		if (operand2.getConnection(0).getVisualiser() instanceof VisualiserOfOperand)
			return null;
		VisualiserOfRelation connected2 = (VisualiserOfRelation)operand2.getConnection(0).getVisualiser();
		if (connected2 == null)
			return null;
		
		String connectedQuery1 = connected1.getQuery();
		String connectedQuery2 = connected2.getQuery();
		if (connectedQuery1 == null || connectedQuery2 == null)
			return null;
		
		return "((" + connectedQuery1 + ") " + getKind() + " (" + connectedQuery2 + "))";
	}

}
