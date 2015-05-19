/*
 * Block.java
 *
 * A a solid, coloured, rectangle widget.  Used to draw lines, etc.
 *
 * Created on July 18, 2002, 3:24 PM
 */

package org.reldb.dbrowser.ui.content.rev.graphics.lines;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;

/**
 * 
 * @author  Dave Voorhis
 */
public class Block extends Composite {
	
	public void setBlock(int xpos, int ypos, int width, int height) {
        setBounds(xpos, ypos, width, height);
        setLocation(xpos, ypos);
    }
    
    // Creates a new Block.  Specified colour.
    public Block(Composite parent, int xpos, int ypos, int width, int height, Color c) {
    	super(parent, SWT.None);
        setBlock(xpos, ypos, width, height);
        setBackground(c);
    }
    
    /** Creates a new instance of Block.  Default black colour. */
    public Block(Composite parent, int xpos, int ypos, int width, int height) {
    	this(parent, xpos, ypos, width, height, new Color(parent.getDisplay(), 0, 0, 0));
    }
    
    // Creates a new undefined block.  Specified color.
    public Block(Composite parent, Color c) {
    	super(parent, SWT.None);
        setBackground(c);
    }
    
    // Creates a new undefined block.  Default colour. */
    public Block(Composite parent) {
    	this(parent, new Color(parent.getDisplay(), 0, 0, 0));
    }
}
