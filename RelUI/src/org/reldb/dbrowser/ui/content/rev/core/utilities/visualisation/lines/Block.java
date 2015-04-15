/*
 * Block.java
 *
 * A a solid, coloured, rectangle widget.  Used to draw lines, etc.
 *
 * Created on July 18, 2002, 3:24 PM
 */

package org.reldb.dbrowser.ui.content.rev.core.utilities.visualisation.lines;

/**
 * 
 * @author  Dave Voorhis
 */
public class Block extends javax.swing.JPanel {
    
	private static final long serialVersionUID = 1L;

	public void setBlock(int xpos, int ypos, int width, int height) {
        setMinimumSize(new java.awt.Dimension(width, height));
        setMaximumSize(new java.awt.Dimension(width, height));
        setPreferredSize(new java.awt.Dimension(width, height));
        setBounds(xpos, ypos, width, height);
        setLocation(xpos, ypos);
    }
    
    /** Creates a new instance of Block.  Default red colour. */
    public Block(int xpos, int ypos, int width, int height) {
        setBlock(xpos, ypos, width, height);
        setBackground(new java.awt.Color(255, 25, 25));
    }
    
    // Creates a new Block.  Specified colour.
    public Block(int xpos, int ypos, int width, int height, java.awt.Color c) {
        setBlock(xpos, ypos, width, height);
        setBackground(c);
    }
    
    // Creates a new undefined block.  Specified color.
    public Block(java.awt.Color c) {
        setBackground(c);
    }
    
    // Create a new empty block.
    public Block() {
    }
}
