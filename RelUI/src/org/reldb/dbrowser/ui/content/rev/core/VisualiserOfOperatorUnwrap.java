package org.reldb.dbrowser.ui.content.rev.core;

import org.reldb.dbrowser.ui.content.rev.core.graphics.Visualiser;
import org.reldb.rel.client.Tuples;

public class VisualiserOfOperatorUnwrap extends VisualiserOfOperatorUngroup {
	
	public VisualiserOfOperatorUnwrap(Rev rev) {
		super(rev);
		keyword = "UNWRAP";
	}
	
	public VisualiserOfOperatorUnwrap(Rev rev, String name) {
		super(rev, name);
		keyword = "UNWRAP";
	}
	
	protected void save(String allBut, String selections) {
		Visualiser connected = getConnected(operand);
		if (connected == null) {
			return;
		}
		DatabaseAbstractionLayer.updatePreservedStateUnwrap(getRev().getConnection(), getVisualiserName(), connected.getVisualiserName(), selections);
	}
	
	protected Tuples load() {
		Tuples tuples = DatabaseAbstractionLayer.getPreservedStateUnwrap(getRev().getConnection(), getVisualiserName());
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
		DatabaseAbstractionLayer.removeOperator_Unwrap(getRev().getConnection(), getVisualiserName());
	}
}
