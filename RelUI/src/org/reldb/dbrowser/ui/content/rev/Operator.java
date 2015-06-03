package org.reldb.dbrowser.ui.content.rev;

import java.util.Vector;

import org.eclipse.swt.graphics.Point;
import org.reldb.rel.client.Attribute;
import org.reldb.rel.client.Heading;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;
import org.reldb.rel.client.Value;

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
    	if (operand instanceof Operator)
    		return "(" + operand.getQuery() + ")";
    	return operand.getQuery();
    }
	
	public Heading getHeadingOfParameter(int parameterNumber) {
		String query = getQueryForParameter(parameterNumber);
		if (query == null)
			return null;
		Value returned = getDatabase().evaluate(query);
		if (returned instanceof Tuples) {
			Tuples tuples = (Tuples)returned;
			return tuples.getHeading();
		}
		return null;
	}
	
	public Vector<String> getAttributeNamesOfParameter(int parameterNumber) {
		String query = getQueryForParameter(parameterNumber);
		if (query == null)
			return null;
		Value returned = getDatabase().evaluate(query);
		Vector<String> output = new Vector<String>();
		if (returned instanceof Tuples) {
			Heading heading = ((Tuples)returned).getHeading();
			for (Attribute attribute: heading.toArray())
				output.add(attribute.getName());
		} else if (returned instanceof Tuple) {
			Tuple tuple = (Tuple)returned;
			for (int index=0; index<tuple.getAttributeCount(); index++)
				output.add(tuple.getAttributeName(index));
		}
		return output;
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
		getDatabase().removeQuery(getID());
    	super.delete();
    }

    public boolean isQueryable() {
    	for (Parameter parameter: parameters)
    		if (parameter.getArgument().isVisible() && parameter.getArgument().getOperand().getQuery() == null)
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
    
	protected Parameter addParameter(String name) {
		Parameter p = new Parameter(this, name, parameters.size(), lastSide);
		lastSide = (lastSide == Parameter.EASTTOWEST) ? Parameter.WESTTOEAST : Parameter.EASTTOWEST;
		parameters.add(p);
		new Argument(p);
		return p;
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
		getDatabase().updateQueryPosition(getID(), location.x, location.y, kind, getConnections(), getModel().getModelName());
	}

}
