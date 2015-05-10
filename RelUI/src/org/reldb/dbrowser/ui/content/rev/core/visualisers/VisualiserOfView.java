package org.reldb.dbrowser.ui.content.rev.core.visualisers;

import java.util.LinkedList;

import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.ToolBar;
import org.reldb.dbrowser.ui.content.rev.core.DatabaseAbstractionLayer;
import org.reldb.dbrowser.ui.content.rev.core.Rev;
import org.reldb.dbrowser.ui.content.rev.core.graphics.Argument;
import org.reldb.dbrowser.ui.content.rev.core.graphics.Model;
import org.reldb.dbrowser.ui.content.rev.core.graphics.Parameter;
import org.reldb.dbrowser.ui.content.rev.core.graphics.Visualiser;

public class VisualiserOfView extends VisualiserOfRelation {
	private Point location;
	private Point size;
	private boolean enabled;
	private boolean stored;
	private boolean deleting = false;
	private Model controlPanel;
	private static boolean nestable = false;
	private LinkedList<Visualiser> tempList = new LinkedList<Visualiser>();
	private MinimizedView minimizedView;
	private MouseListener popupListener;
	private ToolBar toolBar;
	private int toolbarOffset = 28;
	private int scrollbarWidth = 40;
	private boolean maximized = false;
	//Icons
    private final String MinimizeIconFile = "org/reldb/rel/resources/minimize.png";
    private final String MaximizeIconFile = "org/reldb/rel/resources/maximize.png";
    private final String StoreIconFile = "org/reldb/rel/resources/store.png";
    private final String DeleteIconFile = "org/reldb/rel/resources/delete.png";
    private javax.swing.ImageIcon MinimizeIcon = null;
    private javax.swing.ImageIcon MaximizeIcon = null;
    private javax.swing.ImageIcon StoreIcon = null;
    private javax.swing.ImageIcon DeleteIcon = null;
	
	public VisualiserOfView(Rev rev, String kind, String name, int xpos, int ypos) {
		this(rev, kind, name, xpos, ypos, 300, 300, true);
	}
	
	public VisualiserOfView(Rev rev, String kind, String name, int xpos, int ypos, int width, int height, boolean enabled) {
		super(rev, name);
		//Set size
		this.size = new Point(width, height);
		this.setSize(this.size);
		//Set location
		this.location = new Point(xpos, ypos);
		this.setLocation(this.location);
		//Set enabled
		this.enabled = enabled;
		this.stored = false;
		controlPanel.setResizable(false);
		controlPanel.setVisualiserName(name);
		int modelWidth = size.width - controlPanel.getWidth();
		int modelHeight = size.height - toolbarOffset;
		controlPanel.setModelDimensions(modelWidth, modelHeight);
		//Set up popup handler
		popupListener = new PopupListener();
		controlPanel.getModelPane().addMouseListener(popupListener);
	}
	
	private int popupX;
	private int popupY;
	
	private class PopupListener extends MouseAdapter {
	    public void mousePressed(MouseEvent e) {
	        maybeShowPopup(e);
	    }
	    public void mouseReleased(MouseEvent e) {
	        maybeShowPopup(e);
	    }
	    private void maybeShowPopup(MouseEvent e) {
			if (getModel().getVisualiserUnderMouse(e) == null) {
				return;
			}
	        if (e.isPopupTrigger()) {
	        	popupX = e.getX();
	        	popupY = e.getY();
	            getRev().getPopup().show(e.getComponent(), popupX, popupY);
	        }
	    }
	}
	
	public void setMinimized(MinimizedView view) {
		minimizedView = view;
	}
	
	public void updatePositionInDatabase() {
		if (!deleting && !maximized) {
			DatabaseAbstractionLayer.updateViewPosition(getRev().getConnection(), getVisualiserName(), this.location.x, this.location.y, this.size.width, this.size.height, enabled, stored);
		}
	}
	
	//private PreservedState getPreservedState() {
		//return preservedState;
	//}
	
	//private void updatePreservedState() {
		//DatabaseAbstractionLayer.updatePreservedStateRename(getRev().getConnection(), getName(), selections);
	//}
	
	public void addTemp(Visualiser vis) {
		tempList.add(vis);
	}
	
	public void commitTempList() {
		moveVisualisersToModel(tempList, false);
		tempList.clear();
	}
	
	public static boolean getNestable() {
		return nestable;
	}
	
	protected String getQuery() {
		return null;
	}
	
	//Different than isEnabled() which is the actual component
	public boolean getEnabled() {
		return enabled;
	}
	
	public Model getViewModel() {
		return controlPanel;
	}
	
    public void moveVisualisersToModel(LinkedList<Visualiser> visualisers, boolean move) {
    	for (Visualiser visualiser: visualisers) {
    		if (move) {
    			int offsetX = visualiser.getX() - getX() - controlPanel.getX();
    			int offsetY = visualiser.getY() - getY() - controlPanel.getY();
    			visualiser.setLocation(offsetX, offsetY);
    		}
	    	getModel().removeVisualiser(visualiser, true);
	    	controlPanel.addVisualiser(visualiser);
	    	visualiser.visualiserMoved();
	    	//Move the arguments to the model
	    	if (visualiser instanceof Operator) {
	    		moveArgumentToModel(visualiser);
	    	}
    	}
    }
    
	public void moveVisualisersToDefault(LinkedList<Visualiser> visualisers) {
    	for (Visualiser visualiser: visualisers) {
			int offSetX = visualiser.getX() + getX() + controlPanel.getX();
	    	int offsetY = visualiser.getY() + getY() + controlPanel.getY();
	    	visualiser.setLocation(offSetX, offsetY);
	    	controlPanel.removeVisualiser(visualiser, true);
	    	getModel().addVisualiser(visualiser);
	    	visualiser.visualiserMoved();
	    	//Move the arguments to the model
	    	if (visualiser instanceof Operator) {
	    		moveArgumentToModel(visualiser);
	    	}
    	}
	}
	
	public void moveArgumentToModel(Visualiser visualiser) {
		long parCount = visualiser.getParameterCount();
    	for (int i=0; i < parCount; i++) {
			Parameter parameter = visualiser.getParameter(i);
			Visualiser operand = parameter.getConnection(0).getVisualiser();
			if (operand != null) {
				parameter.removeConnections();
				new Argument(parameter, operand, Argument.ARROW_FROM_VISUALISER);
			}
    	}
	}
    
    public boolean receiveDrop(Visualiser draggedVisualiser) {
    	if (!enabled) {
    		return false;
    	}
    	LinkedList<Visualiser> justOne = new LinkedList<Visualiser>();
    	justOne.add(draggedVisualiser);
    	moveVisualisersToModel(justOne, true);
    	return true;
    }
        
    public boolean isDropCandidateFor(Visualiser draggedVisualiser) {
    	//Only allow enabled views to accept drops
    	if (!enabled) {
    		return false;
    	}
    	//Don't allow the minimized view to be dragged onto itself
    	if (draggedVisualiser instanceof MinimizedView) {
    		return false;
    	}
    	//Don't allow nested views
    	if (draggedVisualiser instanceof VisualiserOfView) {
    		return false;
    	}
    	return true;
    }
    
	public void populateCustom() {
		//Set up the model
		if (controlPanel == null) {
			controlPanel = new Model(getVisualiserName());
			controlPanel.setViewOwner(this);
		}
		//Add a toolbar
		toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.setMaximumSize(new Dimension(toolBar.getWidth(), 20));
		//Save button
		JButton save = new JButton();
        try {
        	ClassLoader cl = this.getClass().getClassLoader();
        	StoreIcon = new javax.swing.ImageIcon(cl.getResource(StoreIconFile));
        	save.setIcon(StoreIcon);
        } catch (Exception e) {
        	save.setText("Sto");
        }
		save.setPreferredSize(new Dimension(20, 20));
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				store();
			}
		});
		toolBar.add(save);
		//Delete button
		JButton delete = new JButton();
        try {
        	ClassLoader cl = this.getClass().getClassLoader();
        	DeleteIcon = new javax.swing.ImageIcon(cl.getResource(DeleteIconFile));
        	delete.setIcon(DeleteIcon);
        } catch (Exception e) {
        	delete.setText("Del");
        }
		delete.setPreferredSize(new Dimension (20, 20));
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				delete();
			}
		});
		toolBar.add(delete);
		//Minimize button
		JButton minimize = new JButton();
        try {
        	ClassLoader cl = this.getClass().getClassLoader();
        	MinimizeIcon = new javax.swing.ImageIcon(cl.getResource(MinimizeIconFile));
        	minimize.setIcon(MinimizeIcon);
        } catch (Exception e) {
        	minimize.setText("_");
        }
		minimize.setPreferredSize(new Dimension(20, 20));
		minimize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				minimize();
			}
		});
		toolBar.add(minimize);
		//Maximize button
		JButton maximize = new JButton();
        try {
        	ClassLoader cl = this.getClass().getClassLoader();
        	MaximizeIcon = new javax.swing.ImageIcon(cl.getResource(MaximizeIconFile));
        	maximize.setIcon(MaximizeIcon);
        } catch (Exception e) {
        	maximize.setText("|_");
        }
		maximize.setPreferredSize(new Dimension(20, 20));
		maximize.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				maximize();
			}
		});
		toolBar.add(maximize);
		add(toolBar, BorderLayout.EAST);
		//Set up the control panel
		setBorder(BorderFactory.createLineBorder(Color.red));
		add(controlPanel, BorderLayout.SOUTH);
		setEnabled(enabled);
		if (size != null) {
			setSize(size);
		}
	}
	
	public boolean getMaximized() {
		return maximized;
	}
	
	private void minimize() {
		//Switch visibility
		if (minimizedView != null) {
			minimizedView.setVisible(true);
		}
		this.setVisible(false);
		this.enabled = false;
		//Save to database
		updatePositionInDatabase();
		maximized = false;
	}
	
	private void maximize() {
		//Restore
		if (maximized) {
			restore();
		}
		//Maximize
		else {
			maximized = true;
			setLocation(0, 0);
			Dimension maximumSize = getRev().getModel().getSize();
			maximumSize.width -= scrollbarWidth;
			setSize(maximumSize);
			getViewModel().setModelDimensions(maximumSize.width, maximumSize.height - toolbarOffset);
			getRev().getModel().getModelPane().setLayer(this, 2);
			getRev().validate();
		}
	}
	
	public void restore() {
		getViewModel().setModelDimensions(this.size.width, this.size.height - toolbarOffset);
		setSize(this.size);
		maximized = false;
		setLocation(this.location);
		this.setVisible(true);
		this.enabled = true;
		getRev().getModel().getModelPane().setLayer(this, 0);
		updatePositionInDatabase();
		getRev().validate();
	}
	
	private void store() {
		//Flag the visualiser as stored
		stored = true;
		updatePositionInDatabase();
		//Remove the visualiser and its contents without destroying it
		for (Visualiser vis: controlPanel.getVisualisers()) {
			getRev().getModel().removeVisualiser(vis, true);
		}
		getRev().removeVisualiser(this, true);
	}
	
	private void delete() {
		//Remove the visualisers from the model
		LinkedList<Visualiser> visualisers = new LinkedList<Visualiser>();
		for (Visualiser vis: controlPanel.getVisualisers()) {
			visualisers.add(vis);
		}
		moveVisualisersToDefault(visualisers);
		//Remove the view
		deleting = true;
		super.removing();
		DatabaseAbstractionLayer.removeView(getRev().getConnection(), getVisualiserName());
		getRev().removeVisualiser(this, true);
	}
	
	public void setCacheLocation(Point point) {
		this.location = point;
	}

 	public void visualiserMoved() {
		super.visualiserMoved();
		if (!maximized) {
			this.location = getLocation();
			updatePositionInDatabase();
			if (minimizedView != null) {
				minimizedView.setLocation(this.getLocation());
			}
		}
	}
	
	public void removing() {
		//When a view is removed, it is added to the list and can be recalled
		//to actually remove it permanently, a new function delete() must be called instead.
		store();
	}
}
