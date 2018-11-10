package org.reldb.dbrowser.ui.content.rev;

import org.reldb.dbrowser.ui.content.rev.Parameter;
import org.reldb.dbrowser.ui.content.rev.graphics.Arrow;
import org.reldb.dbrowser.ui.content.rev.graphics.LineHorizontal;
import org.reldb.dbrowser.ui.content.rev.graphics.LineVertical;

public class Argument {

	private final static int lineWidth = 1;
	private final static int arrowSize = 5;
	
	private final static boolean arrowIntoVisualiser = false;
	
    private LineHorizontal parameterExtension;
    private LineVertical visualiserExtension;
    private LineVertical verticalLink;
    private LineHorizontal visualiserLink;
    private Arrow visualiserArrow;
    private Arrow parameterArrow;
    
	private Parameter parameter;
	private Visualiser operand;

	public Argument(Parameter parameter) {
		this.parameter = parameter;
        initComponents();
        parameter.setArgument(this);
        setOperand(null);
	}
	
	public void setOperand(Visualiser visualiser) {
		if (operand != null)
			operand.removeArgumentReference(this);
		if (operand instanceof Connector)
			operand.dispose();
		operand = visualiser;
		if (operand == null)
			operand = new Connector(parameter);
		operand.addArgumentReference(this);
		redraw();
		Operator operator = parameter.getOperator();
		operator.visualiserMoved();
		operand.visualiserMoved();
		operator.verify();
		operand.verify();
	}
	
	public Visualiser getOperand() {
		return operand;
	}
	
	public Operator getOperator() {
		return parameter.getOperator();
	}
	
	void redraw() {
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
		Model model = parameter.getOperator().getModel();
        parameterExtension = new LineHorizontal(model);
        visualiserExtension = new LineVertical(model);
        verticalLink = new LineVertical(model);
        visualiserLink = new LineHorizontal(model);
        visualiserArrow = new Arrow(model);
        parameterArrow = new Arrow(model);
	}

	public void bringToFront() {
		visualiserArrow.moveAbove(null);
		parameterArrow.moveAbove(null);
		parameterExtension.moveAbove(null);
		visualiserExtension.moveAbove(null);
		verticalLink.moveAbove(null);
		visualiserLink.moveAbove(null);
	}

	public void dispose() {
		if (operand instanceof Connector)
			operand.dispose();
		visualiserArrow.dispose();
		parameterArrow.dispose();
		parameterExtension.dispose();
		visualiserExtension.dispose();
		verticalLink.dispose();
		visualiserLink.dispose();
	}

	public void setVisible(boolean b) {
		if (operand instanceof Connector)
			operand.setVisible(b);
		visualiserArrow.setVisible(b);
		parameterArrow.setVisible(b);
		parameterExtension.setVisible(b);
		visualiserExtension.setVisible(b);
		verticalLink.setVisible(b);
		visualiserLink.setVisible(b);
		parameter.getOperator().verify();
	}

	public boolean isVisible() {
		return visualiserArrow.getVisible();
	}

}
