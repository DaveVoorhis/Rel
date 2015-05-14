package org.reldb.dbrowser.ui.content.rev.core2;

import org.eclipse.swt.graphics.Point;

public class Relvar extends Visualiser {

	public Relvar(Rev rev, String id, String name, int x, int y) {
		super(rev.getModel(), id, name, x, y);
	}

	protected void visualiserMoved() {
		super.visualiserMoved();
		Point location = getLocation();
		DatabaseAbstractionLayer.updateRelvarPosition(getModel().getConnection(), getID(), getTitle(), location.x, location.y, getModel().getModelName());
	}

	public String getQuery() {
		return getTitle();
	}
	
    public String toString() {
    	return "Relvar " + getTitle() + " (" + getID() + ")";
    }

}
