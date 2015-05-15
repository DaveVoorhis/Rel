package org.reldb.dbrowser.ui.content.rev.core2;

import org.eclipse.swt.graphics.Point;

public abstract class Operator extends Visualiser {
    
    private int lastSide = Parameter.EASTTOWEST;

	private String kind;
    
    private java.util.Vector<Parameter> parameters = new java.util.Vector<Parameter>();
	
	protected Operator(Model model, String id, String kind, int xpos, int ypos) {
		super(model, id, kind, xpos, ypos);
		this.kind = kind;
		btnEdit.dispose();
		pack();
	}
	
	public String getKind() {
		return kind;
	}
	
	public Parameter getParameter(int parameterNumber) {
		return parameters.get(parameterNumber);
	}
    
    public String getQueryForParameter(int parameterNumber) {
    	Parameter parameter = parameters.get(parameterNumber);
    	Argument argument = parameter.getArgument();
    	if (argument == null)
    		return null;
    	Visualiser operand = argument.getOperand();
    	if (operand == null)
    		return null;
    	return operand.getQuery();
    }
    
    public String toString() {
    	return "Operator " + getTitle() + " (" + getID() + ")";
    }

    protected void disconnect() {
    	super.disconnect();
    	for (Parameter parameter: parameters)
    		parameter.getArgument().setOperand(null);
    }
    
    protected void delete() {
    	super.delete();
    	for (Parameter parameter: parameters)
    		parameter.dispose();
    	parameters.clear();
		DatabaseAbstractionLayer.removeOperator(getModel().getConnection(), getID());
    	dispose();
    }
    
	protected void addParameter(String name, String description) {
		Parameter p;
		if (lastSide == Parameter.EASTTOWEST) {
			p = new Parameter(this, rightSide, name, description, Parameter.EASTTOWEST);
			lastSide = Parameter.WESTTOEAST;
		} else {
			p = new Parameter(this, leftSide, name, description, Parameter.WESTTOEAST);
			lastSide = Parameter.EASTTOWEST;
		}
		parameters.add(p);
		new Argument(p);
	}

    /** Return number of parameters. */
    public int getParameterCount() {
        return parameters.size();
    }

	/** Get connections to this Op as a relation in Tutorial D syntax. */
	private String getConnections() {
		String out = "RELATION {parameter INT, Name CHAR} {";
		for (int i=0; i<getParameterCount(); i++) {
			Parameter parameter = getParameter(i);
			if (parameter.getArgument() != null) {
				if (i > 0)
					out += ", ";
				out += " tuple {";
				out += "parameter " + i + ", ";
				out += "Name '" + parameter.getArgument().getOperand().getID() + "'";
				out += "}";
			}
		}
		out += "   } ";							
		return out;											
	}
	
	protected void movement() {
		super.movement();
		if (parameters != null)
			for (Parameter parameter: parameters)
				parameter.redraw();		
	}
	
	protected void visualiserMoved() {
		Point location = getLocation();
		DatabaseAbstractionLayer.updateQueryPosition(getModel().getConnection(), getID(), location.x, location.y, kind, getConnections(), getModel().getModelName());
	}
	
}
