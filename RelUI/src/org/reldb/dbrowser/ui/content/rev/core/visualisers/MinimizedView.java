package org.reldb.dbrowser.ui.content.rev.core.visualisers;

import org.reldb.dbrowser.ui.content.rev.core.Rev;
import org.reldb.dbrowser.ui.content.rev.core.graphics.Visualiser;

public class MinimizedView extends Relvar {
	
	private View view;
	
	public MinimizedView(Rev rev, String name) {
		super(rev, name);
	}
	
	public String getQuery() {
		restore();
		return getVisualiserName();
	}
	
	public void setView(View view) {
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
	
	public void visualiserMoved() {
		//Don't allow it to be moved by in restored mode
		if (!this.isVisible()) {
			return;
		}
		//Move the main views when in minimized mode
		if (view != null) {
			view.setLocation(getLocation());
			view.setCacheLocation(getLocation());
		}
	}
	
	public void populateCustom() {
		addInvokeButton();
	}
}