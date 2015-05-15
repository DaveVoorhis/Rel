package org.reldb.dbrowser.ui.content.rev.core2;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class Parameter extends Label {

	public static final int EASTTOWEST = 0;
	public static final int WESTTOEAST = 1;

    private int extensionLength = 10;
    
    private Argument argument;
	private int layoutDirection;
	private Operator operator;
		
	public Parameter(Operator operator, Composite parent, String name, String description, int layoutDirection) {
		super(parent, SWT.NONE);
		this.operator = operator;
		this.layoutDirection = layoutDirection;
		pack();
	}

	public void dispose() {
		super.dispose();
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
		int x = getParent().getParent().getParent().getLocation().x;
		if (layoutDirection == EASTTOWEST)
			return x;
		else
			return x + getParent().getParent().getParent().getBounds().width;
	}

	public int getParameterY() {
		return getParent().getParent().getParent().getLocation().y + getParent().getParent().getParent().getBounds().height / 2;
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
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
