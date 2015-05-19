package org.reldb.dbrowser.ui.content.rev.old_rev.visualisers.operators;

import org.reldb.dbrowser.ui.content.rev.old_rev.DatabaseAbstractionLayer;
import org.reldb.dbrowser.ui.content.rev.old_rev.Rev;
import org.reldb.dbrowser.ui.content.rev.old_rev.graphics.Visualiser;
import org.reldb.rel.client.Tuples;

public class Wrap extends Group {
	
	public Wrap(Rev rev) {
		super(rev);
		keyword = "WRAP";
	}
	
	public Wrap(Rev rev, String name, int xpos, int ypos) {
		super(rev, name, xpos, ypos);
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
