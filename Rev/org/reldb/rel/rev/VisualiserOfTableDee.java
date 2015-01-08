package org.reldb.rel.rev;

public class VisualiserOfTableDee extends VisualiserOfOperator {
	private static final long serialVersionUID = 1L; /*what is this one used for and what does 1L mean?*/
	
	public VisualiserOfTableDee(Rev rev, String kind, String name, int xpos, int ypos) {
		super(rev, kind, name, xpos, ypos);
	}
	
	public String getQuery() {
		return "DEE";
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
		DatabaseAbstractionLayer.removeOperator_Project(getRev().getConnection(), getName()); /*should we still remove Operator_Project although this is Table_DEE? same question for Table Dum*/
	}
	
}
