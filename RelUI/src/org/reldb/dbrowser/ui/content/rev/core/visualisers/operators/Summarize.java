package org.reldb.dbrowser.ui.content.rev.core.visualisers.operators;

import org.reldb.dbrowser.ui.content.rev.core.DatabaseAbstractionLayer;
import org.reldb.dbrowser.ui.content.rev.core.Rev;
import org.reldb.dbrowser.ui.content.rev.core.graphics.Visualiser;
import org.reldb.rel.client.Tuples;

public class Summarize extends Extend {
	
	public Summarize(Rev rev) {
		super(rev);
		KeyWord = "SUMMARIZE";
	}
	
	public Summarize(Rev rev, String name, int xpos, int ypos) {
		super(rev, name, xpos, ypos);
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
