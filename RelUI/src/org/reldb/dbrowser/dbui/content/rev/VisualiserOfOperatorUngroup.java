package org.reldb.dbrowser.dbui.content.rev;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.reldb.rel.client.Attribute;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;
import org.reldb.rel.rev.graphics.Parameter;
import org.reldb.rel.rev.graphics.Visualiser;

public class VisualiserOfOperatorUngroup extends VisualiserOfOperator {
	private static final long serialVersionUID = 1L;
	
	protected Parameter operand;	
	protected JPanel controlPanel;
	protected LinkedList<Option> options = new LinkedList<Option>();
	protected String keyword = "UNGROUP";
	
	public VisualiserOfOperatorUngroup(Rev rev, String kind, String name, int xpos, int ypos) {
		super(rev, kind, name, xpos, ypos);
		operand = addParameter("Operand", "Relation to be ungrouped.");
	}

	private String getGroupingString(String name) {
		String groupingString = "(" + name + " ";
		int counter = 0;
		for (Option selector: options) {
			if (selector.isSelected()) {
				groupingString += keyword + " ";
				groupingString += selector.getAttributeName();
				if (counter == 0) {
					groupingString += ")";
				}
				counter++;
			}
		}
		return groupingString;
	}
	
	public String getQuery() {
		Visualiser connect = getConnected(operand);
		if (connect == null) {
			return null;
		}
		VisualiserOfRel connected = (VisualiserOfRel)connect;
		String connectedQuery = connected.getQuery();
		if (connectedQuery == null)
			return null;
		return getGroupingString(connect.getName());
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
		public void addAttribute(String attribute) {
			attributes.put(attribute, attribute);
		}
		public boolean isSelected(String name) {
			return (attributes.get(name) != null);
		}
	}
	
	private PreservedState getPreservedState() {
		Visualiser connected = getConnected(operand);
		if (connected == null) {
			return new PreservedState();
		}
		Tuples tuples = load();
		if (tuples == null)
			return new PreservedState();
		Iterator<Tuple> tupleIterator = tuples.iterator();
		if (!tupleIterator.hasNext())
			return new PreservedState();
		Tuple tuple = tupleIterator.next();
		PreservedState preservedState = new PreservedState();
		//Refresh the preserved state when a new connection is made
		String relvar = tuple.get("Relvar").toString();
		if (!relvar.equals(connected.getName())) {
			if (keyword.equals("UNGROUP")) {
				DatabaseAbstractionLayer.removeOperator_Ungroup(getRev().getConnection(), getName());
			} else if (keyword.equals("UNWRAP")) {
				DatabaseAbstractionLayer.removeOperator_Unwrap(getRev().getConnection(), getName());
			}
			return preservedState;
		}
		Tuples selections = (Tuples)tuple.get("selections");
		for (Tuple selection: selections)
			preservedState.addAttribute(selection.get("attribute").toString());
		return preservedState;
	}
	
	private void updatePreservedState() {
		String selections = "selections relation {";
		int count = 0;
		for (Option option: options) {
			if (option.isSelected()) {
				if (count > 0)
					selections += ", ";
				selections += "tuple {attribute '" + option.getAttributeName() + "'}";
				count++;
			}
		}
		selections += "}";
		save(selections);
	}
	
	protected Tuples load() {
		Tuples tuples = DatabaseAbstractionLayer.getPreservedStateUngroup(getRev().getConnection(), getName());
		return tuples;
	}
	
	protected void save(String selections) {
		Visualiser connected = getConnected(operand);
		if (connected == null) {
			return;
		}
		DatabaseAbstractionLayer.updatePreservedStateUngroup(getRev().getConnection(), getName(), connected.getName(), selections);
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
		box.setFont(Visualiser.LabelFont);
		controlPanel.add(box);
		return box;
	}
	
	private void addSelection(JPanel panel, Attribute attribute, boolean selected) {
		String prompt = attribute.getName() + " (" + attribute.getType() + ")";
		options.add(new Option(addSelection(panel, prompt, selected), attribute.getName()));
	}
	
	private Dimension initialSize;
	
	private void showAttributes() {
		controlPanel.removeAll();
		if (options == null)
			return;
		options.clear();
		setSize(initialSize);
		Attribute[] attributes = getAttributes(operand);
		if (attributes != null) {
			PreservedState preservedState = getPreservedState();
			for (Attribute attribute: attributes) {
				//Only show grouped and wrapped items
				if (attribute.getType().toString().startsWith("{")) {
					boolean selected = preservedState.isSelected(attribute.getName());
					addSelection(controlPanel, attribute, selected);
				}
			}
		}
	}
	
	public void populateCustom() {
		super.populateCustom();
		initialSize = getSize();
		if (controlPanel == null)
			controlPanel = new JPanel();
		controlPanel.setLayout(new GridLayout(0, 1));
		controlPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		add(controlPanel, BorderLayout.SOUTH);
		showAttributes();
	}
	
	public void updateVisualiser() {
		super.updateVisualiser();
		showAttributes();
	}
	
	/** Override to be notified that this Visualiser is being removed from the Model. */
	public void removing() {
		super.removing();
		DatabaseAbstractionLayer.removeOperator_Ungroup(getRev().getConnection(), getName());
	}
	
}
