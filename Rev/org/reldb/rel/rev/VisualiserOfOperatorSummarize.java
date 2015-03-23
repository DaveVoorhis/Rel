package org.reldb.rel.rev;

import org.reldb.rel.client.Tuples;
import org.reldb.rel.rev.graphics.Visualiser;

public class VisualiserOfOperatorSummarize extends VisualiserOfOperatorExtend {
	private static final long serialVersionUID = 1L;
	
	public VisualiserOfOperatorSummarize(Rev rev, String kind, String name, int xpos, int ypos) {
		super(rev, kind, name, xpos, ypos);
		KeyWord = "SUMMARIZE";
	}
	
	protected void save(String save) {
		Visualiser connected = getConnected(operand);
		if (connected == null) {
			return;
		}
		DatabaseAbstractionLayer.updatePreservedStateSummarize(getRev().getConnection(), getName(), connected.getName(), save, getRev().getCrashHandler());
	}
	
	protected Tuples load() {
		Tuples tuples = DatabaseAbstractionLayer.getPreservedStateSummarize(getRev().getConnection(), getName(), getRev().getCrashHandler());
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
		DatabaseAbstractionLayer.removeOperator_Summarize(getRev().getConnection(), getName(), getRev().getCrashHandler());
	}
}
