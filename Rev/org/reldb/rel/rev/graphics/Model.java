/*
 * Model.java
 *
 * Created on July 17, 2002, 4:00 PM
 */

package org.reldb.rel.rev.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import org.reldb.rel.rev.Rev;
import org.reldb.rel.rev.VisualiserOfView;

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
public class Model extends javax.swing.JScrollPane {
	
	private static final long serialVersionUID = 1L;

	private Rev rev;
	// Spacing between automatically-placed Visualisers.
    private final int spacing = 20;
    
    // When model size increases, by how much?
    private int ViewSizeIncrementX = 2048;
    private int ViewSizeIncrementY = 2048;

    // Cursors
    private final java.awt.Cursor CursorWait = new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR);
    private final java.awt.Cursor CursorMove = new java.awt.Cursor(java.awt.Cursor.MOVE_CURSOR);
    private final java.awt.Cursor CursorDefault = new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR);
    private final java.awt.Cursor CursorHand = new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR);
    
    // panel upon which Visualisers are drawn
    private javax.swing.JLayeredPane modelPane = new javax.swing.JLayeredPane();
    
    // visualiser management
    private Visualiser FocusVisualiser = null;
    private Visualiser DropCandidate = null;
    private int ClickOffsetX = 0;
    private int ClickOffsetY = 0;
    private int FirstOffsetX = 0;
    private int FirstOffsetY = 0;
    private int RecommendedNewVisualiserX = spacing;
    private int RecommendedNewVisualiserY = spacing;
    
    // Visualisers under management by this Model
    private java.util.Vector<Visualiser> visualisers = new java.util.Vector<Visualiser>();

    // This invisible panel always appears well below and to the right
    // of the lowest, rightmost Visualiser.  Used to force the editable
    // area to a region outside of any Visualiser.
    private javax.swing.JPanel lowerRight = new javax.swing.JPanel();
    private VisualiserOfView view = null;
    private VisualiserOfView viewOwner = null;
    private JPanel redRectangle = new JPanel();

    // Changed flag.
    private boolean changed = false;
    private boolean resizable = true;
    private boolean outside = false;
    private boolean outsideLeft = false;
    private boolean outsideRight = false;
    private boolean outsideTop = false;
    private boolean outsideBottom = false;
    
    /** Ctor */
    public Model() {
        setName("default");
        setupModel();
    }
    
    /** Ctor */
    public Model(String title) {
        setName(title);
        setupModel();
    }
    
    public void setRev(Rev rev) {
    	this.rev = rev;
    }
    
    /** Refresh display.  This is kludgey but presently necessary. */
    public void refresh() {
    	modelPane.validate();
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
    
    public void setViewOwner(VisualiserOfView view) {
    	this.viewOwner = view;
    }
    public VisualiserOfView getViewOwner() {
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
    		if (visualiser.getName().equals(name))
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
    public javax.swing.JLayeredPane getModelPane() {
        return modelPane;
    }
    
    /** Add a Visualiser to the Model. */
    public void addVisualiser(Visualiser v) {
        setCursor(CursorWait);
        visualisers.add(v);
        modelPane.add(v);
        //Make sure views are on the bottom
        //so things can be dragged over them
        if (v instanceof VisualiserOfView) {
        	modelPane.setLayer(v, 0);
        } else {
        	modelPane.setLayer(v, 1);
        }
        v.setModel(this);       // Tell the visualiser which Model owns it
        int vposX = v.getX();   // Keep it from hiding on the upper or left side
        int vposY = v.getY();
        if (vposX<0)
            vposX = spacing;
        if (vposY<0)
            vposY = spacing;
        v.setLocation(vposX, vposY);
        setModelDimensionsToInclude(v);
        refresh();
        RecommendedNewVisualiserX = v.getX() + spacing;    // update suggested position
        RecommendedNewVisualiserY = v.getY() + spacing;
        if (RecommendedNewVisualiserX > modelPane.getWidth() - spacing)
            RecommendedNewVisualiserX = spacing;
        if (RecommendedNewVisualiserY > modelPane.getHeight() - spacing)
            RecommendedNewVisualiserY = spacing;
        setChanged();
        setCursor(CursorDefault);
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
        modelPane.remove((java.awt.Component)v);        // remove visualiser from pane
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
    
    /** Get recommended location for new visualisers. */
    public java.awt.Point getRecommendedNewVisualiserPoint() {
        return new java.awt.Point(RecommendedNewVisualiserX, RecommendedNewVisualiserY);
    }
    
    /** Set effective size of editable area */
    public void setModelDimensions(int width, int height) {
        lowerRight.setLocation(width, height);        
    }

    /** Set effective size of editable area to include given Visualiser */
    public void setModelDimensionsToInclude(Visualiser v) {
    	if (!resizable) {
    		return;
    	}
        if (v.getX()>lowerRight.getX() || v.getY()>lowerRight.getY())
            setModelDimensions(v.getX() + ViewSizeIncrementX, v.getY() + ViewSizeIncrementY);
    }
    
    /** Position view to an absolute position. */
    public void setViewPosition(int x, int y) {
        getHorizontalScrollBar().setValue(x);
        getVerticalScrollBar().setValue(y);
    }
    
    /** Position view to a given location relative to the current location. */
    public void setViewPositionOffset(int offsetX, int offsetY) {
        setViewPosition(offsetX + getHorizontalScrollBar().getValue(),
                        offsetY + getVerticalScrollBar().getValue());
    }
    
    //Set view size increment
    public void setViewSizeIncrement(int x, int y) {
        ViewSizeIncrementX = x;
        ViewSizeIncrementY = y;
    }
    
    /** Set up visual environment. */
    protected void setupModel() {
        // User-defined runtime layout.
    	modelPane.setLayout(new org.reldb.rel.Utilities.Layouts.UserLayout());
    	
        // This invisible panel always appears well below and to the right
        // of the lowest, rightmost Visualiser.  Makes editing more palatable
        // when used within a ScrollPane.
        modelPane.add(lowerRight);

        // Set up JScrollPane behaviour
        setViewportView(modelPane);
        setAutoscrolls(true);
       
        // Mouse listeners to enable interactive editing
        modelPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                doMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                doMouseReleased(evt);
            }
        });        
        modelPane.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                doMouseDragged(evt);
            }
        });
        
        setModelDimensions(320, 320);
        
        //Create a red rectangle for drawing around selections
        redRectangle.setBorder(BorderFactory.createLineBorder(Color.red));
        redRectangle.setVisible(false);
        redRectangle.setBackground(new Color(0, 0, 0, 0));
        modelPane.add(redRectangle);
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
        return FocusVisualiser;
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
    private static boolean isVisualiser(java.awt.Component c) {
        return (c instanceof Visualiser);
    }
    
    // return the component under the mouse
    private java.awt.Component getUnderMouse(java.awt.event.MouseEvent evt) {
        return modelPane.getComponentAt(evt.getPoint());
    }
    
    // return the visualiser under the mouse, null if there isn't one
    public Visualiser getVisualiserUnderMouse(java.awt.event.MouseEvent evt) {
        java.awt.Component underMouse = getUnderMouse(evt);
        return (isVisualiser(underMouse)) ? (Visualiser)underMouse : null;
    }
    
    // Return true if shift key was held during mouse event
    private boolean isShiftKeyHeld(java.awt.event.MouseEvent evt) {
        int onmask = java.awt.event.MouseEvent.SHIFT_DOWN_MASK;
        int offmask = 0;
        return ((evt.getModifiersEx() & (onmask | offmask)) == onmask);
    }
    
    // Handle mouse press
    private void doMousePressed(java.awt.event.MouseEvent evt) {
        FocusVisualiser = getVisualiserUnderMouse(evt);
        FirstOffsetX = evt.getX();
        FirstOffsetY = evt.getY();
        if (FocusVisualiser==null) {
            ClickOffsetX = evt.getX();
            ClickOffsetY = evt.getY();
        } else {
            if (evt.getButton()==1)
                if (isShiftKeyHeld(evt))
                    FocusVisualiser.setSelected(!FocusVisualiser.isSelected());
                else
                    setSelected(FocusVisualiser);
            ClickOffsetX = evt.getX() - FocusVisualiser.getX();
            ClickOffsetY = evt.getY() - FocusVisualiser.getY();
        }
    }
    
    // Find first visualiser (bounded by a given coordinate in the Model) that
    // is a compatible drop target for Dragged Visualiser.
    private Visualiser getPossibleDropTarget(int x, int y, Visualiser draggedVisualiser) {
        //if (draggedVisualiser.getExposedParameterCount()>0)
            //return null;
        for (Visualiser v: visualisers)
            if (v != draggedVisualiser && x >= v.getX() && x <= v.getX() + v.getWidth() && y >= v.getY() && y <= v.getY() + v.getHeight())
                if (v.isDropCandidateFor(draggedVisualiser))
                    return v;
        return null;
    }

    // Handle mouse drag event.
    private void doMouseDragged(java.awt.event.MouseEvent evt) {
        // Handle draggables
        int newX = evt.getX() - ClickOffsetX;
        int newY = evt.getY() - ClickOffsetY;
        
        //Modify the panels
        if (isShiftKeyHeld(evt)) {
        	if (FocusVisualiser != null) {
        		if (FocusVisualiser instanceof VisualiserOfView) {
        			view = (VisualiserOfView)FocusVisualiser;
        		}
        	}
        	//Resize an existing view
        	if (view != null) {
        		//Make sure the view is resizeable (i.e. it is not minimized or maximized)
        		if (!view.getMaximized() && view.getEnabled()) {
	        		int width = evt.getX() - view.getX();
	        		int height = evt.getY() - view.getY();
	        		view.setSize(width, height);
	        		view.getModel().setModelDimensions(width, height);
	        		view.updatePositionInDatabase();
        		}
        	}
        	//Draw a red rectangle where the new view will be created
        	else {
        		//Also make sure red rectangles cannot be drawn nested
        		if (rev != null) {
        			redRectangle.setVisible(true);
        			redRectangle.setLocation(ClickOffsetX, ClickOffsetY);
        			redRectangle.setSize(newX, newY);
        		}
        	}
        }
        //Drag the view around
        else if (FocusVisualiser==null) {
            setCursor(CursorHand);
            setViewPositionOffset(-newX, -newY);    // move view
        }
        //Drag the visualisers around
        else {
        	//Don't allow maximized views to be moved
        	if (FocusVisualiser instanceof VisualiserOfView) {
        		VisualiserOfView tpView = (VisualiserOfView)FocusVisualiser;
        		if (tpView.getMaximized()) {
        			return;
        		}
        	}
            setCursor(CursorMove);
            // Move focus visualiser
            if (!FocusVisualiser.isSelected())
                setSelected(FocusVisualiser);
            int oldX = FocusVisualiser.getX();
            int oldY = FocusVisualiser.getY();
            VisualiserOfView owner = FocusVisualiser.getModel().getViewOwner();
            //When its outside, adjust for container location
            if (outside) {
            	newX = oldX;
            	newY = oldY;
            	//Left
            	if (outsideLeft) {
            		newX = oldX - FocusVisualiser.getWidth();
            		outsideLeft = false;
            	}
            	//Right
            	else if (outsideRight) {
            		newX = oldX + FocusVisualiser.getWidth();
            		outsideRight = false;
            	}
            	//Top
            	if (outsideTop) {
            		newY = oldY - FocusVisualiser.getHeight() - getY();
            		outsideTop = false;
            	}
            	//Bottom
            	else if (outsideBottom) {
            		newY = oldY + FocusVisualiser.getHeight();
            		outsideBottom = false;
            	}
            }
            moveVisualiser(FocusVisualiser, newX, newY);
            int deltaX = FocusVisualiser.getX() - oldX;
            int deltaY = FocusVisualiser.getY() - oldY;
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
	            	if (newX + FocusVisualiser.getWidth() > getWidth()) {
	            		outsideRight = true;
	            	}
	            	//Bottom
	            	if (newY + FocusVisualiser.getHeight() > getHeight()) {
	            		outsideBottom = true;
	            	}
	            	//Any
	            	if (outsideLeft || outsideRight || outsideTop || outsideBottom) {
	            		outside = true;
	            		justOne.add(FocusVisualiser);
	            		FocusVisualiser.getModel().getViewOwner().moveVisualisersToDefault(justOne);
	            	}
            	}
            }
            //Move visualisers
            if (getSelectedCount()>1) {             
            	// move multiple, no d&d
                Visualiser[] moveables = getSelected();
                for (Visualiser moveable: moveables)
                    if (moveable != FocusVisualiser)
                        moveVisualiser(moveable, moveable.getX() + deltaX, moveable.getY() + deltaY);
            } else {                                
            	// d&d support
                Visualiser DropTarget = getPossibleDropTarget(evt.getX(), evt.getY(), FocusVisualiser);
                if (DropCandidate!=null && DropCandidate!=DropTarget)
                    DropCandidate.setDropCandidate(false);
                if (DropTarget!=null) {
                    DropCandidate = DropTarget;
                    DropCandidate.setDropCandidate(true);
                }
            }
            setChanged();
            // Make drag position visible via autoscroll
            //java.awt.Rectangle r = new java.awt.Rectangle(evt.getX(), evt.getY(), 1, 1);
            //modelPane.scrollRectToVisible(r);
        }
    }

    // Handle mouse release event.
    private void doMouseReleased(java.awt.event.MouseEvent evt) {
    	//Reset the cursor
    	setCursor(CursorDefault);
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
		redRectangle.setVisible(false);
		redRectangle.setLocation(0, 0);
		redRectangle.setSize(0, 0);
		redRectangle.validate();
        //Create a view around the drag position
        if (isShiftKeyHeld(evt)) {
        	//Create a new view
        	if (FocusVisualiser != null) {
        		ClickOffsetX = FirstOffsetX - ClickOffsetX;
        		ClickOffsetY = FirstOffsetY - ClickOffsetY;
        	}
        	int newX = evt.getX() - ClickOffsetX;
        	int newY = evt.getY() - ClickOffsetY - 20;
        	String uniqueNumber = null;
        	if (view != null) {
        		uniqueNumber = view.getName();
        		rev.removeVisualiser(view, true);
        		view = null;
        	}
        	view = rev.createView(ClickOffsetX, ClickOffsetY, new Dimension(newX, newY), uniqueNumber);
        	//Add visualisers within its bounds
        	LinkedList<Visualiser> selected = new LinkedList<Visualiser>();
        	for (Visualiser vis: visualisers) {
        		Point location = vis.getLocation();
        		boolean validPlacement = false;
        		if (vis != view) {
        			if ((location.x >= ClickOffsetX && location.y >= ClickOffsetY)
        				&& (location.x <= evt.getX() && location.y <= evt.getY())) {
        				//Restrict certain types
        				if (vis instanceof VisualiserOfView) {
        					if (VisualiserOfView.getNestable()) {
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
        	if (FocusVisualiser!=null) {
        		if (DropCandidate!=null) {
        			if (DropCandidate == getPossibleDropTarget(evt.getX(), evt.getY(), FocusVisualiser) && 
        					DropCandidate.receiveDrop(FocusVisualiser))
                        FocusVisualiser = DropCandidate;
        			DropCandidate.setDropCandidate(false);
        			DropCandidate = null;
        		}
        		FocusVisualiser.redrawArguments();
        		refresh();
        	}
        }
    }
}
