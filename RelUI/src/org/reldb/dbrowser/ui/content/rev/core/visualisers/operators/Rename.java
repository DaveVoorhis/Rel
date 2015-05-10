package org.reldb.dbrowser.ui.content.rev.core.visualisers.operators;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.eclipse.swt.graphics.Point;
import org.reldb.dbrowser.ui.content.rev.core.DatabaseAbstractionLayer;
import org.reldb.dbrowser.ui.content.rev.core.Rev;
import org.reldb.dbrowser.ui.content.rev.core.graphics.Parameter;
import org.reldb.dbrowser.ui.content.rev.core.graphics.Visualiser;
import org.reldb.dbrowser.ui.content.rev.core.visualisers.Operator;
import org.reldb.dbrowser.ui.content.rev.core.visualisers.Relation;
import org.reldb.rel.client.Attribute;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;

public class Rename extends Operator {
	private Parameter operand;	
	private JPanel controlPanel;
	private LinkedList<Option> options = new LinkedList<Option>();
	private LinkedList<JTextField> newNames = null;
	
	public Rename(Rev rev) {
		super(rev, "RENAME");
		operand = addParameter("Operand", "Relation to be renamed");
	}
	
	public Rename(Rev rev, String name) {
		super(rev, "RENAME", name);
		operand = addParameter("Operand", "Relation to be renamed");
	}

	private String getRenameString() {
		String renameString = "";
		int count = 0;
		for (Option selector: options) {
			if (selector.isSelected()) {
				if (renameString.length() > 1)
					renameString += ", ";
				renameString += selector.getAttributeName() + " AS ";
				renameString += newNames.get(count).getText();
			}
			count++;
		}
		return renameString;
	}
	
	public String getQuery() {
		Visualiser connect = getConnected(operand);
		if (connect == null) {
			return null;
		}
		Relation connected = (Relation)connect;
		String connectedQuery = connected.getQuery();
		if (connectedQuery == null)
			return null;	
		return "(" + connectedQuery + ") RENAME {" + getRenameString() + "}";
	}
	
	private static class Option {
		private JCheckBox box;
		private String attributeName;
		public Option(JCheckBox box, String attributeName) {
			this.box = box;
			this.attributeName = attributeName;
		}
		public String getAttributeName() {
			return attributeName;
		}
		public boolean isSelected() {
			return box.isSelected();
		}
	}
	
	private static class PreservedState {
		private Map<String, String> attributes = new HashMap<String, String>();
		private Map<String, String> expressions = new HashMap<String, String>();
		public void addAttribute(String attribute, String expression) {
			attributes.put(attribute, attribute);
			expressions.put(attribute, expression);
		}
		public boolean isSelected(String name) {
			return (attributes.get(name) != null);
		}
		public String getExpression(String name) {
			return expressions.get(name);
		}
	}
	
	private PreservedState getPreservedState() {
		Visualiser connected = getConnected(operand);
		if (connected == null) {
			return new PreservedState();
		}
		Tuples tuples = DatabaseAbstractionLayer.getPreservedStateRename(getRev().getConnection(), getVisualiserName());
		if (tuples == null)
			return new PreservedState();
		Iterator<Tuple> tupleIterator = tuples.iterator();
		if (!tupleIterator.hasNext())
			return new PreservedState();
		Tuple tuple = tupleIterator.next();
		PreservedState preservedState = new PreservedState();
		//Refresh the preserved state when a new connection is made
		String relvar = tuple.get("Relvar").toString();
		if (!relvar.equals(connected.getVisualiserName())) {
			DatabaseAbstractionLayer.removeOperator_Rename(getRev().getConnection(), getVisualiserName());
			return preservedState;
		}
		Tuples selections = (Tuples)tuple.get("selections");
		for (Tuple selection: selections) {
			preservedState.addAttribute(selection.get("attribute").toString(), selection.get("expression").toString());
		}
		return preservedState;
	}
	
	private void updatePreservedState() {
		Visualiser connected = getConnected(operand);
		if (connected == null) {
			return;
		}
		String selections = "selections relation {";
		int count = 0;
		int count2 = 0;
		for (Option option: options) {
			if (option.isSelected()) {
				if (count > 0)
					selections += ", ";
				selections += "tuple {attribute '" + option.getAttributeName() + "'";
				selections += ", expression '" + newNames.get(count2).getText() + "'";
				selections += "}";
				count++;
			}
			count2++;
		}
		selections += "}";
		DatabaseAbstractionLayer.updatePreservedStateRename(getRev().getConnection(), getVisualiserName(), connected.getVisualiserName(), selections);
	}
	
	private JCheckBox addSelection(JPanel panel, String prompt, boolean selected) {
		JCheckBox box = new JCheckBox(prompt, selected);
		box.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				(new SwingWorker<Object, Object>() {
					protected Object doInBackground() throws Exception {
						updatePreservedState();
						return null;
					}	
				}).execute();
			}
		});
//		box.setFont(Visualiser.LabelFont);
		controlPanel.add(box);
		return box;
	}
	
	private void addSelection(JPanel panel, Attribute attribute, boolean selected) {
		String prompt = attribute.getName() + " (" + attribute.getType() + ")";
		options.add(new Option(addSelection(panel, prompt, selected), attribute.getName()));
	}
	
	private Point initialSize;
	
	private void showAttributes() {
		controlPanel.removeAll();
		if (options == null)
			return;
		options.clear();
		setSize(initialSize);
		if (newNames == null)
		{
			newNames = new LinkedList<JTextField>();
		}
		newNames.clear();
		Attribute[] attributes = getAttributes(operand);
		if (attributes != null) {
			PreservedState preservedState = getPreservedState();
			for (Attribute attribute: attributes) {
				boolean selected = preservedState.isSelected(attribute.getName());
				addSelection(controlPanel, attribute, selected);
				//Add box for the expression
				JTextField box = new JTextField();
				String expression = preservedState.getExpression(attribute.getName());
				if (expression != null) {
					box.setText(expression);
				}
				//Add the event handler
				box.addCaretListener(new CaretListener() {
					public void caretUpdate(CaretEvent e) {
						getRev().getModel().refresh();
						(new SwingWorker<Object, Object>() {
							protected Object doInBackground() throws Exception {
								updatePreservedState();
								return null;
							}					
						}).execute();
					}
				});
				newNames.add(box);
				controlPanel.add(box);
			}
		}
	}
	
	public void populateCustom() {
		super.populateCustom();
		initialSize = getSize();
		if (controlPanel == null)
			controlPanel = new JPanel();
		controlPanel.setLayout(new GridLayout(0, 2));
		controlPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		/** TODO Fixme 
		add(controlPanel, BorderLayout.SOUTH); */
		showAttributes();
	}
	
	public void updateVisualiser() {
		super.updateVisualiser();
		showAttributes();
	}
	
	/** Override to be notified that this Visualiser is being removed from the Model. */
	public void removing() {
		super.removing();
		DatabaseAbstractionLayer.removeOperator_Rename(getRev().getConnection(), getVisualiserName());
	}
	
}
