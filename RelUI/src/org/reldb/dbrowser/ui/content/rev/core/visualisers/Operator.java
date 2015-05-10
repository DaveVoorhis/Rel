package org.reldb.dbrowser.ui.content.rev.core.visualisers;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.reldb.dbrowser.ui.content.rev.core.DatabaseAbstractionLayer;
import org.reldb.dbrowser.ui.content.rev.core.Rev;
import org.reldb.dbrowser.ui.content.rev.core.graphics.Argument;
import org.reldb.dbrowser.ui.content.rev.core.graphics.Parameter;
import org.reldb.dbrowser.ui.content.rev.core.graphics.Visualiser;
import org.reldb.rel.client.Attribute;
import org.reldb.rel.client.Heading;
import org.reldb.rel.client.Tuples;

public abstract class Operator extends VisualiserOfRelation {
	
	private String kind;
		
	public Operator(Rev rev, String kind) {
		super(rev, kind);
		this.kind = kind;
	}
	
	public Operator(Rev rev, String kind, String name) {
		super(rev, kind, name);
		this.kind = kind;
	}
		
	public String getKind() {
		return kind;
	}
	
	public void setKind(String kind) {
		this.kind = kind;
	}
	
	public String getQuery() {
		return "";
	}

	private Argument createDefaultOperand(final Parameter parameter) {
		String operandName = getVisualiserName() + parameter.getConnectorName();
		Visualiser operand = new Operand(getRev(), operandName, Math.max(0, getBounds().x - 140), getBounds().y);
		getRev().getModel().refresh();
		return new Argument(parameter, operand, Argument.ARROW_FROM_VISUALISER);
	}

	/** Get connections to this Op as a relation in Tutorial D syntax. */
	private String getConnections() {
		String out = "RELATION {";
		for (int i=0; i<getParameterCount(); i++) {
			Parameter parameter = getParameter(i);
			if (parameter.getConnection(0) != null && parameter.getConnection(0).getVisualiser() != null) {
				if (i > 0)
					out += ", ";
				out += " tuple {";
				out += "parameter " + i + ", ";
				if (parameter.getConnection(0).getVisualiser() instanceof Operand)
					out += "Name ''";
				else
					out += "Name '" + parameter.getConnection(0).getVisualiser().getVisualiserName() + "'";
				out += "}";
			}
		}
		out += "   } ";							
		return out;											
	}
	
	protected Visualiser getConnected(Parameter operand) {
		if (operand == null)
			return null;
		if (operand.getConnection(0) == null)
			return null;
		Visualiser connected = operand.getConnection(0).getVisualiser();
		if (connected instanceof Operand) {
			return null;
		}
		return connected;
	}
	
	protected Attribute[] getAttributes(Parameter operand) {
		Visualiser connect = getConnected(operand);
		VisualiserOfRelation connected = (VisualiserOfRelation)connect;
		if (connected == null) {
			return null;
		}
		String query = connected.getQuery();
		if (query == null)
			return null;
		Tuples tuples = DatabaseAbstractionLayer.evaluate(getRev().getConnection(), query);
		Heading heading = tuples.getHeading();
		return heading.toArray();
	}

	protected void updatePositionInDatabase() {
		DatabaseAbstractionLayer.updateQueryPosition(getRev().getConnection(), getVisualiserName(), getBounds().x, getBounds().y, getKind(), getConnections(), getRev().getModel().getModelName());
	}
	
	/** Override to be notified that this Visualiser is being removed from the Model. */
	public void removing() {
		super.removing();
		DatabaseAbstractionLayer.removeOperator(getRev().getConnection(), getVisualiserName());
	}
	
	private void updatePositionInDatabaseEventHandler() {
		updatePositionInDatabase();
	}
	
	public void visualiserMoved() {
		super.visualiserMoved();
		updatePositionInDatabaseEventHandler();
	}
	
	public void updateVisualiser() {
		super.updateVisualiser();
		updatePositionInDatabaseEventHandler();
	}

	public Parameter addParameter(Parameter parameter) {
		addParameter(parameter, true);
		createDefaultOperand(parameter);
		return parameter;
	}
	
	public Parameter addParameter(final String name, String comment) {
		return addParameter(new Parameter(this, name, comment) {
			private void disconnect() {
				removeConnections();
				reconnect();				
			}
			private void reconnect() {
				createDefaultOperand(this);				
			}
			public void handleMouseClick(MouseEvent evt) {
				Menu popup = new Menu(getShell(), SWT.POP_UP);
				MenuItem menuItem = new MenuItem(popup, SWT.PUSH);
				menuItem.setText("Disconnect");
				menuItem.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						disconnect();
					}
				});
				if (getConnectionCount() == 0) {
					menuItem = new MenuItem(popup, SWT.PUSH);
					menuItem.setText("Connect");
					menuItem.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent e) {
							reconnect();
						}
					});
				}
				popup.setVisible(true);
		    }
		});
	}
	
}
