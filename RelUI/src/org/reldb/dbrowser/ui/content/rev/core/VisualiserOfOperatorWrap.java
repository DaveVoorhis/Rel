package org.reldb.dbrowser.ui.content.rev.core;

import org.reldb.dbrowser.ui.content.rev.core.graphics.Visualiser;
import org.reldb.rel.client.Tuples;

public class VisualiserOfOperatorWrap extends VisualiserOfOperatorGroup {
	
	public VisualiserOfOperatorWrap(Rev rev) {
		super(rev);
		keyword = "WRAP";
	}
	
	public VisualiserOfOperatorWrap(Rev rev, String name) {
		super(rev, name);
		keyword = "WRAP";
	}
	
	protected Tuples load() {
		Tuples tuples = DatabaseAbstractionLayer.getPreservedStateWrap(getRev().getConnection(), getVisualiserName());
		return tuples;
	}
	
	protected void save(String allBut, String selections, String asString) {
		Visualiser connected = getConnected(operand);
		if (connected == null) {
			return;
		}
		DatabaseAbstractionLayer.updatePreservedStateWrap(getRev().getConnection(), getVisualiserName(), connected.getVisualiserName(), allBut, selections, asString);
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
		DatabaseAbstractionLayer.removeOperator_Wrap(getRev().getConnection(), getVisualiserName());
	}
}
