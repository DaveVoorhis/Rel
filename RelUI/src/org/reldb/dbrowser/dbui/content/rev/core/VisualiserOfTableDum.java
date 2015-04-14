package org.reldb.dbrowser.dbui.content.rev.core;



public class VisualiserOfTableDum extends VisualiserOfOperator {
	private static final long serialVersionUID = 1L;
	

	
	public VisualiserOfTableDum(Rev rev, String kind, String name, int xpos, int ypos) {
		super(rev, kind, name, xpos, ypos);
	}

	public String getQuery() {
		return "DUM"; 
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
		DatabaseAbstractionLayer.removeOperator_Project(getRev().getConnection(), getName());
	}
	
}
