package org.reldb.dbrowser.dbui.content.rev.core;

import org.reldb.rel.client.Tuples;
import org.reldb.rel.rev.graphics.Visualiser;

public class VisualiserOfOperatorWrap extends VisualiserOfOperatorGroup {
	private static final long serialVersionUID = 1L;
	
	public VisualiserOfOperatorWrap(Rev rev, String kind, String name, int xpos, int ypos) {
		super(rev, kind, name, xpos, ypos);
		keyword = "WRAP";
	}
	
	protected Tuples load() {
		Tuples tuples = DatabaseAbstractionLayer.getPreservedStateWrap(getRev().getConnection(), getName());
		return tuples;
	}
	
	protected void save(String allBut, String selections, String asString) {
		Visualiser connected = getConnected(operand);
		if (connected == null) {
			return;
		}
		DatabaseAbstractionLayer.updatePreservedStateWrap(getRev().getConnection(), getName(), connected.getName(), allBut, selections, asString);
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
		DatabaseAbstractionLayer.removeOperator_Wrap(getRev().getConnection(), getName());
	}
}
