package org.reldb.rel.rev;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.reldb.rel.client.Attribute;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;
import org.reldb.rel.rev.graphics.Parameter;
import org.reldb.rel.rev.graphics.Visualiser;

public class VisualiserOfOperatorProject extends VisualiserOfOperator {
	private static final long serialVersionUID = 1L;
	
	protected Parameter operand;	
	protected JPanel controlPanel;
	protected GridBagConstraints con = new GridBagConstraints();
	private LinkedList<Option> options = new LinkedList<Option>();
	
	public VisualiserOfOperatorProject(Rev rev, String kind, String name, int xpos, int ypos) {
		super(rev, kind, name, xpos, ypos);
		operand = addParameter("Operand", "Relation to be projected");
	}

	private String getProjectionString() {
		String projectString = "";
		String allBut = "";
		//Go through the list and display them
		for (Option selector: options) {
			if (selector.isAllBut()) {
				if (selector.isSelected())
					allBut = "ALL BUT ";
			} else if (selector.isSelected()) {
				if (projectString.length() > 1)
					projectString += ", ";
				projectString += selector.getAttributeName();
			}
		}
		return allBut + projectString;
	}
	
	public String getQuery() {
		Visualiser connect = getConnected(operand);
		if (connect == null) {
			return null;
		}
		VisualiserOfRel connected = (VisualiserOfRel)connect;
		String connectedQuery = connected.getQuery();
		//Don't try to project a null object
		if (connectedQuery == null)
			return null;
		String projectString = getProjectionString();
		//Don't allow empty projections
		if (projectString.length() == 0) {
			return "";
		}
		return "(" + connectedQuery + ") {" + projectString + "}";
	}
	
	private static class Option {
		private JCheckBox checkbox;
		private JSpinner sortOrder;
		private String prompt;
		private boolean isAllBut;
		private int id;
		public Option(JCheckBox box, Attribute attribute, JSpinner sortOrder, int id) {
			this.checkbox = box;
			this.sortOrder = sortOrder;
			this.id = id;
			this.prompt = attribute.getName();
		}
		public Option(JCheckBox box, String prompt, boolean isAllBut) {
			this.checkbox = box;
			this.prompt = prompt;
			this.sortOrder = null;
			this.isAllBut = isAllBut;
		}
		public String getAttributeName() {
			return prompt;
		}
		public void Update(String newName) {
			this.prompt = newName;
		}
		public JSpinner getSortOrder() {
			return sortOrder;
		}
		public boolean isSelected() {
			return checkbox.isSelected();
		}
		public boolean isAllBut() {
			return isAllBut;
		}
		public int getID() {
			return id;
		}
	}
	
	private static class PreservedState {
		private boolean allBut = false;
		private Map<String, String> attributes = new HashMap<String, String>();
		private Map<String, String> sortOrder = new HashMap<String, String>();
		private Map<String, String> isSelected = new HashMap<String, String>();
		public void setAllBut(boolean allBut) {
			this.allBut = allBut;
		}
		public boolean isAllBut() {
			return allBut;
		}
		public void addAttribute(String attribute, String sort, String selected) {
			attributes.put(attribute, attribute);
			sortOrder.put(attribute, sort);
			isSelected.put(attribute, selected);
		}
		public int getSortOrder(String name) {
			if (sortOrder.get(name) != null) {
				return Integer.parseInt(sortOrder.get(name));
			}
			return -1;
		}
		
		public boolean isSelected(String name) {
			if (isSelected.get(name) != null) {
				return isSelected.get(name).equals("true");
			}
			return false;
		}
	}
	
	private PreservedState getPreservedState() {
		Visualiser connected = getConnected(operand);
		if (connected == null) {
			return new PreservedState();
		}
		Tuples tuples = DatabaseAbstractionLayer.getPreservedStateProject(getRev().getConnection(), getName());
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
			DatabaseAbstractionLayer.removeOperator_Project(getRev().getConnection(), getName());
			return preservedState;
		}
		preservedState.setAllBut(tuple.get("AllBut").toBoolean());
		Tuples selections = (Tuples)tuple.get("selections");
		for (Tuple selection: selections)
			preservedState.addAttribute(selection.get("attribute").toString(), selection.get("ID").toString(), selection.get("selected").toString());
		return preservedState;
	}
	
	protected void updatePreservedState() {
		Visualiser connected = getConnected(operand);
		if (connected == null) {
			return;
		}
		String allBut = "AllBut ";
		String selections = "selections relation {";
		int count = 0;
		for (Option option: options) {
			if (option.isAllBut()) {
				allBut += (option.isSelected() ? "true" : "false");
			} else {
				if (count > 0)
					selections += ", ";
				JSpinner box = option.getSortOrder();
				if (box != null) {
					String id = box.getValue().toString();
					selections += "tuple {ID " + id; 
					selections += ", attribute '" + option.getAttributeName() + "'";
					selections += ", selected " + option.isSelected();
					selections += "}";
					count++;
				}
			}
		}
		selections += "}";
		DatabaseAbstractionLayer.updatePreservedStateProject(getRev().getConnection(), getName(), connected.getName(), allBut, selections);
	}
	
	protected JCheckBox addSelection(JPanel panel, String prompt, boolean selected, int id) {
		JCheckBox box = new JCheckBox(prompt, selected);
		box.setToolTipText(Integer.toString(id));
		box.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				try {
					updatePreservedState();
				}
				catch (Exception e) {
				}
			}
		});
		box.setFont(Visualiser.LabelFont);
		con.gridx = 0;
		con.gridy = id;
		con.anchor = GridBagConstraints.WEST;
		controlPanel.add(box, con);
		return box;
	}
	
	protected JSpinner addSpinner(int count, int id) {
		SpinnerModel model = new SpinnerNumberModel(1, 1, count, -1);
		JSpinner spinner = new JSpinner(model);
		spinner.setSize(0, spinner.getSize().height);
		spinner.setValue(id);
		con.gridx = 1;
		controlPanel.add(spinner, con);
		spinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				spinnerChanged(arg0);
			}
		});
		return spinner;
	}
	
	protected void spinnerChanged(ChangeEvent arg0) {
		int now = -1;
		JSpinner spinner = (JSpinner)arg0.getSource();
		for (int i=0; i < options.size(); i++) {
			if (options.get(i).getSortOrder() == spinner) {
				now = i;
			}
		}
		if (now == -1) {
			return;
		}
		Option option = options.get(now);
		int previousValue = option.getID();
		int prev = now;
		int current = Integer.parseInt(option.getSortOrder().getValue().toString());
		if (previousValue > current) {
			prev--;
		}
		else if (previousValue < current) {
			prev++;
		}				
		//Update the other box
		if (prev >= 0 && prev < options.size()) {
			JSpinner other = options.get(prev).getSortOrder();
			if (other != null) {
				String currentTxt = option.checkbox.getText();
				String currentPrompt = option.getAttributeName();
				boolean currentSel = option.checkbox.isSelected();
				Option otherOption = options.get(prev);
				option.checkbox.setText(otherOption.checkbox.getText());
				option.checkbox.setSelected(otherOption.checkbox.isSelected());
				option.Update(otherOption.getAttributeName());
				spinner.setValue(previousValue);
				otherOption.checkbox.setText(currentTxt);
				otherOption.checkbox.setSelected(currentSel);
				otherOption.Update(currentPrompt);
			}
		}
	}
	
	protected void addSelectionAllBut(JPanel panel, boolean selected) {
		String prompt = "ALL BUT";
		options.add(new Option(addSelection(panel, prompt, selected, 0), prompt, true));	
		JLabel space = new JLabel("");
		con.gridx = 1;
		controlPanel.add(space, con);
	}
	
	protected void addSelection(JPanel panel, Attribute attribute, boolean selected, int count, int id) {
		String prompt = attribute.getName() + " (" + attribute.getType() + ")";
		options.set(id, new Option(addSelection(panel, prompt, selected, id), attribute, addSpinner(count, id), id));
	}
	
	protected Dimension initialSize;
	
	protected void showAttributes() {
		controlPanel.removeAll();
		if (options == null)
			return;
		options.clear();
		setSize(initialSize);
		Attribute[] attributes = getAttributes(operand);
		if (attributes != null) {
			int count = attributes.length;
			PreservedState preservedState = getPreservedState();
			addSelectionAllBut(controlPanel, preservedState.isAllBut());
			for (int i=0; i < count; i++) {
				options.add(null);
			}
			int counter = 1;
			for (Attribute attribute: attributes) {
				boolean selected = preservedState.isSelected(attribute.getName());
				int id = preservedState.getSortOrder(attribute.getName());
				if (id == -1) {
					id = counter;
				}
				addSelection(controlPanel, attribute, selected, count, id);
				counter++;
			}
		}
	}
	
	public void populateCustom() {
		super.populateCustom();
		initialSize = getSize();
		if (controlPanel == null)
			controlPanel = new JPanel();
		GridBagLayout layout = new GridBagLayout();
		controlPanel.setLayout(layout);
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
		DatabaseAbstractionLayer.removeOperator_Project(getRev().getConnection(), getName());
	}
	
}
