/*
 * Visualiser.java
 *
 * Created on June 9, 2002, 1:28 AM
 */

package org.reldb.dbrowser.ui.content.rev.core.graphics;

import swing2swt.layout.BorderLayout;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.reldb.dbrowser.ui.content.rev.core.Rev;

/**
 * A Visualiser represents a thing in a Model.
 *
 * @author  Dave Voorhis
 */
public class Visualiser extends Composite {
    
    private final static Color MessageColor = new Color(Display.getDefault(), 153, 175, 175);
    private final static Color BaseColor = new Color(Display.getDefault(), 153, 153, 255);
    private final static Color SelectedColor = new Color(Display.getDefault(), 100, 100, 255);
    private final static Color DropCandidateColor = new Color(Display.getDefault(), 100, 255, 100);
    private final static Color PulseColor = new Color(Display.getDefault(), 255, 255, 75);
    private final static Color BackgroundColor = new Color(Display.getDefault(), 198, 198, 198);

    private final static int PulseDuration = 250;

    private final static int DEFAULT_EXTENSION_STEP_LENGTH = 3;
    private final static int DEFAULT_EXTENSION_BASE_LENGTH = 15;

    // display widgets
    private Composite jPanelLeft;
    private Composite jPanelRight;
    private Composite jPanelMain;
    private Label jLabelTitle;
    
    // Parameters on this visualiser
    private java.util.Vector<Parameter> parameters = new java.util.Vector<Parameter>();
    
    // Arguments to this visualiser
    private java.util.Vector<Argument> arguments = new java.util.Vector<Argument>();
    
    // Unique visualiser ID stamp
    private static long IDNumberStamp = 1;
    
    // Unique visualiser ID
    private long IDNumber = 0;
    
    // Next connector ID
    private long nextParameterID = 0;
    
    // Title
    private String Title = "<Undefined>";
    
    // True if selected
    private boolean Selected;
    
    // True if drop candidate
    private boolean DropCandidate;
    
    // Movement timer
    private Timer movementTimer;
    
    // Rev
    private Rev rev;
    
    /** Ctor */
    protected Visualiser(Rev rev) {
    	super(rev.getModel(), SWT.NONE);
    	this.rev = rev;
        IDNumber = IDNumberStamp++;
        nextParameterID = 0;
        Selected = false;
        DropCandidate = false;
        buildWidgets();
        rev.getModel().addVisualiser(this);
        movementTimer = new Timer();
        addControlListener(new ControlAdapter() {
			@Override
			public void controlMoved(ControlEvent e) {
				dispatchMovement();
			}
        });
        dispatchMovement();
    }

    private void dispatchMovement() {
        movementTimer.schedule(new TimerTask() {
        	public void run() {
        		if (isDisposed())
        			return;
        		getDisplay().asyncExec(new Runnable() {
        			public void run() {
        				if (!isDisposed())
        					visualiserMoved();		        				
        			}
        		});
        	}
        }, 250);
		movement();    	
    }
    
    /** Override to be notified of every movement.  This can receive a cascade of movements. */
    public void movement() {}
    
    /** Override to be notified 250 milliseconds after movement has stopped. */
    public void visualiserMoved() {}
    
    /** Establish a connection. The 'throwMeAway' visualiser vanishes, and the connection remains to 'attachToMe'.  Return true if succeeded. */
    public static boolean attachAndDelete(Visualiser attachToMe, Visualiser throwMeAway) {
        if (attachToMe.isDropCandidateFor(throwMeAway)) {
            while (throwMeAway.getArgumentCount() > 0)
                throwMeAway.getArgument(0).setVisualiser(attachToMe);
            throwMeAway.getRev().getModel().removeVisualiser(throwMeAway);
            return true;
        }
        return false;    	
    }
    
    /** Return true if a given visualiser can be dropped on this one, with something
       good possibly taking place thereafter via a receiveDrop() operation. */
    public boolean isDropCandidateFor(Visualiser draggedVisualiser) {
        return false;
    }
    
    /** Drop a visualiser on this one.  The dragged visualiser vanishes, and the connection remains to this visualiser.  Return true if succeeded. */
    public boolean receiveDrop(Visualiser draggedVisualiser) {
    	return attachAndDelete(this, draggedVisualiser);
    }
    
    private Timer pulseTimer = null;
    
    /** Pulse the visualiser with a given color for a moment.  Used to indicate
     * activity, instance changes, thrown exceptions, etc. */
    public void pulse(Color theColor) {
        if (pulseTimer!=null)
            return;
        setVisualiserColor(theColor);
        pulseTimer = new Timer();
        pulseTimer.schedule(new TimerTask() {
        	public void run() {
                setVisualiserColor();
                pulseTimer = null;        		
        	}
        }, PulseDuration);
    }
    
    /** Pulse the visualiser, i.e., set background to pulse color for a moment. */
    public void pulse() {
        pulse(PulseColor);
    }

    // set visualiser to given color
    private void setVisualiserColor(Color theColor) {
        jLabelTitle.setBackground(theColor);
    }
    
    // set visualiser to state-appropriate color
    private void setVisualiserColor() {
        if (DropCandidate)
            setVisualiserColor(DropCandidateColor);
        else if (Selected)
            setVisualiserColor(SelectedColor);
        else if (isOwnedByParameter())
            setVisualiserColor(MessageColor);
        else
            setVisualiserColor(BaseColor);
    }
    
    // prevent-infinite-recursion flag
    private boolean inSetSelected = false;
    
    /** Set selected status. */
    public void setSelected(boolean flag) {
        if (inSetSelected)
            return;
        inSetSelected = true;
        Selected = flag;
        setVisualiserColor();
        for (Argument connection: arguments)
        	connection.setSelected(flag);
        for (Parameter connector: parameters)
            for (int j=0; j<connector.getConnectionCount(); j++)
                connector.getConnection(j).setSelected(flag);
        inSetSelected = false;
    }
    
    /** Get selected status. */
    public boolean isSelected() {
        return Selected;
    }

    /** Set drop candidate status */
    public void setDropCandidate(boolean flag) {
        DropCandidate = flag;
        setVisualiserColor();
    }
    
    /** Get drop candidate status */
    public boolean isDropCandidate() {
        return DropCandidate;
    }
    
    // get connector extension step length, i.e., the amount each new connector extends
    // the recommended extension length
    int getConnectorExtensionStepLength() {
        return DEFAULT_EXTENSION_STEP_LENGTH;
    }
    
    // get the connector extension base length
    int getConnectorExtensionBaseLength() {
        return DEFAULT_EXTENSION_BASE_LENGTH;
    }

    /** Return number of parameters. */
    public int getParameterCount() {
        return parameters.size();
    }

    /** Get the ith parameter. */
    public Parameter getParameter(int i) {
        return parameters.get(i);
    }
    
    /** Get the named parameter. Return null if not found. */
    public Parameter getParameter(String name) {
        for (Parameter parameter: parameters)
            if (parameter.getConnectorName().equals(name))
            	return parameter;
        return null;
    }
    
    /** Return number of exposed parameters. */
    public int getExposedParameterCount() {
        int count = 0;
        for (Parameter parameter: parameters)
            if (parameter.isExposed())
                count++;
        return count;
    }
    
    /** Get next assignable connector ID. */
    public long getNextParameterID() {
        return nextParameterID++;
    }
    
    // Let visualiser know it got a new argument
    void addArgument(Argument c) {
        arguments.add(c);
        updateVisualiser();
    }
    
    // Remove a argument
    void removeArgument(Argument c) {
        arguments.remove(c);
        updateVisualiser();
    }
    
    /** Return number of arguments. */
    public long getArgumentCount() {
        return arguments.size();
    }
    
    /** Get the i'th argument. */
    public Argument getArgument(int i) {
        return arguments.get(i);
    }
    
    /** Redraw all arguments to this Visualiser. */
    public void redrawArguments() {
        // redraw arguments
    	for (Argument argument: arguments)
    		argument.redraw();
        // redraw connector's connections
    	for (Parameter parameter: parameters)
    		parameter.redrawConnections();
    }
    
    /** Get visualiser ID, which uniquely identifies it in the Model. */
    public long getID() {
        return IDNumber;
    }
    
    /** Set visualiser ID. */
    public void setID(long ID) {
        IDNumber = ID;
        if (IDNumber>IDNumberStamp)
            IDNumberStamp = IDNumber + 1;
    }

    public Rev getRev() {
    	return rev;
    }
    
    /** True if this Visualiser is wholly owned by its parameter and should 
     * be deleted along with its Argument. */
    public boolean isOwnedByParameter() {
        return false;
    }
    
    // this is invoked by a parameter when it receives a new argument
    void notifyArgumentAdded(Parameter x, Argument c) {
        expose(x);
    }
    
    // this is invoked by a connector when it loses a connection
    void notifyArgumentRemoved(Parameter x, Argument c) {
        redrawArguments();
    }
    
    private String name = "";
    
    public String getVisualiserName() {
    	return name;
    }
    
    public void setVisualiserName(String name) {
    	this.name = name;
        setLabel();
    }
    
    /** Set label (blue bar at top of every Visualiser). */
    public void setLabel() {
        jLabelTitle.setText(getTitle());
    }
    
    /** Get long title, for use in control panel, etc. */
    public String getLongTitle() {
        String out = getVisualiserName() + ": ";
        return out;
    }
    
    /** Set title.  Set name to title if it hasn't been set yet. */
    public void setTitle(String title) {
        Title = title;
        if (getVisualiserName()==null || getVisualiserName().length()==0)
            setVisualiserName(Title + getID());       // auto-naming via title
        setLabel();
    }
    
    /** Get title. */
    public String getTitle() {
        return Title;
    }
    
    /** Stringify as long title. */
    public String toString() {
        return getLongTitle();
    }
    
    /** Get argument attachment point in Model coordinates. */
    public int getArgumentX(Argument c) {
    	Rectangle bounds = getBounds();
        int indexOfArgument = arguments.indexOf(c);
        if (indexOfArgument==-1)
            return bounds.x + bounds.width / 2;
        int marginWidth = bounds.width / 5;               // 5%
        int drawWidth = bounds.width - marginWidth;
        int drawStep = drawWidth / (int)getArgumentCount();
        if (drawStep<0)
            return bounds.x + bounds.width / 2;
        return bounds.x + (bounds.width - drawWidth) / 2 + drawStep / 2 + indexOfArgument * drawStep;
    }
    
    /** Return true if attachment point should be at bottom of Visualiser. */
    public boolean isConnectionAtBottom(Argument c) {
    	return (c.getConnector().getConnectionY() > getBounds().y);
    }
    
    /** Get argument attachment point in Model coordinates. */
    public int getArgumentY(Argument c) {
    	Rectangle bounds = getBounds();
    	if (isConnectionAtBottom(c))
    		return bounds.y + bounds.height;
    	else
    		return bounds.y;
    }
    
    /** Get appropriate parameter connection extension length given argument. */
    public int getExtensionLength(Argument c) {
        int indexOfConnection = arguments.indexOf(c);
        if (indexOfConnection==0)
            return DEFAULT_EXTENSION_BASE_LENGTH;
        return DEFAULT_EXTENSION_BASE_LENGTH + indexOfConnection * DEFAULT_EXTENSION_STEP_LENGTH;
    }
    
    private int mouseOffsetX;
    private int mouseOffsetY;
    private boolean dragging = false;
    private Visualiser dropCandidate = null;
    
    // build widgets
    protected void buildWidgets() {   
        setLayout(new BorderLayout(0, 0));
        setBackground(BackgroundColor);
        
        jPanelLeft = new Composite(this, SWT.BORDER);
        jPanelLeft.setLayout(new GridLayout());
        jPanelLeft.setLayoutData(BorderLayout.WEST);
        
        jPanelRight = new Composite(this, SWT.BORDER);
        jPanelRight.setLayout(new GridLayout());
        jPanelRight.setLayoutData(BorderLayout.EAST);

        // Visualiser customisations
        populateCustom();
        
        jLabelTitle = new Label(this, SWT.NONE);
        jLabelTitle.setBackground(BaseColor);
        jLabelTitle.setAlignment(SWT.CENTER);
        jLabelTitle.setLayoutData(BorderLayout.NORTH);
        
        jLabelTitle.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
				mouseOffsetX = e.x;
				mouseOffsetY = e.y;
				dragging = true;
				setCapture(true);
			}
			@Override
			public void mouseUp(MouseEvent e) {
				setCapture(false);
				dragging = false;
        		if (dropCandidate!=null) {
        			Point location = getLocation();
        			if (dropCandidate == rev.getModel().getPossibleDropTarget(e.x + location.x, e.y + location.y, Visualiser.this) && dropCandidate.receiveDrop(Visualiser.this))
        				dropCandidate.setDropCandidate(false);
        			dropCandidate = null;
            		redrawArguments();
        		}
			}
        });
        jLabelTitle.addMouseMoveListener(new MouseMoveListener() {
			@Override
			public void mouseMove(MouseEvent e) {
				if (dragging) {
					Point location = getLocation();
					int newX = location.x + e.x - mouseOffsetX;
					int newY = location.y + e.y - mouseOffsetY;
					rev.getModel().moveVisualiser(Visualiser.this, newX, newY);
					// drop?
	                Visualiser dropTarget = rev.getModel().getPossibleDropTarget(location.x + e.x, location.y + e.y, Visualiser.this);
	                if (dropCandidate!=null && dropCandidate!=dropTarget)
	                    dropCandidate.setDropCandidate(false);
	                if (dropTarget!=null) {
	                    dropCandidate = dropTarget;
	                    dropCandidate.setDropCandidate(true);
	                }				
				} else
					moveAbove(null);
			}
        });
    }
    
    /** Populate custom section. */
    protected void populateCustom() {
    	updateVisualiser();
    }
    
    /** Update Visualiser as a result of some change in state. */
    public void updateVisualiser() {
    }
    
    /** Get main panel. */
    public Composite getMainPanel() {
        return jPanelMain;
    }
   
    /** Expose a parameter. */
    public void expose(Parameter c) {
        if (c.isExposed())
            return;
        // Position of parameter
        if (c.getLayoutDirection() == Parameter.EASTTOWEST) {
            if (!c.setParent(jPanelLeft))
            	System.out.println("Visualiser: c.setParent() not supported.");
            c.setExtensionLength(jPanelLeft.getChildren().length * getConnectorExtensionStepLength() + getConnectorExtensionBaseLength());
        } else {
        	if (!c.setParent(jPanelRight));
        		System.out.println("Visualiser: c.setParent() not supported.");
            c.setExtensionLength(jPanelRight.getChildren().length * getConnectorExtensionStepLength() + getConnectorExtensionBaseLength());
        }
        c.setExposed(true);                
        // Redraw arguments to this visualiser.  
        redrawArguments();
    }

    // Temporarily unexposes a parameter (while leaving its connections in visual limbo)
    // so that it can shortly be re-exposed, possibly on a new side of the whazzit.
    private void unexposeTemporary(Parameter c) {
        c.setExposed(false);
    }
    
    /** Unexpose a parameter.  Will only work if the parameter has no arguments. */
    public void unexpose(Parameter c) {
        if (!c.isExposed())
            return;
        if (c.getConnectionCount()>0)
            return;
        unexposeTemporary(c);
        redrawArguments();
    }
    
    /** Make a connector switch sides.  Only works if connector is exposed. */
    public void switchSides(Parameter c) {
        if (!c.isExposed())
            return;
        unexposeTemporary(c);
        if (c.getLayoutDirection()==Parameter.EASTTOWEST)
            c.setLayoutDirection(Parameter.WESTTOEAST);
        else
            c.setLayoutDirection(Parameter.EASTTOWEST);
        expose(c);
    }
    
    /** Remove all arguments and any message visualisers associated with the arguments. */
    public void removeArguments() {
        // detach argument connections
        while (getArgumentCount()>0)
            getArgument(0).disconnect();
        // clear all argument connections
        arguments = new java.util.Vector<Argument>();
        // remove connector connections
        for (int i=0; i<getParameterCount(); i++)
           getParameter(i).removeConnections();
    }
    
    /** Called by a parameter when it receives a mouse click that it hasn't handled. */
    protected void parameterForwardsClick(Parameter c, java.awt.event.MouseEvent e) {
    }
    
    /** Add a parameter. */
    protected void addParameter(Parameter c, boolean expose) {
        parameters.add(c);
        if (expose)
            expose(c);
    }

	public boolean isRemovable() {
		return true;
	}
	
	/** Override to be notified that this Visualiser is being removed from the Model. */
	public void removing() {
	}
}
