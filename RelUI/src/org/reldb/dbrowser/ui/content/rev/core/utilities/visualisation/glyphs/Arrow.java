/*
 * Arrow.java
 *
 * Created on October 4, 2002, 3:20 AM
 */

package org.reldb.dbrowser.ui.content.rev.core.utilities.visualisation.glyphs;

/**
 * A rudely triangular shape, for creating simple arrows.
 *
 * @author  Dave Voorhis
 */
public class Arrow extends javax.swing.JPanel {
    
	private static final long serialVersionUID = 1L;
	
	public static final int DIRECTION_LEFT = 1;
    public static final int DIRECTION_RIGHT = 2;
    public static final int DIRECTION_UP = 3;
    public static final int DIRECTION_DOWN = 4;
    
    private int Direction = 0;
    private int Size = 0;
    private java.awt.Color theColor = new java.awt.Color(0, 0, 0);
    
    /** Creates a new instance of arrow. */
    public Arrow() {
        setOpaque(false);
    }
    
    /** Creates a new instance of arrow. */
    public Arrow(int direction, int size) {
        setOpaque(false);
        setArrow(direction, size);
    }
    
    /** Set the arrow size and direction */
    public void setArrow(int direction, int size) {
        Direction = direction;
        Size = size - 1;
        switch (Direction) {
            case DIRECTION_LEFT:
            case DIRECTION_RIGHT:
                setSize(size + 1, size + 1);
                break;
            case DIRECTION_UP:
            case DIRECTION_DOWN:
                setSize(size + 1, size + 1);
                break;
        }
        repaint();
    }
    
    /** Set the color. */
    public void setColor(java.awt.Color c) {
        theColor = c;
        repaint();
    }
    
    /** Paint. */
    public void paint(java.awt.Graphics g) {
        if (Direction==0 || Size==0 || theColor==null)
            return;
        g.setColor(theColor);
        float m = 0;
        switch (Direction) {
            case DIRECTION_LEFT:
                for (int x=Size; x>=0; x--) {
                    g.drawLine(x, (int)m, x, Size - (int)m);
                    m += 0.5;
                }
                break;
            case DIRECTION_RIGHT:
                for (int x=0; x<Size; x++) {
                    g.drawLine(x, (int)m, x, Size - (int)m);
                    m += 0.5;
                }
                break;
            case DIRECTION_UP:
                for (int y=Size; y>=0; y--) {
                    g.drawLine((int)m, y, Size - (int)m, y);
                    m += 0.5;
                }
                break;
            case DIRECTION_DOWN:
                for (int y=0; y<Size; y++) {
                    g.drawLine((int)m, y, Size - (int)m, y);
                    m += 0.5;
                }
                break;
        }        
    }
}
