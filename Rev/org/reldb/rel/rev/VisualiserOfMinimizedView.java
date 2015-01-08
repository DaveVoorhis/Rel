package org.reldb.rel.rev;

import org.reldb.rel.rev.graphics.Visualiser;

public class VisualiserOfMinimizedView extends VisualiserOfRelvar {
	private static final long serialVersionUID = 1L;
	
	private VisualiserOfView view;
	
	public VisualiserOfMinimizedView(Rev rev, String name) {
		super(rev, name);
	}
	
	protected String getQuery() {
		restore();
		return getName();
	}
	
	public void setView(VisualiserOfView view) {
		this.view = view;
	}
	
	private void restore() {
		if (view != null) {
			this.setVisible(false);
			view.restore();
		}
	}
	
	public boolean isRemovable() {
		return false;
	}
	
    public boolean isDropCandidateFor(Visualiser draggedVisualiser) {
    	return false;
    }
	
	public void moved() {
		//Don't allow it to be moved by in restored mode
		if (!this.isVisible()) {
			return;
		}
		//Move the main views when in minimized mode
		if (view != null) {
			view.setLocation(this.getLocation());
			view.setCacheLocation(this.getLocation());
		}
	}
	
	public void populateCustom() {
		addInvokeButton();
	}
	
	/** Override to be notified that this Visualiser is being removed from the Model. */
	public void removing() {
		super.removing();
	}	
}