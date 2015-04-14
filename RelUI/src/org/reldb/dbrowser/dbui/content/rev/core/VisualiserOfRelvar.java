package org.reldb.dbrowser.dbui.content.rev.core;


public class VisualiserOfRelvar extends VisualiserOfRel {
	private static final long serialVersionUID = 1L;
	
	public VisualiserOfRelvar(Rev rev, String name) {
		super(rev, name);
	}
	
	protected String getQuery() {
		return getName();
	}
	
	public void moved() {
		DatabaseAbstractionLayer.updateRelvarPosition(getRev().getConnection(), getName(), getX(), getY(), getModel().getName());
	}
	
	public boolean isRemovable() {
		return false;
	}
	
	public void populateCustom() {
		addInvokeButton();
		addEditButton();
	}
	
	/** Override to be notified that this Visualiser is being removed from the Model. */
	public void removing() {
		super.removing();
		DatabaseAbstractionLayer.removeRelvar(getRev().getConnection(), getName());
	}
	
}
