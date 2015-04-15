/*
 * Connection.java
 *
 * Created on July 17, 2002, 4:00 PM
 */

package org.reldb.dbrowser.ui.content.rev.core.graphics;

import org.reldb.dbrowser.ui.content.rev.core.utilities.visualisation.glyphs.Arrow;
import org.reldb.dbrowser.ui.content.rev.core.utilities.visualisation.lines.LineHorizontal;
import org.reldb.dbrowser.ui.content.rev.core.utilities.visualisation.lines.LineVertical;

/**
 * Establishes and displays an argument to a parameter.
 *
 * @author  Dave Voorhis
 */
public class Argument {

	public final static int ARROW_TO_VISUALISER = 0;
	public final static int ARROW_FROM_VISUALISER = 1;
	
    private final static java.awt.Color BaseColor = new java.awt.Color(25, 75, 75);
    private final static java.awt.Color SelectedColor = new java.awt.Color(150, 25, 25);
    private final static java.awt.Color PulseColor = new java.awt.Color(255, 255, 75);
    private final static int PulseDuration = 250;
    private final static int NormalArrowWidth = 5;
    private final static int NormalLineWidth = 1;
    private final static int SelectedArrowWidth = 8;
    private final static int SelectedLineWidth = 2;
    
    private Model model;
    private Parameter parameter;
    private Visualiser visualiser;

    private LineHorizontal parameterExtension = new LineHorizontal();
    private LineVertical visualiserExtension = new LineVertical();
    private LineVertical verticalLink = new LineVertical();
    private LineHorizontal visualiserLink = new LineHorizontal();
    private Arrow visualiserArrow = new Arrow();
    private Arrow parameterArrow = new Arrow();

    private int lineWidth = NormalLineWidth;

    private boolean pulsed = false;
    private boolean selected = false;
    private boolean drawing = false;
    private boolean arrowIntoVisualiser = false;
    
    /** Create a connection between a parameter and a Visualiser. */
    public Argument(Parameter parameter, Visualiser visualiser, int direction) {
        this.model = visualiser.getModel();
        this.parameter = parameter;
        this.visualiser = visualiser;
        arrowIntoVisualiser = (direction == ARROW_TO_VISUALISER);
        initComponents();
        if (isDangling())
            System.out.println("Argument: Attempt to create a dangling reference!");
        else {
            parameter.addConnection(this);
            visualiser.addArgument(this);
            redraw();
        }
    }
    
    // Get recommended color
    private java.awt.Color getRecommendedColor() {
        if (pulsed)
            return PulseColor;
        else if (selected)
            return SelectedColor;
        else if (parameter==null)
            return BaseColor;
        else {
            return BaseColor;
        }
    }
    
    // Initialise widgets
    private void initComponents() {
        model.getModelPane().add(visualiserExtension);
        model.getModelPane().add(visualiserLink);
        model.getModelPane().add(verticalLink);
        model.getModelPane().add(parameterExtension);
        model.getModelPane().add(visualiserArrow);
        model.getModelPane().add(parameterArrow);
    }
    
    /** Set connection as selected or unselected */
    public void setSelected(boolean flag) {
        selected = flag;
        if (selected)
            lineWidth = SelectedLineWidth;
        else
            lineWidth = NormalLineWidth;
        redraw();
    }

    private javax.swing.Timer pulseTimer = null;
    
    /** Pulse the connector with a given color for a moment.  Used to indicate
     * activity. */
    public void pulse() {
        if (pulseTimer!=null)
            return;
        pulsed = true;
        redrawColor();
        pulseTimer = new javax.swing.Timer(PulseDuration, new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pulsed = false;
                redrawColor();
                pulseTimer = null;
            }
        });
        pulseTimer.setRepeats(false);
        pulseTimer.start();
    }
    
    /** Set Parameter end of connection. */
    public void setParameter(Parameter c) {
        if (parameter!=null)
            parameter.removeConnection(this);         // remove connection from old parameter
        parameter = c;
        parameter.addConnection(this);               // attach connection to new connector
        // must disconnect and reconnect visualiser here, because this
        // is essentially a new connection
        if (visualiser!=null) {
            visualiser.removeArgument(this);
            visualiser.addArgument(this);
        }
        redraw();
    }
    
    /** Set Visualiser at visualiser end of connection. */
    public void setVisualiser(Visualiser v) {
        if (visualiser!=null)
            visualiser.removeArgument(this);
        visualiser = v;
        visualiser.addArgument(this);
        // must disconnect and reconnect connector here, because this
        // is essentially a new connection
        if (parameter!=null) {
            parameter.removeConnection(this);
            parameter.addConnection(this);
        }
        redraw();
    }
    
    /** Get Connector at connector end of Connection */
    public Parameter getConnector() {
        return parameter;
    }
    
    /** Get Visualiser at visualiser end of Connection. */
    public Visualiser getVisualiser() {
        return visualiser;
    }
    
    /** Get Model in which this Connection lives. */
    public Model getModel() {
        return model;
    }

    // Unvisualise it
    private void undraw() {
    	visualiserExtension.setVisible(false);
    	visualiserLink.setVisible(false);
    	verticalLink.setVisible(false);
    	parameterExtension.setVisible(false);
    	visualiserArrow.setVisible(false);
    	parameterArrow.setVisible(false);
        model.getModelPane().remove(visualiserExtension);
        model.getModelPane().remove(visualiserLink);
        model.getModelPane().remove(verticalLink);
        model.getModelPane().remove(parameterExtension);
        model.getModelPane().remove(visualiserArrow);
        model.getModelPane().remove(parameterArrow);
    }

    /** Recolor existing lines. */
    public void redrawColor() {
        if (isDangling())
            return;        
        java.awt.Color c = getRecommendedColor();
        visualiserExtension.setBackground(c);
        parameterExtension.setBackground(c);
        verticalLink.setBackground(c);
        visualiserLink.setBackground(c);
        visualiserArrow.setColor(c);
        parameterArrow.setColor(c);
    }
    
    /** True if the connection is dangling, and therefore invalid. */
    public boolean isDangling() {
        return (parameter==null || visualiser==null);
    }

    // Set connector arrow to given arrow type.
    private void setConnectorArrow(boolean In, int arrowSize) {
        if (parameter.getLayoutDirection()==Parameter.EASTTOWEST) {
            parameterArrow.setArrow((In) ? Arrow.DIRECTION_RIGHT : Arrow.DIRECTION_LEFT, arrowSize);
            parameterArrow.setLocation(parameter.getConnectionX() - arrowSize - 2, lineWidth - 1 + parameter.getConnectionY() - arrowSize / 2);
        } else {
            parameterArrow.setArrow((In) ? Arrow.DIRECTION_LEFT : Arrow.DIRECTION_RIGHT, arrowSize);
            parameterArrow.setLocation(parameter.getConnectionX() + 2, lineWidth - 1 + parameter.getConnectionY() - arrowSize / 2);
        }
    }

    // Set visualiser arrow to given arrow type.
    private void setVisualiserArrow(boolean In, int arrowSize) {
    	if (visualiser.isConnectionAtBottom(this)) {
    		visualiserArrow.setArrow((!In) ? Arrow.DIRECTION_DOWN : Arrow.DIRECTION_UP, arrowSize);
    		visualiserArrow.setLocation(lineWidth - 1 + visualiser.getArgumentX(this) - arrowSize / 2, visualiser.getArgumentY(this) + arrowSize - 4);
    	} else {
    		visualiserArrow.setArrow((In) ? Arrow.DIRECTION_DOWN : Arrow.DIRECTION_UP, arrowSize);
    		visualiserArrow.setLocation(lineWidth - 1 + visualiser.getArgumentX(this) - arrowSize / 2, visualiser.getArgumentY(this) - arrowSize - 2);
    	}
    }
    
    /** Draw lines */
    public void redraw() {
        if (isDangling())
            return;

        if (drawing)
            return;
        drawing = true;
        
        // Force connector to a side nearest a connected Visualisers
        long vxSum = 0;
        for (int i=0; i<parameter.getConnectionCount(); i++)
            vxSum += parameter.getConnection(i).getVisualiser().getArgumentX(this);
        long vxAverage = vxSum / parameter.getConnectionCount();
        Visualiser ConnectorVisualiser = parameter.getVisualiser();
        if (vxAverage > ConnectorVisualiser.getWidth() / 2 + ConnectorVisualiser.getX()) {
            if (parameter.getLayoutDirection()==Parameter.EASTTOWEST)
                parameter.switchSides();
        } else {
            if (parameter.getLayoutDirection()==Parameter.WESTTOEAST)
                parameter.switchSides();
        }
        
        // Set up connection visualisation.

        int vx2 = visualiser.getArgumentX(this);
        
        int cextlen = parameter.getExtensionLength();

        int vx1 = parameter.getConnectionX() +
            ((parameter.getLayoutDirection()==Parameter.EASTTOWEST) ? -cextlen : cextlen);

        int vextlen = visualiser.getExtensionLength(this);
        
        int vy2 = visualiser.getArgumentY(this) +
        	((visualiser.isConnectionAtBottom(this)) ? vextlen : -vextlen);
       
        parameterExtension.setLine(parameter.getConnectionX(), vx1 +
            ((parameter.getLayoutDirection()!=Parameter.EASTTOWEST) ? lineWidth : 0),
            parameter.getConnectionY(), lineWidth);
           
        visualiserExtension.setLine(vx2, visualiser.getArgumentY(this), vy2, lineWidth);
        
        verticalLink.setLine(vx1, parameter.getConnectionY(), vy2, lineWidth);
        
        visualiserLink.setLine(
        		vx2 + ((vx2 > vx1 && visualiser.isConnectionAtBottom(this)) ? lineWidth : 0), 
        		vx1 + ((vy2 > parameter.getConnectionY() && vx2 < vx1) ? lineWidth : 0), 
        		vy2,
        		lineWidth);

        // Create appropriate connection decorations.
        int arrowSize;
        if (lineWidth == NormalLineWidth)
            arrowSize = NormalArrowWidth;
        else
            arrowSize = SelectedArrowWidth;
        setVisualiserArrow(arrowIntoVisualiser, arrowSize);
        setConnectorArrow(!arrowIntoVisualiser, arrowSize);
       
        redrawColor();
        
        drawing = false;
    }

    /** Disconnect from the Connector and the Visualiser. */
    public void disconnect() {
        undraw();
        if (parameter!=null)
            parameter.removeConnection(this);
        parameter = null;
        if (visualiser!=null)
            visualiser.removeArgument(this);
        visualiser = null;
    }
}
