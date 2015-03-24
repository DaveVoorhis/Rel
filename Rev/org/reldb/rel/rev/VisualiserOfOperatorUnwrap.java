package org.reldb.rel.rev;

import org.reldb.rel.client.Tuples;
import org.reldb.rel.rev.graphics.Visualiser;

public class VisualiserOfOperatorUnwrap extends VisualiserOfOperatorUngroup {
	private static final long serialVersionUID = 1L;
	
	public VisualiserOfOperatorUnwrap(Rev rev, String kind, String name, int xpos, int ypos) {
		super(rev, kind, name, xpos, ypos);
		keyword = "UNWRAP";
	}
	
	protected void save(String allBut, String selections) {
		Visualiser connected = getConnected(operand);
		if (connected == null) {
			return;
		}
		DatabaseAbstractionLayer.updatePreservedStateUnwrap(getRev().getConnection(), getName(), connected.getName(), selections);
	}
	
	protected Tuples load() {
		Tuples tuples = DatabaseAbstractionLayer.getPreservedStateUnwrap(getRev().getConnection(), getName());
		return tuples;
	}
	
	public void populateCustom() {
		super.populateCustom();
	}
	
	public void updateVisualiser() {
		super.updateVisualiser();
	}
	
	/** Override to be notified that this Visualiser is being removed from the Model. */
	public void removing() {
		super.removing();
		DatabaseAbstractionLayer.removeOperator_Unwrap(getRev().getConnection(), getName());
	}
}
