/*
 * Model.java
 *
 * Created on July 17, 2002, 4:00 PM
 */

package org.reldb.dbrowser.ui.content.rev.core.graphics;

import java.util.LinkedList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.reldb.dbrowser.ui.content.rev.core.Rev;
import org.reldb.dbrowser.ui.content.rev.core.visualisers.View;

/**
 * Defines a layered pane in which visualised classes may be manipulated.
 *
 * It may be used on its own or managed by a Shell.
 *
 * JLayeredPane functionality is not yet used, hence it behaves as a JPanel,
 * and Connector/Connection overlapping suffers as a result.
 *
 * @author  Dave Voorhis
 */
public class Model extends ScrolledComposite {

	private Rev rev;

    // Cursors
    private final Cursor cursorWait = new Cursor(getDisplay(), SWT.CURSOR_WAIT);
    private final Cursor cursorMove = new Cursor(getDisplay(), SWT.CURSOR_CROSS);
    private final Cursor cursorDefault = new Cursor(getDisplay(), SWT.CURSOR_ARROW);
    private final Cursor cursorHand = new Cursor(getDisplay(), SWT.CURSOR_HAND);
    
    // visualiser management
    private Visualiser focusVisualiser = null;
    private Visualiser dropCandidate = null;
    private int clickOffsetX = 0;
    private int clickOffsetY = 0;
    private int firstOffsetX = 0;
    private int firstOffsetY = 0;
    private int lastMouseX;
    private int lastMouseY;
    
    // Visualisers under management by this Model
    private java.util.Vector<Visualiser> visualisers = new java.util.Vector<Visualiser>();

    // This invisible panel always appears well below and to the right
    // of the lowest, rightmost Visualiser.  Used to force the editable
    // area to a region outside of any Visualiser.
    private View view = null;
    private View viewOwner = null;
//    private JPanel redRectangle = new JPanel();

    // Changed flag.
    private boolean changed = false;
    private boolean resizable = true;
    private boolean outside = false;
    private boolean outsideLeft = false;
    private boolean outsideRight = false;
    private boolean outsideTop = false;
    private boolean outsideBottom = false;
    
    private String name;
    
    /** Ctor */
    public Model(Rev rev, Composite parent) {
    	super(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
        setupModel();
        this.rev = rev;
    }
    
    public void setModelName(String name) {
    	this.name = name;
    }
    
    public String getModelName() {
    	return name;
    }
    
    /** Refresh display.  This is kludgey but presently necessary. */
    public void refresh() {
    	redraw();
    }
    
    /** Reset 'Changed' flag */
    public void clearChanged() {
        changed = false;
    }
    
    /** get 'Changed' flag */
    public boolean isChanged() {
        return changed;
    }
    
    /** Set 'Changed' flag */
    public void setChanged() {
        changed = true;
    }
    
    public void setResizable(boolean flag) {
    	resizable = flag;
    }
    
    /** Redraw all known arguments. */
    public void redrawArguments() {
    	for (Visualiser visualiser: visualisers)
    		visualiser.redrawArguments();
    }
    
    public void setViewOwner(View view) {
    	this.viewOwner = view;
    }
    public View getViewOwner() {
    	return viewOwner;
    }
    
    /** Return a visualiser, given its visualiser ID.
     * Return null if not found. */
    public Visualiser getVisualiser(long visualiserArgument) {
    	for (Visualiser visualiser: visualisers)
    		if (visualiser.getID() == visualiserArgument)
    			return visualiser;
    	return null;
    }
    
    /** Return a visualiser, given its name.  Return null if not found. */
    public Visualiser getVisualiser(String name) {
    	for (Visualiser visualiser: visualisers)
    		if (visualiser.getVisualiserName().equals(name))
    			return visualiser;
    	return null;
    }
        
    /** Return quantity of visualisers managed by this Model */
    public int getVisualiserCount() {
        return visualisers.size();
    }
    
    /** Get the ith visualiser managed by this Model */
    public Visualiser getVisualiser(int i) {
        return visualisers.get(i);
    }
    
    public LinkedList<Visualiser> getVisualisers() {
    	LinkedList<Visualiser> results = new LinkedList<Visualiser>();
    	for (Object obj: visualisers.toArray()) {
    		Visualiser vis = (Visualiser)obj;
    		results.add(vis);
    	}
    	return results;
    }
    
    /** Get the pane upon which visualisation is drawn */
    public Composite getModelPane() {
        return this;
    }
    
    /** Add a Visualiser to the Model. */
    public void addVisualiser(Visualiser v) {
        setCursor(cursorWait);
        visualisers.add(v);
        setModelDimensionsToInclude(v);
        v.setBounds(lastMouseX, lastMouseY, 150, 40);
		moveAbove(null);
        refresh();
        setChanged();
        setCursor(cursorDefault);
    }
    
    /** Remove a Visualiser and its Connections from the Model. */
    public void removeVisualiser(Visualiser v, boolean transfer) {
    	if (v == null)
    		return;
    	if (!transfer) {
    		v.removing();								// notify the visualiser
    		v.setVisible(false);
    		v.removeArguments();                       	// remove connections to visualiser
    	}
        v.dispose();        							// remove visualiser from pane
        visualisers.remove(v);                          // remove visualiser from list of visualisers
        refresh();
        setChanged();
    }
    
    public void removeVisualiser(Visualiser v) {
    	removeVisualiser(v, false);
    }
    
    /** Clear the model. */
    public void removeEverything() {
    	for (Visualiser visualiser: visualisers.toArray(new Visualiser[] {}))
    		removeVisualiser(visualiser);
    }
    
    /** Set effective size of editable area */
    public void setModelDimensions(int width, int height) {
//        lowerRight.setLocation(width, height);        
    }

    /** Set effective size of editable area to include given Visualiser */
    public void setModelDimensionsToInclude(Visualiser v) {
    	if (!resizable) {
    		return;
    	}
//        if (v.getX()>lowerRight.getX() || v.getY()>lowerRight.getY())
//            setModelDimensions(v.getX() + ViewSizeIncrementX, v.getY() + ViewSizeIncrementY);
    }
    
    /** Position view to an absolute position. */
    public void setViewPosition(int x, int y) {
//        getHorizontalScrollBar().setValue(x);
//        getVerticalScrollBar().setValue(y);
    }
    
    /** Position view to a given location relative to the current location. */
    public void setViewPositionOffset(int offsetX, int offsetY) {
//        setViewPosition(offsetX + getHorizontalScrollBar().getValue(),
//                        offsetY + getVerticalScrollBar().getValue());
    }
    
    /** Set up visual environment. */
    protected void setupModel() {
        // User-defined runtime layout.
    //	modelPane.setLayout(new UserLayout());
    	
        // This invisible panel always appears well below and to the right
        // of the lowest, rightmost Visualiser.  Makes editing more palatable
        // when used within a ScrollPane.
     //   modelPane.add(lowerRight);

        // Set up JScrollPane behaviour
     //   setViewportView(modelPane);
     //   setAutoscrolls(true);
       
        // Mouse listeners to enable interactive editing
        addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
                doMousePressed(e);
			}
			@Override
			public void mouseUp(MouseEvent e) {
                doMouseReleased(e);
			}
        });        
        addMouseMoveListener(new MouseMoveListener() {
			@Override
			public void mouseMove(MouseEvent e) {
                lastMouseX = e.x;
                lastMouseY = e.y;
                doMouseDragged(e);
			}
        });
        
        setModelDimensions(320, 320);
        
        //Create a red rectangle for drawing around selections
  //      redRectangle.setBorder(BorderFactory.createLineBorder(Color.red));
   //     redRectangle.setVisible(false);
  //      redRectangle.setBackground(new Color(0, 0, 0, 0));
  //      modelPane.add(redRectangle);
    }
    
    /** Select all. */
    public void doSelectAll() {
        setSelectAll(true);
    }
    
    /** Select none. */
    public void doSelectNone() {
        setSelectAll(false);
    }
    
    /** Invert selections. */
    public void doSelectInvert() {
    	for (Visualiser visualiser: visualisers)
            visualiser.setSelected(!visualiser.isSelected());
    }
    
    /** Delete selections. */
    public void doSelectedDelete() {
        for (Visualiser visualiser: getSelectedAndRemovable())
            removeVisualiser(visualiser);
    }
    
    /** Get the Visualiser currently in focus.  Null if none. */
    public Visualiser getFocusVisualiser() {
        return focusVisualiser;
    }
    
    /** Set all Visualisers to a given selection state. */
    public void setSelectAll(boolean selectAll) {
    	for (Visualiser visualiser: visualisers)
            visualiser.setSelected(selectAll);
    }
    
    /** Set a particular visualiser to be uniquely selected. */
    public void setSelected(Visualiser v) {
        doSelectNone();
        v.setSelected(true);
    }
    
    /** Get count of selections. */
    public int getSelectedCount() {
        int count = 0;
        for (Visualiser visualiser: visualisers)
            if (visualiser.isSelected())
                count++;
        return count;
    }
    
    /** Get array of selected visualisers. */
    public Visualiser[] getSelected() {
        Visualiser v[] = new Visualiser[getSelectedCount()];
        int index = 0;
        for (Visualiser visualiser: visualisers)
            if (visualiser.isSelected())
                v[index++] = visualiser;
        return v;
    }
    
    /** Get array of selected removable visualisers. */
    public Visualiser[] getSelectedAndRemovable() {
        Visualiser v[] = new Visualiser[getSelectedCount()];
        int index = 0;
        for (Visualiser visualiser: visualisers)
            if (visualiser.isSelected() && visualiser.isRemovable())
                v[index++] = visualiser;
        return v;
    }
    
    /** Return number of connections in the entire Model. */
    public int getConnectionCount() {
        int count = 0;
        for (Visualiser visualiser: visualisers)
            count += visualiser.getArgumentCount();
        return count;
    }
    
    /** Move a Visualiser to a given location, adjusting edit area as needed. */
    public void moveVisualiser(Visualiser v, int x, int y) {
        v.setLocation(x, y);
        setModelDimensionsToInclude(v);
        v.redrawArguments();                
    }
    
    // True if a component is a Visualiser.
    private static boolean isVisualiser(Composite c) {
        return (c instanceof Visualiser);
    }
    
    // return the component under the mouse
    private Composite getUnderMouse(MouseEvent evt) {
    	for (Control child: getChildren())
    		if (child.getBounds().contains(evt.x, evt.y))
    			return (Composite)child;
    	return null;
    }
    
    // return the visualiser under the mouse, null if there isn't one
    public Visualiser getVisualiserUnderMouse(MouseEvent evt) {
        Composite underMouse = getUnderMouse(evt);
        return (isVisualiser(underMouse)) ? (Visualiser)underMouse : null;
    }
    
    // Return true if shift key was held during mouse event
    private boolean isShiftKeyHeld(MouseEvent evt) {
    	return (evt.stateMask & SWT.MODIFIER_MASK) != 0;
    }
    
    // Handle mouse press
    private void doMousePressed(MouseEvent evt) {
        focusVisualiser = getVisualiserUnderMouse(evt);
        firstOffsetX = evt.x;
        firstOffsetY = evt.y;
        if (focusVisualiser == null) {
        	System.out.println("Model mousePressed: focusVisualiser is null");
            clickOffsetX = evt.x;
            clickOffsetY = evt.y;
        } else {
        	System.out.println("Model mousePressed: focusVisualiser is not null");
            if (evt.button == 1) {
                if (isShiftKeyHeld(evt))
                    focusVisualiser.setSelected(!focusVisualiser.isSelected());
                else
                    setSelected(focusVisualiser);
            }
            clickOffsetX = evt.x - focusVisualiser.getBounds().x;
            clickOffsetY = evt.y - focusVisualiser.getBounds().y;
        }
    }
    
    // Find first visualiser (bounded by a given coordinate in the Model) that
    // is a compatible drop target for Dragged Visualiser.
    Visualiser getPossibleDropTarget(int x, int y, Visualiser draggedVisualiser) {
        //if (draggedVisualiser.getExposedParameterCount()>0)
            //return null;
        for (Visualiser v: visualisers) {
        	Rectangle bounds = v.getBounds();
            if (v != draggedVisualiser && x >= bounds.x && x <= bounds.x + bounds.width && y >= bounds.y && y <= bounds.y + bounds.height)
                if (v.isDropCandidateFor(draggedVisualiser))
                    return v;
        }
        return null;
    }

    // Handle mouse drag event.
    private void doMouseDragged(MouseEvent evt) {
        // Handle draggables
        int newX = evt.x - clickOffsetX;
        int newY = evt.x - clickOffsetY;
        
        //Modify the panels
        if (isShiftKeyHeld(evt)) {
        	if (focusVisualiser != null) {
        		if (focusVisualiser instanceof View) {
        			view = (View)focusVisualiser;
        		}
        	}
        	//Resize an existing view
        	if (view != null) {
        		//Make sure the view is resizeable (i.e. it is not minimized or maximized)
        		if (!view.getMaximized() && view.getEnabled()) {
	        		int width = evt.x - view.getBounds().x;
	        		int height = evt.y - view.getBounds().y;
	        		view.setSize(width, height);
	        		view.getRev().getModel().setModelDimensions(width, height);
	        		view.updatePositionInDatabase();
        		}
        	}
        	//Draw a red rectangle where the new view will be created
        	else {
        		//Also make sure red rectangles cannot be drawn nested
        		if (rev != null) {
  //      			redRectangle.setVisible(true);
  //      			redRectangle.setLocation(ClickOffsetX, ClickOffsetY);
  //      			redRectangle.setSize(newX, newY);
        		}
        	}
        }
        //Drag the view around
        else if (focusVisualiser==null) {
            setCursor(cursorHand);
            setViewPositionOffset(-newX, -newY);    // move view
        }
        //Drag the visualisers around
        else {
        	//Don't allow maximized views to be moved
        	if (focusVisualiser instanceof View) {
        		View tpView = (View)focusVisualiser;
        		if (tpView.getMaximized()) {
        			return;
        		}
        	}
            setCursor(cursorMove);
            // Move focus visualiser
            if (!focusVisualiser.isSelected())
                setSelected(focusVisualiser);
            int oldX = focusVisualiser.getBounds().x;
            int oldY = focusVisualiser.getBounds().y;
            View owner = focusVisualiser.getRev().getModel().getViewOwner();
            //When its outside, adjust for container location
            if (outside) {
            	newX = oldX;
            	newY = oldY;
            	//Left
            	if (outsideLeft) {
            		newX = oldX - focusVisualiser.getBounds().width;
            		outsideLeft = false;
            	}
            	//Right
            	else if (outsideRight) {
            		newX = oldX + focusVisualiser.getBounds().width;
            		outsideRight = false;
            	}
            	//Top
            	if (outsideTop) {
            		newY = oldY - focusVisualiser.getBounds().height - getBounds().y;
            		outsideTop = false;
            	}
            	//Bottom
            	else if (outsideBottom) {
            		newY = oldY + focusVisualiser.getBounds().height;
            		outsideBottom = false;
            	}
            }
            moveVisualiser(focusVisualiser, newX, newY);
            int deltaX = focusVisualiser.getBounds().x - oldX;
            int deltaY = focusVisualiser.getBounds().y - oldY;
            //Release a visualiser from its container
            LinkedList<Visualiser> justOne = new LinkedList<Visualiser>();
            if (owner != null) {
            	//Only allow enabled boxes to be interacted with
            	if (owner.getEnabled()) {
            		//Outside of the bounds
            		//Left
	            	if (newX < 0) {
	            		outsideLeft = true;
	            	}
	            	//Top
	            	if (newY < 0) {
	            		outsideTop = true;
	            	}
	            	//Right
	            	if (newX + focusVisualiser.getBounds().width > getBounds().width) {
	            		outsideRight = true;
	            	}
	            	//Bottom
	            	if (newY + focusVisualiser.getBounds().height > getBounds().height) {
	            		outsideBottom = true;
	            	}
	            	//Any
	            	if (outsideLeft || outsideRight || outsideTop || outsideBottom) {
	            		outside = true;
	            		justOne.add(focusVisualiser);
	            		focusVisualiser.getRev().getModel().getViewOwner().moveVisualisersToDefault(justOne);
	            	}
            	}
            }
            //Move visualisers
            if (getSelectedCount()>1) {             
            	// move multiple, no d&d
                Visualiser[] moveables = getSelected();
                for (Visualiser moveable: moveables)
                    if (moveable != focusVisualiser)
                        moveVisualiser(moveable, moveable.getBounds().x + deltaX, moveable.getBounds().y + deltaY);
            } else {                                
            	// d&d support
                Visualiser DropTarget = getPossibleDropTarget(evt.x, evt.y, focusVisualiser);
                if (dropCandidate!=null && dropCandidate!=DropTarget)
                    dropCandidate.setDropCandidate(false);
                if (DropTarget!=null) {
                    dropCandidate = DropTarget;
                    dropCandidate.setDropCandidate(true);
                }
            }
            setChanged();
            // Make drag position visible via autoscroll
            //java.awt.Rectangle r = new java.awt.Rectangle(evt.getX(), evt.getY(), 1, 1);
            //modelPane.scrollRectToVisible(r);
        }
    }

    // Handle mouse release event.
    private void doMouseReleased(MouseEvent evt) {
    	//Reset the cursor
    	setCursor(cursorDefault);
    	//Reset the bounds flags
    	outside = false;
    	outsideLeft = false;
    	outsideRight = false;
    	outsideTop = false;
    	outsideBottom = false;
    	//Prevent nesting
    	if (rev == null) {
    		return;
    	}
    	//Reset the red rectangle
//		redRectangle.setVisible(false);
//		redRectangle.setLocation(0, 0);
//		redRectangle.setSize(0, 0);
//		redRectangle.validate();
        //Create a view around the drag position
        if (isShiftKeyHeld(evt)) {
        	//Create a new view
        	if (focusVisualiser != null) {
        		clickOffsetX = firstOffsetX - clickOffsetX;
        		clickOffsetY = firstOffsetY - clickOffsetY;
        	}
        	int newX = evt.x - clickOffsetX;
        	int newY = evt.y - clickOffsetY - 20;
        	String uniqueNumber = null;
        	if (view != null) {
        		uniqueNumber = view.getVisualiserName();
        		rev.removeVisualiser(view, true);
        		view = null;
        	}
        	view = rev.createView(clickOffsetX, clickOffsetY, new Point(newX, newY), uniqueNumber);
        	//Add visualisers within its bounds
        	LinkedList<Visualiser> selected = new LinkedList<Visualiser>();
        	for (Visualiser vis: visualisers) {
        		Point location = vis.getLocation();
        		boolean validPlacement = false;
        		if (vis != view) {
        			if ((location.x >= clickOffsetX && location.y >= clickOffsetY)
        				&& (location.x <= evt.x && location.y <= evt.y)) {
        				//Restrict certain types
        				if (vis instanceof View) {
        					if (View.getNestable()) {
        						validPlacement = true;	
        					}
        				}
        				else {
        					validPlacement = true;
        				}
        			}
        		}
        		//Insert the visualisers
        		if (validPlacement) {
        			selected.add(vis);
        		}
        	}
        	if (view != null) {
        		view.moveVisualisersToModel(selected, true);
        	}
        	view = null;
        }
        //Drop candidate
        else {
        	if (focusVisualiser!=null) {
        		if (dropCandidate!=null) {
        			if (dropCandidate == getPossibleDropTarget(evt.x, evt.y, focusVisualiser) && 
        					dropCandidate.receiveDrop(focusVisualiser))
                        focusVisualiser = dropCandidate;
        			dropCandidate.setDropCandidate(false);
        			dropCandidate = null;
        		}
        		focusVisualiser.redrawArguments();
        		refresh();
        	}
        }
    }
}
