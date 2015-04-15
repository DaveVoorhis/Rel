package org.reldb.dbrowser.ui.content.rev.core;

import org.reldb.dbrowser.ui.content.rev.core.graphics.Model;
import org.reldb.dbrowser.ui.content.rev.core.graphics.Visualiser;

public class VisualiserOfOperand extends Visualiser {
	private static final long serialVersionUID = 1L;

	protected VisualiserOfOperand(Model model, String name, int xpos, int ypos) {
		super(model);
		setName(name);
		setLocation(xpos, ypos);
	}
	
    /** True if this Visualiser is wholly owned by its parameter and should 
     * be deleted along with its Argument. */
    public boolean isOwnedByParameter() {
        return true;
    }

	/** Return true if a given visualiser can be dropped on this one, with something
    good possibly taking place thereafter via a receiveDrop() operation. */
	public boolean isDropCandidateFor(Visualiser draggedVisualiser) {
		if (getArgumentCount() != 1) {
			System.out.println("VisualiserOfOperand: Argument count is wrong.  It should be 1, i.e., I should be an argument to only one parameter, but I'm not.");
			return false;
		}
		if (getArgument(0).getVisualiser() == draggedVisualiser)
			return false;
		return (!(draggedVisualiser instanceof VisualiserOfOperand));
	}
    
    /** Drop a visualiser on this one.  Return true if succeeded. */
    public boolean receiveDrop(Visualiser draggedVisualiser) {
    	return attachAndDelete(draggedVisualiser, this);
    }
	
}
