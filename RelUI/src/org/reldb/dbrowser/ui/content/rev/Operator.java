package org.reldb.dbrowser.ui.content.rev;

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
    	for (Parameter parameter: parameters)
    		parameter.getArgument().setOperand(null);
    	super.disconnect();
    }
    
    protected void delete() {
    	disconnect();
    	for (Parameter parameter: parameters)
    		parameter.dispose();
    	parameters.clear();
		DatabaseAbstractionLayer.removeOperator(getModel().getConnection(), getID());
    	super.delete();
    }

    public boolean isQueryable() {
    	for (Parameter parameter: parameters)
    		if (parameter.getArgument().getOperand().getQuery() == null)
    			return false;
    	return true;
    }
    
    public void verify() {
    	super.verify();
    	notifyArgumentChanged();
    }
    
    private String cachedQuery = null;
    
    private void notifyArgumentChanged() {
    	if (isQueryable()) {
        	setReadyColour();
        	btnInfo.setEnabled(true);
        	btnRun.setEnabled(true);
        	String query = getQuery();
        	if (cachedQuery == null || query.compareTo(cachedQuery) != 0) {
        		notifyArgumentChanged(true);
        		cachedQuery = query;
        	}
    	} else {
			setWarningColour();
			btnInfo.setEnabled(false);
			btnRun.setEnabled(false);
        	if (cachedQuery != null) {
        		notifyArgumentChanged(false);
        		cachedQuery = null;
        	}
    	}
    }

    /** Override to be notified that a parameter's argument has changed, with identification as to whether it's queryable or not. */
    protected void notifyArgumentChanged(boolean queryable) {}
    
	protected void addParameter(String name, String description) {
		Parameter p = new Parameter(this, name, description, parameters.size(), lastSide);
		lastSide = (lastSide == Parameter.EASTTOWEST) ? Parameter.WESTTOEAST : Parameter.EASTTOWEST;
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
				out += "parameter " + parameter.getNumber() + ", ";
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
