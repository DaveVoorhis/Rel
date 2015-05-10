package org.reldb.dbrowser.ui.content.rev.core;

import org.reldb.dbrowser.ui.content.rev.core.graphics.Visualiser;
import org.reldb.rel.client.Tuples;

public class VisualiserOfOperatorSummarize extends VisualiserOfOperatorExtend {
	
	public VisualiserOfOperatorSummarize(Rev rev) {
		super(rev);
		KeyWord = "SUMMARIZE";
	}
	
	public VisualiserOfOperatorSummarize(Rev rev, String name) {
		super(rev, name);
		KeyWord = "SUMMARIZE";
	}
	
	protected void save(String save) {
		Visualiser connected = getConnected(operand);
		if (connected == null) {
			return;
		}
		DatabaseAbstractionLayer.updatePreservedStateSummarize(getRev().getConnection(), getVisualiserName(), connected.getVisualiserName(), save);
	}
	
	protected Tuples load() {
		Tuples tuples = DatabaseAbstractionLayer.getPreservedStateSummarize(getRev().getConnection(), getVisualiserName());
		return tuples;
	}
	
	/** Override to be notified that this Visualiser is being removed from the Model. */
	public void removing() {
		super.removing();
		DatabaseAbstractionLayer.removeOperator_Summarize(getRev().getConnection(), getVisualiserName());
	}
}
