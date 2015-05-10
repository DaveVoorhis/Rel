package org.reldb.dbrowser.ui.content.rev.core;

import org.eclipse.swt.graphics.Rectangle;

public class VisualiserOfRelvar extends VisualiserOfRelation {
	
	private String relvarName;
	
	public VisualiserOfRelvar(Rev rev, String name) {
		super(rev, name);
		relvarName = name;
	}
	
	protected String getQuery() {
		return relvarName;
	}

	public void visualiserMoved() {
		Rectangle bounds = getBounds();
		DatabaseAbstractionLayer.updateRelvarPosition(getRev().getConnection(), getVisualiserName(), relvarName, bounds.x, bounds.y, getRev().getModel().getModelName());
	}
	
	public boolean isRemovable() {
		return false;
	}
	
	public void populateCustom() {
		addShowButton();
		addInvokeButton();
		addEditButton();
	}
	
	/** Override to be notified that this Visualiser is being removed from the Model. */
	public void removing() {
		super.removing();
		DatabaseAbstractionLayer.removeRelvar(getRev().getConnection(), getVisualiserName());
	}
	
}
