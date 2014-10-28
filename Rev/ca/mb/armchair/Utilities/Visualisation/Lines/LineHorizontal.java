/*
 * LineHorizontal.java
 *
 * Created on July 18, 2002, 3:56 PM
 */

package ca.mb.armchair.Utilities.Visualisation.Lines;

/**
 *
 * @author  Dave Voorhis
 */
public class LineHorizontal extends Block {
    
	private static final long serialVersionUID = 1L;

	/** Creates a new instance of HorizontalLine */
    public LineHorizontal(int x1, int x2, int y, int lineWidth) {
        super((x1 < x2) ? x1 : x2, y, Math.abs(x1 - x2), lineWidth);
    }
    
    // ctor
    public LineHorizontal() {
    }
       
    // Set the line
    public void setLine(int x1, int x2, int y, int lineWidth) {
        setBlock((x1 < x2) ? x1 : x2, y, Math.abs(x1 - x2), lineWidth);
    }
}
