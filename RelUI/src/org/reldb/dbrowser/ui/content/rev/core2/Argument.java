package org.reldb.dbrowser.ui.content.rev.core2;

import org.reldb.dbrowser.ui.content.rev.core2.Parameter;

import org.reldb.dbrowser.ui.content.rev.core2.graphics.glyphs.Arrow;
import org.reldb.dbrowser.ui.content.rev.core2.graphics.lines.LineHorizontal;
import org.reldb.dbrowser.ui.content.rev.core2.graphics.lines.LineVertical;

public class Argument {

	private final static int lineWidth = 1;
	private final static int arrowSize = 5;
	
    private LineHorizontal parameterExtension;
    private LineVertical visualiserExtension;
    private LineVertical verticalLink;
    private LineHorizontal visualiserLink;
    private Arrow visualiserArrow;
    private Arrow parameterArrow;
    
	private boolean arrowIntoVisualiser;
	private Parameter parameter;
	private Visualiser operand;

	public Argument(Parameter parameter, Visualiser operand) {
		this.parameter = parameter;
		this.operand = operand;
		arrowIntoVisualiser = false;
        initComponents();
        if (isDangling())
            System.out.println("Argument: Attempt to create a dangling reference!");
        else {
            parameter.setArgument(this);
            operand.addArgumentReference(this);
            redraw();
        }
	}
	
	public void setOperand(Visualiser visualiser) {
		if (operand instanceof Connector)
			operand.dispose();
		operand = visualiser;
		operand.addArgumentReference(this);
		redraw();
		parameter.getOperator().visualiserMoved();
		operand.visualiserMoved();
	}
	
	public Visualiser getOperand() {
		return operand;
	}
	
	public Operator getOperator() {
		return parameter.getOperator();
	}
	
	void redraw() {
        if (isDangling())
            return;

        // Set up connection visualisation.

        int vx2 = operand.getArgumentX(this);
        
        int cextlen = parameter.getExtensionLength();

        int pcx = parameter.getParameterX();
        int pcy = parameter.getParameterY();
        
        int vx1 = pcx + ((parameter.getLayoutDirection() == Parameter.EASTTOWEST) ? -cextlen : cextlen);

        int vextlen = operand.getExtensionLength(this);
        
        int vayt = operand.getArgumentY(this);
        int vy2 = vayt + ((operand.isConnectionAtBottom(this)) ? vextlen : -vextlen);
       
        int x1 = pcx;
        int x2 = vx1 + ((parameter.getLayoutDirection() != Parameter.EASTTOWEST) ? lineWidth : 0);
        int y = pcy;
        parameterExtension.setLine(x1, x2, y, lineWidth);
        
        x1 = vx2;
        x2 = operand.getArgumentY(this);
        y = vy2;
        visualiserExtension.setLine(x1, x2, y, lineWidth);
        
        x1 = vx1;
        x2 = parameter.getParameterY();
        y = vy2;
        verticalLink.setLine(x1, x2, y, lineWidth);

        x1 = vx2 + ((vx2 > vx1 && operand.isConnectionAtBottom(this)) ? lineWidth : 0);
        x2 = vx1 + ((vy2 > pcy && vx2 < vx1) ? lineWidth : 0);
        y = vy2;
        visualiserLink.setLine(x1, x2, y, lineWidth);
        
        setVisualiserArrow(arrowIntoVisualiser, arrowSize);
        setConnectorArrow(!arrowIntoVisualiser, arrowSize);
	}
    
    /** True if the connection is dangling, and therefore invalid. */
    public boolean isDangling() {
        return (parameter == null || operand == null);
    }

    // Set connector arrow to given arrow type.
    private void setConnectorArrow(boolean in, int arrowSize) {
        if (parameter.getLayoutDirection() == Parameter.EASTTOWEST) {
            parameterArrow.setArrow((in) ? Arrow.DIRECTION_RIGHT : Arrow.DIRECTION_LEFT, arrowSize);
            parameterArrow.setLocation(parameter.getParameterX() - arrowSize - 2, lineWidth - 1 + parameter.getParameterY() - arrowSize / 2);
        } else {
            parameterArrow.setArrow((in) ? Arrow.DIRECTION_LEFT : Arrow.DIRECTION_RIGHT, arrowSize);
            parameterArrow.setLocation(parameter.getParameterX() + 2, lineWidth - 1 + parameter.getParameterY() - arrowSize / 2);
        }
    }

    // Set visualiser arrow to given arrow type.
    private void setVisualiserArrow(boolean in, int arrowSize) {
    	if (operand.isConnectionAtBottom(this)) {
    		visualiserArrow.setArrow((!in) ? Arrow.DIRECTION_DOWN : Arrow.DIRECTION_UP, arrowSize);
    		visualiserArrow.setLocation(lineWidth - 1 + operand.getArgumentX(this) - arrowSize / 2, operand.getArgumentY(this) + arrowSize - 4);
    	} else {
    		visualiserArrow.setArrow((in) ? Arrow.DIRECTION_DOWN : Arrow.DIRECTION_UP, arrowSize);
    		visualiserArrow.setLocation(lineWidth - 1 + operand.getArgumentX(this) - arrowSize / 2, operand.getArgumentY(this) - arrowSize - 2);
    	}
    }

    public String toString() {
    	return "[argument]";
    }
    
	private void initComponents() {
		Model model = operand.getModel();
        parameterExtension = new LineHorizontal(model);
        visualiserExtension = new LineVertical(model);
        verticalLink = new LineVertical(model);
        visualiserLink = new LineHorizontal(model);
        visualiserArrow = new Arrow(model);
        parameterArrow = new Arrow(model);
	}

}
