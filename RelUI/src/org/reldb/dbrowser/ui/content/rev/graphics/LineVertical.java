/*
 * LineVertical.java
 *
 * Created on July 18, 2002, 3:56 PM
 */

package org.reldb.dbrowser.ui.content.rev.graphics;

import org.eclipse.swt.widgets.Composite;

/**
 *
 * @author  Dave Voorhis
 */
public class LineVertical extends Block {

    public LineVertical(Composite parent, int x, int y1, int y2, int lineWidth) {
        super(parent, x, (y1<y2) ? y1 : y2, lineWidth, Math.abs(y1 - y2));
    }
    
    public LineVertical(Composite parent) {
    	super(parent);
    }
    
    // Set the line
    public void setLine(int x, int y1, int y2, int lineWidth) {
        setBlock(x, (y1<y2) ? y1 : y2, lineWidth, Math.abs(y1 - y2));
    }
}
