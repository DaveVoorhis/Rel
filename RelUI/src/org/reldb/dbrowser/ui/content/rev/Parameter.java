package org.reldb.dbrowser.ui.content.rev;

public class Parameter {

	public static final int EASTTOWEST = 0;
	public static final int WESTTOEAST = 1;

    private int extensionLength = 10;
    
    private Argument argument;
	private int number;
	private int layoutDirection;
	private Operator operator;
		
	public Parameter(Operator operator, String name, String description, int number, int layoutDirection) {
		this.operator = operator;
		this.number = number;
		this.layoutDirection = layoutDirection;
	}

	public void dispose() {
		argument.dispose();
	}
	
	public int getNumber() {
		return number;
	}
	
	public Argument getArgument() {
		return argument;
	}

	public void setArgument(Argument argument) {
		this.argument = argument;
	}

	public int getExtensionLength() {
		return extensionLength;
	}

	public int getParameterX() {
		int x = operator.getLocation().x;
		if (layoutDirection == EASTTOWEST)
			return x;
		else
			return x + operator.getBounds().width;
	}

	public int getParameterY() {
		return operator.getLocation().y + operator.getBounds().height / 2;
	}

	public int getLayoutDirection() {
		return layoutDirection;
	}

	public void redraw() {
		if (argument != null)
			argument.redraw();
	}

	public Operator getOperator() {
		return operator;
	}

	public String toString() {
		return "Parameter " + operator.toString() + "->" + argument.toString();
	}

}
