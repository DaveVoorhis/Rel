/*
 * Glyph.java
 *
 * Created on October 4, 2002, 2:15 AM
 */

package org.reldb.dbrowser.ui.content.rev.core.utilities.visualisation.glyphs;

/**
 *
 * @author  Dave Voorhis
 */
public class Glyph extends javax.swing.JPanel {
    
	private static final long serialVersionUID = 1L;
	
	private java.awt.Polygon theShape;
    private java.awt.Color theColor;
    
    /** Creates a glyph. */
    public Glyph() {
        theShape = null;
        theColor = null;
        setOpaque(false);
    }
    
    /** Creates a glyph of given shape and color. */
    public Glyph(java.awt.Polygon p, java.awt.Color c) {
        setOpaque(false);
        setPolygon(p, c);
    }
    
    /** Set the polygon & color to display. */
    public void setPolygon(java.awt.Polygon p, java.awt.Color c) {
        setColor(c);
        setPolygon(p);
    }
    
    /** Set the polygon to display. */
    public void setPolygon(java.awt.Polygon p) {
        theShape = p;
        setSize(theShape.getBounds().getSize());
        repaint();
    }
    
    /** Set the color. */
    public void setColor(java.awt.Color c) {
        theColor = c;
        repaint();
    }
    
    /** Paint. */
    public void paint(java.awt.Graphics g) {
        if (theShape==null || theColor==null)
            return;
        g.setColor(theColor);
        g.fillPolygon(theShape);
    }
}
