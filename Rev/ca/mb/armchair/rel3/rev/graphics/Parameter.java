/*
 * Connector.java
 *
 * Created on June 11, 2002, 2:55 AM
 */

package ca.mb.armchair.rel3.rev.graphics;

/**
 * A Parameter is a widget bound to a Visualiser that represents
 * some attribute of the Visualiser.
 * 
 * Normally, you will not use this class directly, except perhaps when
 * creating custom Visualisers.
 *
 * @author  Dave Voorhis
 */
public class Parameter extends javax.swing.JLabel {
    
	private static final long serialVersionUID = 1L;
	
	// Fonts
//    private final static java.awt.Font LabelFontBold = new java.awt.Font("sans-serif", java.awt.Font.BOLD, 9);
    private final static java.awt.Font LabelFontPlain = new java.awt.Font("sans-serif", java.awt.Font.PLAIN, 9);
    
    // Sizes
    private final static int distance = 20;    // starting distance for connection extension
    
    // Visualiser to which this connector is bound
    private Visualiser visualiser;
    
    // Connector properties
    private String name = "";
    
    // Connector attributes
    private long id = -1;
    private boolean exposed = false;        // Merely a flag used by Visualiser
    
    // Layout control
    public static final int EASTTOWEST = 0;
    public static final int WESTTOEAST = 1;
    private int layoutDirection = -1;
    private static int nextLayoutDirection = EASTTOWEST;
    private int extensionLength = 10;
    
    // Connections
    private java.util.Vector<Argument> connectionList = new java.util.Vector<Argument>();

    /** Create an invisible, null connector */
    public Parameter(Visualiser v) {
        name = "";
        visualiser = v;
        id = visualiser.getNextParameterID();
        setLayoutDirection();
        setOpaque(true);
        addConnector(" ");
        setVisible(false);
    }
    
    /** Create a connector. */
    public Parameter(Visualiser visualiser, String name, String Tip) {
        this.visualiser = visualiser;
        this.name = name;
        id = visualiser.getNextParameterID();
        setLayoutDirection();
        setOpaque(true);
        setToolTipText(Tip);
        setBorder(new javax.swing.border.EtchedBorder());
        addConnector(Tip);
        addConnectorLabel();
        configureMouse();
    }
    
    /** Obtain suggested new message visualiser position, relative to Model,
        and offset (if necessary) by the new visualiser's width. */
    java.awt.Point getRecommendedNewVisualiserPoint(int NewVisualiserWidth) {
        return new java.awt.Point(getConnectionX() +
                ((getLayoutDirection()==EASTTOWEST) ? -(distance + NewVisualiserWidth) : distance),
                getConnectionY());
    }
    
    /** Get 'exposed' flag. */
    public boolean isExposed() {
        return exposed;
    }
    
    /** Set 'exposed' flag.  You should not manipulate this flag directly!!!  Use expose() and unexpose() to
     expose and unexpose this connector. */
    void setExposed(boolean flag) {
        exposed = flag;
    }
    
    /** 'ExtensionLength' value */
    public int getExtensionLength() {
        return extensionLength;
    }
    
    /** Set 'ExtensionLength' value */
    public void setExtensionLength(int v) {
        extensionLength = v;
    }
    
    /** Explicitly set layout direction */
    public void setLayoutDirection(int Direction) {
        layoutDirection = Direction;
    }
    
    /** layout direction */
    public int getLayoutDirection() {
        return layoutDirection;
    }
    
    /** Get this connector's visualiser */
    public Visualiser getVisualiser() {
        return visualiser;
    }
    
    /** Stringize */
    public String toString() {
    	return "Connector";
    }
    
    /** Return raw connector ID number */
    public long getID() {
        return id;
    }
    
    /** Unexpose this Connector */
    public void unexpose() {
        getVisualiser().unexpose(this);
    }
    
    /** Expose this Connector */
    public void expose() {
        getVisualiser().expose(this);
    }
    
    /** Move this Connector to the other side of the Visualiser */
    public void switchSides() {
        getVisualiser().switchSides(this);
    }
    
    /** Change the ranking of this Connector in the Visualiser's display */
    public void changeRank(int n) {
        java.awt.Container container = this.getParent();
        container.remove(this);
        container.add(this, n);
        getVisualiser().updateVisualiser();
        redrawConnections();
        getVisualiser().getModel().refresh();
    }
    
    /** Redraw all Connections that argument this Connector */
    public void redrawConnections() {
        for (int i=0; i<getConnectionCount(); i++)
            getConnection(i).redraw();
    }
    
    /** Remove all Connections from this Connector */
    public void removeConnections() {
        while (getConnectionCount()>0) {
            Visualiser Source = getConnection(0).getVisualiser();
            if (Source!=null && Source.isOwnedByParameter())
                Source.getModel().removeVisualiser(Source);
            else
                getConnection(0).disconnect();
        }
        connectionList = new java.util.Vector<Argument>();
    }
    
    /** Advise this connector that it's received a connection.  Do not invoke directly! */
    void addConnection(Argument c) {
        connectionList.add(c);
        getVisualiser().notifyArgumentAdded(this, c);
        getVisualiser().updateVisualiser();
    }
    
    /** Advise this connector that it's lost a connection.  Do not invoke directly! */
    void removeConnection(Argument c) {
        connectionList.remove(c);
        getVisualiser().notifyArgumentRemoved(this, c);
        getVisualiser().updateVisualiser();
    }
    
    /** How many connections are there to this connector? */
    public long getConnectionCount() {
        return connectionList.size();
    }
    
    /** Get the i'th connection */
    public Argument getConnection(int i) {
        try {
            return (Argument)connectionList.get(i);
        } catch (java.lang.ArrayIndexOutOfBoundsException j) {
            return null;
        }
    }
    
    /** Return the connector ID, which is used to identify connectors on
     * a Visualiser.
     *
     * Use getParameterNumber() in conjunction with getConnectorID() to
     * resolve to specific connectors when the ConnectorID is ambiguous. This
     * occurs (for example) on message visualisers with multiple 
     * parameters of the same type. 
     *
     */
    public String getConnectorID() {
        return toString();
    }
    
    /** Get the Connector's name */
    public String getName() {
        return name;
    }
    
    /** get attachment point in Model coordinates */
    public int getConnectionX() {
        if (getLayoutDirection()==EASTTOWEST)
            return getParent().getParent().getX() + getParent().getX() + getX();
        else
            return getParent().getParent().getX() + getParent().getX() + getX() + getWidth();
    }
    
    /** get attachment point in Model coordinates */
    public int getConnectionY() {
        return getParent().getParent().getY() + getParent().getY() + getY() + getHeight() / 2;
    }
    
    // Set up label
    private void addConnectorLabel() {
        setFont(LabelFontPlain);
        setText(getText() + getName());
    }
    
    /** implement a connector with associated tip */
    protected void addConnector(String Tip) {
        if (Tip.length()>0)
            setToolTipText(Tip);
    }
    
    /** implement a connector without a tip */
    protected void addConnector() {
        addConnector("");
    }

    /** Override to receive mouse click. */
    public void handleMouseClick(java.awt.event.MouseEvent evt) {}
    
    // Set up mouse handler
    private void configureMouse() {
        for (int i=0; i<this.getComponentCount(); i++)
            getComponent(i).addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                   	handleMouseClick(evt);
                }
            });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
               	handleMouseClick(evt);
            }
        });
    }
    
    // Obtain default layout direction
    private void setLayoutDirection() {
        layoutDirection = nextLayoutDirection;
        if (nextLayoutDirection==EASTTOWEST)
            nextLayoutDirection=WESTTOEAST;
        else
            nextLayoutDirection=EASTTOWEST;
    }
}
