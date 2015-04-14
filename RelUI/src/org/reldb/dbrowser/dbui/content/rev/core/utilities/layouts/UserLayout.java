package org.reldb.dbrowser.dbui.content.rev.core.utilities.layouts;

// Custom layout manager for World

import java.awt.*;

public class UserLayout implements LayoutManager, java.io.Serializable {

	private static final long serialVersionUID = 1L;
	
	private int minWidth = 0, minHeight = 0;
    private int preferredWidth = 0, preferredHeight = 0;
    private boolean sizeUnknown = true;

    public UserLayout() {
    }

    /* Required by LayoutManager. */
    public void addLayoutComponent(String name, Component comp) {
    }

    /* Required by LayoutManager. */
    public void removeLayoutComponent(Component comp) {
    }

    private void setSizes(Container parent) {
        int nComps = parent.getComponentCount();
        Dimension d = null;

        // Reset preferred/minimum width and height.
        preferredWidth = 0;
        preferredHeight = 0;
        minWidth = 0;
        minHeight = 0;

        for (int i = 0; i < nComps; i++) {
            Component c = parent.getComponent(i);
            if (c.isVisible()) {
                d = c.getPreferredSize();
                
                int excursionX = c.getX() + (int)Math.max(d.getWidth(), c.getWidth());
                int excursionY = c.getY() + (int)Math.max(d.getHeight(), c.getHeight());
                
                if (excursionX > preferredWidth)
                    preferredWidth = excursionX;
                if (excursionY > preferredHeight)
                    preferredHeight = excursionY;
            }
        }

        minWidth = preferredWidth;
        minHeight = preferredHeight;
        sizeUnknown = false;
    }


    /* Required by LayoutManager. */
    public Dimension preferredLayoutSize(Container parent) {
        Dimension dim = new Dimension(0, 0);

        setSizes(parent);

        // Always add the container's insets!
        Insets insets = parent.getInsets();
        dim.width = preferredWidth 
                    + insets.left + insets.right;
        dim.height = preferredHeight 
                     + insets.top + insets.bottom;

        return dim;
    }

    /* Required by LayoutManager. */
    public Dimension minimumLayoutSize(Container parent) {
        Dimension dim = new Dimension(0, 0);

        setSizes(parent);
        
        // Always add the container's insets!
        Insets insets = parent.getInsets();
        dim.width = minWidth 
                    + insets.left + insets.right;
        dim.height = minHeight 
                     + insets.top + insets.bottom;

        return dim;
    }

    /* Required by LayoutManager. */
    /* 
     * This is called when the panel is first displayed, 
     * and every time its size changes. 
     * Note: You CAN'T assume preferredLayoutSize or 
     * minimumLayoutSize will be called -- in the case 
     * of applets, at least, they probably won't be. 
     */
    public void layoutContainer(Container parent) {
        Insets insets = parent.getInsets();

        // Go through the components' sizes, if neither 
        // preferredLayoutSize nor minimumLayoutSize has 
        // been called.
        if (sizeUnknown) {
            setSizes(parent);
        }
        
        // put 'em out there
        int nComps = parent.getComponentCount();
        for (int i = 0 ; i < nComps ; i++) {
            Component c = parent.getComponent(i);
            if (c.isVisible()) {
                Dimension d = c.getPreferredSize();
                java.awt.Rectangle r = new java.awt.Rectangle(
                    c.getX() + insets.left, 
                    c.getY() + insets.top, 
                    (int)Math.max(c.getWidth(), d.getWidth()), 
                    (int)Math.max(c.getHeight(), d.getHeight()));
                c.setBounds(r);
            }
        }
    }
    
    public String toString() {
        String str = "";
        return getClass().getName() + "[" + str + "]";
    }
}
