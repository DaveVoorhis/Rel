/*
 * Visualiser.java
 *
 * Created on June 9, 2002, 1:28 AM
 */

package org.reldb.rel.rev.graphics;

import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

/**
 * A Visualiser represents a thing in a Model.
 *
 * @author  Dave Voorhis
 */
public class Visualiser extends javax.swing.JPanel {
	
    public final static java.awt.Font LabelFont = new java.awt.Font("sans-serif", java.awt.Font.PLAIN, 10);

	private static final long serialVersionUID = 1L;
    
    private final static java.awt.Color MessageColor = new java.awt.Color(153, 175, 175);
    private final static java.awt.Color BaseColor = new java.awt.Color(153, 153, 255);
    private final static java.awt.Color SelectedColor = new java.awt.Color(100, 100, 255);
    private final static java.awt.Color DropCandidateColor = new java.awt.Color(100, 255, 100);
    private final static java.awt.Color PulseColor = new java.awt.Color(255, 255, 75);
    private final static java.awt.Color BackgroundColor = new java.awt.Color(198, 198, 198);
    private final static int PulseDuration = 250;

    private final static int DEFAULT_EXTENSION_STEP_LENGTH = 3;
    private final static int DEFAULT_EXTENSION_BASE_LENGTH = 15;

    // display widgets
    private javax.swing.JPanel jPanelLeft;
    private javax.swing.JPanel jPanelRight;
    private javax.swing.JPanel jPanelMain;
    private javax.swing.JLabel jLabelTitle;
    
    // Parameters on this visualiser
    private java.util.Vector<Parameter> parameters = new java.util.Vector<Parameter>();
    
    // Arguments to this visualiser
    private java.util.Vector<Argument> arguments = new java.util.Vector<Argument>();
    
    // Model in which this visualiser lives
    private Model theModel;
    
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
    
    /** Ctor */
    protected Visualiser(Model model) {
        IDNumber = IDNumberStamp++;
        nextParameterID = 0;
        theModel = model;
        Selected = false;
        DropCandidate = false;
        buildWidgets();
        model.addVisualiser(this);
        movementTimer = new Timer(250, new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			moved();
    		}
        });
        movementTimer.setRepeats(false);
        addComponentListener(new ComponentAdapter() {
			public void componentMoved(ComponentEvent e) {
				movementTimer.restart();
				movement();
			}        	
        });
    }

    /** Override to be notified of every movement.  This can receive a cascade of movements. */
    public void movement() {}
    
    /** Override to be notified 250 milliseconds after movement has stopped. */
    public void moved() {}
    
    /** Establish a connection. The 'throwMeAway' visualiser vanishes, and the connection remains to 'attachToMe'.  Return true if succeeded. */
    public static boolean attachAndDelete(Visualiser attachToMe, Visualiser throwMeAway) {
        if (attachToMe.isDropCandidateFor(throwMeAway)) {
            while (throwMeAway.getArgumentCount() > 0)
                throwMeAway.getArgument(0).setVisualiser(attachToMe);
            throwMeAway.getModel().removeVisualiser(throwMeAway);
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
    
    private javax.swing.Timer pulseTimer = null;
    
    /** Pulse the visualiser with a given color for a moment.  Used to indicate
     * activity, instance changes, thrown exceptions, etc. */
    public void pulse(java.awt.Color theColor) {
        if (pulseTimer!=null)
            return;
        setVisualiserColor(theColor);
        pulseTimer = new javax.swing.Timer(PulseDuration, new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setVisualiserColor();
                pulseTimer = null;
            }
        });
        pulseTimer.setRepeats(false);
        pulseTimer.start();
    }
    
    /** Pulse the visualiser, i.e., set background to pulse color for a moment. */
    public void pulse() {
        pulse(PulseColor);
    }

    // set visualiser to given color
    private void setVisualiserColor(java.awt.Color theColor) {
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
            if (parameter.getName().equals(name))
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

    /** Set the Model to which this visualiser belongs. */
    public void setModel(Model w) {
        theModel = w;
    }
    
    /** Get the Model that may be accessed by this Visualiser.  Null if none. */
    public Model getModel() {
        return theModel;
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
    
    /** Set label (blue bar at top of every Visualiser). */
    public void setLabel() {
        jLabelTitle.setText(getName());
    }
    
    /** Get long title, for use in control panel, etc. */
    public String getLongTitle() {
        String out = getName() + ": ";
        return out;
    }
    
    /** Set name and set label to name. */
    public void setName(String name) {
        super.setName(name);
        setLabel();
    }
    
    /** Set title.  Set name to title if it hasn't been set yet. */
    public void setTitle(String title) {
        Title = title;
        if (getName()==null || getName().length()==0)
            setName(Title + getID());       // auto-naming via title
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
        int indexOfArgument = arguments.indexOf(c);
        if (indexOfArgument==-1)
            return getX() + getWidth() / 2;
        int marginWidth = getWidth() / 5;               // 5%
        int drawWidth = getWidth() - marginWidth;
        int drawStep = drawWidth / (int)getArgumentCount();
        if (drawStep<0)
            return getX() + getWidth() / 2;
        return getX() + (getWidth() - drawWidth) / 2 + drawStep / 2 + indexOfArgument * drawStep;
    }
    
    /** Return true if attachment point should be at bottom of Visualiser. */
    public boolean isConnectionAtBottom(Argument c) {
    	return (c.getConnector().getConnectionY() > getY());
    }
    
    /** Get argument attachment point in Model coordinates. */
    public int getArgumentY(Argument c) {
    	if (isConnectionAtBottom(c))
    		return getY() + getHeight();
    	else
    		return getY();
    }
    
    /** Get appropriate parameter connection extension length given argument. */
    public int getExtensionLength(Argument c) {
        int indexOfConnection = arguments.indexOf(c);
        if (indexOfConnection==0)
            return DEFAULT_EXTENSION_BASE_LENGTH;
        return DEFAULT_EXTENSION_BASE_LENGTH + indexOfConnection * DEFAULT_EXTENSION_STEP_LENGTH;
    }
    
    // build widgets
    protected void buildWidgets() {    
        setLayout(new java.awt.BorderLayout());
        setBackground(BackgroundColor);
        
        jPanelLeft = new javax.swing.JPanel();
        jPanelLeft.setLayout(new java.awt.GridBagLayout());
        jPanelLeft.setOpaque(false);
        add(jPanelLeft, java.awt.BorderLayout.WEST);
        
        jPanelRight = new javax.swing.JPanel();
        jPanelRight.setLayout(new java.awt.GridBagLayout());
        jPanelRight.setOpaque(false);
        add(jPanelRight, java.awt.BorderLayout.EAST);
        
        // Visualiser customisations
        populateCustom();
        
        jLabelTitle = new javax.swing.JLabel();
        jLabelTitle.setOpaque(true);
        jLabelTitle.setBackground(BaseColor);
        jLabelTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabelTitle.setFont(LabelFont);
        add(jLabelTitle, java.awt.BorderLayout.NORTH);
    }
    
    /** Populate custom section. */
    protected void populateCustom() {
    	updateVisualiser();
    }
    
    /** Update Visualiser as a result of some change in state. */
    public void updateVisualiser() {
    }
    
    /** Get main panel. */
    public javax.swing.JPanel getMainPanel() {
        return jPanelMain;
    }
   
    /** Expose a parameter. */
    public void expose(Parameter c) {
        if (c.isExposed())
            return;
        
        c.setAlignmentY(0.0F);
        c.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        c.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        java.awt.GridBagConstraints gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridx = 0;
        
        // Position of parameter
        if (c.getLayoutDirection()==Parameter.EASTTOWEST) {
            gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
            jPanelLeft.add(c, gridBagConstraints);
            c.setExtensionLength(jPanelLeft.getComponentCount() * getConnectorExtensionStepLength() + getConnectorExtensionBaseLength());
        } else {
            gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
            jPanelRight.add(c, gridBagConstraints);
            c.setExtensionLength(jPanelRight.getComponentCount() * getConnectorExtensionStepLength() + getConnectorExtensionBaseLength());
        }
        c.setExposed(true);
                
        // Redraw arguments to this visualiser.  
        redrawArguments();
        if (theModel != null)
            theModel.refresh();
    }

    // Temporarily unexposes a parameter (while leaving its connections in visual limbo)
    // so that it can shortly be re-exposed, possibly on a new side of the whazzit.
    private void unexposeTemporary(Parameter c) {
        c.setExposed(false);
        c.getParent().remove(c);
    }
    
    /** Unexpose a parameter.  Will only work if the parameter has no arguments. */
    public void unexpose(Parameter c) {
        if (!c.isExposed())
            return;
        if (c.getConnectionCount()>0)
            return;
        unexposeTemporary(c);
        redrawArguments();
        theModel.refresh();
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
