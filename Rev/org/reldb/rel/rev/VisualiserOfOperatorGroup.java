package org.reldb.rel.rev;

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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.reldb.rel.client.Attribute;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;
import org.reldb.rel.rev.graphics.Parameter;
import org.reldb.rel.rev.graphics.Visualiser;

public class VisualiserOfOperatorGroup extends VisualiserOfOperator {
	private static final long serialVersionUID = 1L;
	
	protected Parameter operand;	
	protected JPanel controlPanel;
	protected JTextField asBox;
	protected String asText;
	protected LinkedList<Option> options = new LinkedList<Option>();
	protected String keyword = "GROUP";
	
	public VisualiserOfOperatorGroup(Rev rev, String kind, String name, int xpos, int ypos) {
		super(rev, kind, name, xpos, ypos);
		operand = addParameter("Operand", "Relation to be grouped.");
	}

	private String getGroupingString() {
		String groupingString = "";
		String allBut = "";
		for (Option selector: options) {
			if (selector.isAllBut()) {
				if (selector.isSelected())
					allBut = "ALL BUT ";
			} else if (selector.isSelected()) {
				if (groupingString.length() > 1)
					groupingString += ", ";
				groupingString += selector.getAttributeName();
			}
		}
		return allBut + groupingString;
	}
	
	public String getQuery() {
		Visualiser connected = getConnected(operand);
		if (connected == null) {
			return null;
		}	
		String groupString = connected.getName() + " " + keyword + " {";
		groupString += getGroupingString();
		String newAttribute = "";
		if (asBox != null) {
			newAttribute = asBox.getText();
		}
		groupString += "} AS " + newAttribute;
		return groupString;
	}
	
	private static class Option {
		private JCheckBox box;
		private String attributeName;
		private boolean isAllBut;
		public Option(JCheckBox box, String attributeName) {
			this.box = box;
			this.attributeName = attributeName;
		}
		public Option(JCheckBox box, String attributeName, boolean isAllBut) {
			this.box = box;
			this.attributeName = attributeName;
			this.isAllBut = isAllBut;
		}		
		public String getAttributeName() {
			return attributeName;
		}
		public boolean isSelected() {
			return box.isSelected();
		}
		public boolean isAllBut() {
			return isAllBut;
		}
	}
	
	private static class PreservedState {
		private boolean allBut = false;
		private Map<String, String> attributes = new HashMap<String, String>();
		public void setAllBut(boolean allBut) {
			this.allBut = allBut;
		}
		public boolean isAllBut() {
			return allBut;
		}
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
			if (keyword.equals("GROUP")) {
				DatabaseAbstractionLayer.removeOperator_Group(getRev().getConnection(), getName(), getRev().getCrashHandler());
			} else if (keyword.equals("WRAP")) {
				DatabaseAbstractionLayer.removeOperator_Wrap(getRev().getConnection(), getName(), getRev().getCrashHandler());
			}
			asText = "";
			asBox.setText("");
			return preservedState;
		}
		preservedState.setAllBut(tuple.get("AllBut").toBoolean());
		Tuples selections = (Tuples)tuple.get("selections");
		for (Tuple selection: selections)
			preservedState.addAttribute(selection.get("attribute").toString());
		String asString = tuple.get("ASText").toString();
		if (asString != null) {
			asText = asString;
		}
		return preservedState;
	}
	
	private void updatePreservedState() {
		String allBut = "AllBut ";
		String selections = "selections relation {";
		int count = 0;
		for (Option option: options) {
			if (option.isAllBut())
				allBut += (option.isSelected() ? "true" : "false");
			else {
				if (option.isSelected()) {
					if (count > 0)
						selections += ", ";
					selections += "tuple {attribute '" + option.getAttributeName() + "'}";
					count++;
				}
			}
		}
		selections += "}";
		String asString = "";
		if (asBox != null) {
			asString = asBox.getText();
		}
		save(allBut, selections, asString);
	}
	
	protected Tuples load() {
		Tuples tuples = DatabaseAbstractionLayer.getPreservedStateGroup(getRev().getConnection(), getName(), getRev().getCrashHandler());
		return tuples;
	}
	
	protected void save(String allBut, String selections, String asString) {
		Visualiser connected = getConnected(operand);
		if (connected == null) {
			return;
		}
		DatabaseAbstractionLayer.updatePreservedStateGroup(getRev().getConnection(), getName(), connected.getName(), allBut, selections, asString, getRev().getCrashHandler());
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
	
	private void addSelectionAllBut(JPanel panel, boolean selected) {
		String prompt = "ALL BUT";
		options.add(new Option(addSelection(panel, prompt, selected), prompt, true));		
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
		int count = 0;
		Attribute[] attributes = getAttributes(operand);
		if (attributes != null) {
			PreservedState preservedState = getPreservedState();
			addSelectionAllBut(controlPanel, preservedState.isAllBut());
			for (Attribute attribute: attributes) {
				boolean selected = preservedState.isSelected(attribute.getName());
				addSelection(controlPanel, attribute, selected);
				count++;
			}
			
			//Add the 'AS' box
			asBox = null;
			if (count > 0)
			{
				JLabel asLabel = new JLabel("  AS");
				asBox = new JTextField();
				asBox.setText(asText);
				asBox.addCaretListener(new CaretListener() {
					@Override
					public void caretUpdate(CaretEvent arg0) {
						updatePreservedState();
					}
				});
				controlPanel.add(asLabel);
				controlPanel.add(asBox);
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
		DatabaseAbstractionLayer.removeOperator_Group(getRev().getConnection(), getName(), getRev().getCrashHandler());
	}
	
}
