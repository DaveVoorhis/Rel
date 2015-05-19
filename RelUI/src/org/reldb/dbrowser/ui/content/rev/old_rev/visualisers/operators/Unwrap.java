package org.reldb.dbrowser.ui.content.rev.old_rev.visualisers.operators;

import org.reldb.dbrowser.ui.content.rev.old_rev.DatabaseAbstractionLayer;
import org.reldb.dbrowser.ui.content.rev.old_rev.Rev;
import org.reldb.dbrowser.ui.content.rev.old_rev.graphics.Visualiser;
import org.reldb.rel.client.Tuples;

public class Unwrap extends Ungroup {
	
	public Unwrap(Rev rev) {
		super(rev);
		keyword = "UNWRAP";
	}
	
	public Unwrap(Rev rev, String name, int xpos, int ypos) {
		super(rev, name, xpos, ypos);
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
