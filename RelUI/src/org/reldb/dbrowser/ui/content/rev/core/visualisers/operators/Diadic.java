package org.reldb.dbrowser.ui.content.rev.core.visualisers.operators;

import org.reldb.dbrowser.ui.content.rev.core.Rev;
import org.reldb.dbrowser.ui.content.rev.core.graphics.Parameter;
import org.reldb.dbrowser.ui.content.rev.core.visualisers.Operand;
import org.reldb.dbrowser.ui.content.rev.core.visualisers.Operator;
import org.reldb.dbrowser.ui.content.rev.core.visualisers.Relation;

public class Diadic extends Operator { 
	private Parameter operand1;
	private Parameter operand2;

	public Diadic(Rev rev, String operatorName) {
		super(rev, operatorName);
		operand1 = addParameter("Operand 1", "First relation passed to " + operatorName); 
		operand2 = addParameter("Operand 2", "Second relation passed to " + operatorName);		
	}
	
	public Diadic(Rev rev, String visualiserName, String operatorName, int xpos, int ypos) {
		super(rev, operatorName, visualiserName, xpos, ypos);
		operand1 = addParameter("Operand 1", "First relation passed to " + operatorName); 
		operand2 = addParameter("Operand 2", "Second relation passed to " + operatorName);		
	}
	
	public String getQuery() {		
		if (operand1 == null || operand1.getConnection(0) == null)
			return null;
		if (operand1.getConnection(0).getVisualiser() instanceof Operand)
			return null;
		Relation connected1 = (Relation)operand1.getConnection(0).getVisualiser();
		if (connected1 == null)
			return null;
		
		if (operand2 == null || operand2.getConnection(0) == null)
			return null;
		if (operand2.getConnection(0).getVisualiser() instanceof Operand)
			return null;
		Relation connected2 = (Relation)operand2.getConnection(0).getVisualiser();
		if (connected2 == null)
			return null;
		
		String connectedQuery1 = connected1.getQuery();
		String connectedQuery2 = connected2.getQuery();
		if (connectedQuery1 == null || connectedQuery2 == null)
			return null;
		
		return "((" + connectedQuery1 + ") " + getKind() + " (" + connectedQuery2 + "))";
	}

}
