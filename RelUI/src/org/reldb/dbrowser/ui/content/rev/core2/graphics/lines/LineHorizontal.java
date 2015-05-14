/*
 * LineHorizontal.java
 *
 * Created on July 18, 2002, 3:56 PM
 */

package org.reldb.dbrowser.ui.content.rev.core2.graphics.lines;

import org.eclipse.swt.widgets.Composite;

/**
 *
 * @author  Dave Voorhis
 */
public class LineHorizontal extends Block {

    public LineHorizontal(Composite parent, int x1, int x2, int y, int lineWidth) {
        super(parent, (x1 < x2) ? x1 : x2, y, Math.abs(x1 - x2), lineWidth);
    }

    public LineHorizontal(Composite parent) {
    	super(parent);
    }
    
    // Set the line
    public void setLine(int x1, int x2, int y, int lineWidth) {
        setBlock((x1 < x2) ? x1 : x2, y, Math.abs(x1 - x2), lineWidth);
    }
}
