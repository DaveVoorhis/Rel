/*
 * Arrow.java
 *
 * Created on October 4, 2002, 3:20 AM
 */

package org.reldb.dbrowser.ui.content.rev.core.utilities.visualisation.glyphs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * A rudely triangular shape, for creating simple arrows.
 *
 * @author  Dave Voorhis
 */
public class Arrow extends Composite {
	
	public static final int DIRECTION_LEFT = 1;
    public static final int DIRECTION_RIGHT = 2;
    public static final int DIRECTION_UP = 3;
    public static final int DIRECTION_DOWN = 4;
    
    private int direction = 0;
    private int size = 0;
    private Color theColor = new Color(Display.getDefault(), 0, 0, 0);
    
    private void setupPainter() {
		addListener (SWT.Paint, new Listener () {
			@Override
			public void handleEvent (Event e) {
				GC gc = e.gc;
		        if (direction==0 || size==0 || theColor==null)
		            return;
		        gc.setForeground(theColor);
		        float m = 0;
		        switch (direction) {
		            case DIRECTION_LEFT:
		                for (int x=size; x>=0; x--) {
		                    gc.drawLine(x, (int)m, x, size - (int)m);
		                    m += 0.5;
		                }
		                break;
		            case DIRECTION_RIGHT:
		                for (int x=0; x<size; x++) {
		                    gc.drawLine(x, (int)m, x, size - (int)m);
		                    m += 0.5;
		                }
		                break;
		            case DIRECTION_UP:
		                for (int y=size; y>=0; y--) {
		                    gc.drawLine((int)m, y, size - (int)m, y);
		                    m += 0.5;
		                }
		                break;
		            case DIRECTION_DOWN:
		                for (int y=0; y<size; y++) {
		                    gc.drawLine((int)m, y, size - (int)m, y);
		                    m += 0.5;
		                }
		                break;
		        }
			}
		});    	
    }
    
    /** Creates a new instance of arrow. */
    public Arrow(Composite parent) {
    	super(parent, SWT.None);
    	setupPainter();
    }
    
    /** Creates a new instance of arrow. */
    public Arrow(Composite parent, int direction, int size) {
    	super(parent, SWT.None);
    	setupPainter();
        setArrow(direction, size);
    }
    
    /** Set the arrow size and direction */
    public void setArrow(int _direction, int _size) {
        direction = _direction;
        size = _size - 1;
        switch (direction) {
            case DIRECTION_LEFT:
            case DIRECTION_RIGHT:
                setSize(_size + 1, _size + 1);
                break;
            case DIRECTION_UP:
            case DIRECTION_DOWN:
                setSize(_size + 1, _size + 1);
                break;
        }
        redraw();
    }
    
    /** Set the color. */
    public void setColor(Color c) {
        theColor = c;
        redraw();
    }
    
}
