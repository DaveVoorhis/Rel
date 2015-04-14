package org.reldb.dbrowser.dbui.content.rev.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.reldb.dbrowser.dbui.content.rev.core.graphics.Argument;
import org.reldb.dbrowser.dbui.content.rev.core.graphics.Parameter;
import org.reldb.dbrowser.dbui.content.rev.core.graphics.Visualiser;
import org.reldb.rel.client.Attribute;
import org.reldb.rel.client.Heading;
import org.reldb.rel.client.Tuples;

public abstract class VisualiserOfOperator extends VisualiserOfRel {
	
	private static final long serialVersionUID = 1L;
	
	private String kind;
		
	public VisualiserOfOperator(Rev rev, String kind, String name, int xpos, int ypos) {
		super(rev, name);
		this.kind = kind;
		setLocation(xpos, ypos);
	}
	
	public String getKind() {
		return kind;
	}
	
	protected String getQuery() {
		return "";
	}

	public Argument createDefaultOperand(final Parameter parameter) {
		String operandName = getName() + parameter.getName();
		Visualiser operand = new VisualiserOfOperand(getModel(), operandName, Math.max(0, getX() - 140), getY());
		getModel().refresh();
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
				if (parameter.getConnection(0).getVisualiser() instanceof VisualiserOfOperand)
					out += "Name ''";
				else
					out += "Name '" + parameter.getConnection(0).getVisualiser().getName() + "'";
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
		if (connected instanceof VisualiserOfOperand) {
			return null;
		}
		return connected;
	}
	
	protected Attribute[] getAttributes(Parameter operand) {
		Visualiser connect = getConnected(operand);
		VisualiserOfRel connected = (VisualiserOfRel)connect;
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
		DatabaseAbstractionLayer.updateQueryPosition(getRev().getConnection(), getName(), getX(), getY(), getKind(), getConnections(), getModel().getName());
	}
	
	/** Override to be notified that this Visualiser is being removed from the Model. */
	public void removing() {
		super.removing();
		DatabaseAbstractionLayer.removeOperator(getRev().getConnection(), getName());
	}
	
	private void updatePositionInDatabaseEventHandler() {
		if (SwingUtilities.isEventDispatchThread())
			(new SwingWorker<Object, Object>() {
				protected Object doInBackground() throws Exception {
					updatePositionInDatabase();
					return null;
				}			
			}).execute();
		else
			updatePositionInDatabase();
	}
	
	public void moved() {
		super.moved();
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
			private static final long serialVersionUID = 1L;
			private void disconnect() {
				removeConnections();
				reconnect();				
			}
			private void reconnect() {
				createDefaultOperand(this);				
			}
			public void handleMouseClick(java.awt.event.MouseEvent evt) {
				JPopupMenu popup = new JPopupMenu();
				JMenuItem menuItem = new JMenuItem("Disconnect");
				menuItem.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						disconnect();
					}
				});
				popup.add(menuItem);
				if (getConnectionCount() == 0) {
					menuItem = new JMenuItem("Connect");
					menuItem.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							reconnect();
						}
					});
					popup.add(menuItem);
				}
	            popup.show(evt.getComponent(), evt.getX(), evt.getY());
		    }
		});
	}
	
}
