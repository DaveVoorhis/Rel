/*
 * Connection.java
 *
 * Created on July 17, 2002, 4:00 PM
 */

package org.reldb.dbrowser.ui.content.rev.core.graphics;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.reldb.dbrowser.ui.content.rev.core.graphics.glyphs.Arrow;
import org.reldb.dbrowser.ui.content.rev.core.graphics.lines.LineHorizontal;
import org.reldb.dbrowser.ui.content.rev.core.graphics.lines.LineVertical;

/**
 * Establishes and displays an argument to a parameter.
 *
 * @author  Dave Voorhis
 */
public class Argument {

	public final static int ARROW_TO_VISUALISER = 0;
	public final static int ARROW_FROM_VISUALISER = 1;
	
    private final static Color BaseColor = new Color(Display.getDefault(), 25, 75, 75);
    private final static Color SelectedColor = new Color(Display.getDefault(), 150, 25, 25);
    private final static Color PulseColor = new Color(Display.getDefault(), 255, 255, 75);
    
    private final static int PulseDuration = 250;
    private final static int NormalArrowWidth = 5;
    private final static int NormalLineWidth = 1;
    private final static int SelectedArrowWidth = 8;
    private final static int SelectedLineWidth = 2;
    
    private Model model;
    private Parameter parameter;
    private Visualiser visualiser;

    private LineHorizontal parameterExtension;
    private LineVertical visualiserExtension;
    private LineVertical verticalLink;
    private LineHorizontal visualiserLink;
    private Arrow visualiserArrow;
    private Arrow parameterArrow;

    private int lineWidth = NormalLineWidth;

    private boolean pulsed = false;
    private boolean selected = false;
    private boolean drawing = false;
    private boolean arrowIntoVisualiser = false;
    
    /** Create a connection between a parameter and a Visualiser. */
    public Argument(Parameter parameter, Visualiser visualiser, int direction) {
        this.model = visualiser.getRev().getModel();
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
    private Color getRecommendedColor() {
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
        parameterExtension = new LineHorizontal(model.getModelPane());
        visualiserExtension = new LineVertical(model.getModelPane());
        verticalLink = new LineVertical(model.getModelPane());
        visualiserLink = new LineHorizontal(model.getModelPane());
        visualiserArrow = new Arrow(model.getModelPane());
        parameterArrow = new Arrow(model.getModelPane());
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

    private Timer pulseTimer = null;
    
    /** Pulse the connector with a given color for a moment.  Used to indicate
     * activity. */
    public void pulse() {
        if (pulseTimer!=null)
            return;
        pulsed = true;
        redrawColor();
        pulseTimer = new Timer();
        pulseTimer.schedule(new TimerTask() {
        	public void run() {
                pulsed = false;
                redrawColor();
                pulseTimer = null;        		
        	}
        }, PulseDuration);
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
    	visualiserExtension.dispose();
    	visualiserLink.dispose();
    	verticalLink.dispose();
    	parameterExtension.dispose();
    	visualiserArrow.dispose();
    	parameterArrow.dispose();
    }

    /** Recolor existing lines. */
    public void redrawColor() {
        if (isDangling())
            return;
        if (visualiserExtension.isDisposed())
        	return;
        visualiserExtension.getDisplay().asyncExec(new Runnable() {
        	public void run() {
                Color c = getRecommendedColor();
                if (!visualiserExtension.isDisposed())
                	visualiserExtension.setBackground(c);
                if (!parameterExtension.isDisposed())
                	parameterExtension.setBackground(c);
                if (!verticalLink.isDisposed())
                	verticalLink.setBackground(c);
                if (!visualiserLink.isDisposed())
                	visualiserLink.setBackground(c);
                if (!visualiserArrow.isDisposed())
                	visualiserArrow.setColor(c);
                if (!parameterArrow.isDisposed())
                	parameterArrow.setColor(c);        		
        	}
        });
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

        // Set up connection visualisation.

        int vx2 = visualiser.getArgumentX(this);
        
        int cextlen = parameter.getExtensionLength();

        int pcx = parameter.getConnectionX();
        int pcy = parameter.getConnectionY();
        
        int vx1 = pcx + ((parameter.getLayoutDirection() == Parameter.EASTTOWEST) ? -cextlen : cextlen);

        int vextlen = visualiser.getExtensionLength(this);
        
        int vayt = visualiser.getArgumentY(this);
        int vy2 = vayt + ((visualiser.isConnectionAtBottom(this)) ? vextlen : -vextlen);
       
        int x1 = pcx;
        int x2 = vx1 + ((parameter.getLayoutDirection() != Parameter.EASTTOWEST) ? lineWidth : 0);
        int y = pcy;
        parameterExtension.setLine(x1, x2, y, lineWidth);
        
        x1 = vx2;
        x2 = visualiser.getArgumentY(this);
        y = vy2;
        visualiserExtension.setLine(x1, x2, y, lineWidth);
        
        x1 = vx1;
        x2 = parameter.getConnectionY();
        y = vy2;
        verticalLink.setLine(x1, x2, y, lineWidth);

        x1 = vx2 + ((vx2 > vx1 && visualiser.isConnectionAtBottom(this)) ? lineWidth : 0);
        x2 = vx1 + ((vy2 > pcy && vx2 < vx1) ? lineWidth : 0);
        y = vy2;
        visualiserLink.setLine(x1, x2, y, lineWidth);

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
