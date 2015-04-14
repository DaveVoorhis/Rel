package org.reldb.dbrowser.dbui.content.rev.core;

import java.awt.GridBagConstraints;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.reldb.rel.client.Attribute;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;
import org.reldb.rel.rev.graphics.Visualiser;

public class VisualiserOfOperatorOrder extends VisualiserOfOperatorProject {
	private static final long serialVersionUID = 1L;
	
	private LinkedList<Option> options = new LinkedList<Option>();
	
	public VisualiserOfOperatorOrder(Rev rev, String kind, String name, int xpos, int ypos) {
		super(rev, kind, name, xpos, ypos);
	}

	private String getOrderString() {
		String orderString = "";
		for (Option selector: options) {
			if (selector == null) {
				continue;
			}
			if (selector.isSelected()) {
				if (orderString.length() > 1)
					orderString += ", ";
				orderString += selector.getSortType().getSelectedItem().toString() + " ";
				orderString += selector.getAttributeName();
			}
		}
		return orderString;
	}
	
	public String getQuery() {
		Visualiser connect = getConnected(operand);
		if (connect instanceof VisualiserOfOperand) {
			return null;
		}
		if (connect == null) {
			return null;
		}
		VisualiserOfRel connected = (VisualiserOfRel)connect;
		String connectedQuery = connected.getQuery();
		if (connectedQuery == null)
			return null;
		String orderString = connected.getQuery() + " ORDER (";
		orderString += getOrderString() + ")";
		return orderString;
	}
	
	private static class Option {
		private JCheckBox checkbox;
		private JSpinner sortOrder;
		private JComboBox<String> sortType;
		private String prompt;
		private int id;
		public Option(JCheckBox box, Attribute attribute, JComboBox<String> sortType, JSpinner sortOrder, int id) {
			this.checkbox = box;
			this.sortOrder = sortOrder;
			this.sortType = sortType;
			this.id = id;
			this.prompt = attribute.getName();
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
		public JComboBox<String> getSortType() {
			return sortType;
		}
		public boolean isSelected() {
			return checkbox.isSelected();
		}
		public int getID() {
			return id;
		}
	}
	
	private static class PreservedState {
		private Map<String, String> attributes = new HashMap<String, String>();
		private Map<String, String> sortOrder = new HashMap<String, String>();
		private Map<String, String> sortType = new HashMap<String, String>();
		private Map<String, String> isSelected = new HashMap<String, String>();
		
		public void addAttribute(String attribute, String sort, String type, String selected) {
			attributes.put(attribute, attribute);
			sortOrder.put(attribute, sort);
			sortType.put(attribute, type);
			isSelected.put(attribute, selected);
		}
		public int getSortOrder(String name) {
			if (sortOrder.get(name) != null) {
				return Integer.parseInt(sortOrder.get(name));
			}
			return -1;
		}
		public int getSortType(String name) {
			if (sortType.get(name) != null) {
				return Integer.parseInt(sortType.get(name));
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
		Tuples tuples = DatabaseAbstractionLayer.getPreservedStateOrder(getRev().getConnection(), getName());
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
			DatabaseAbstractionLayer.removeOperator_Order(getRev().getConnection(), getName());
			return preservedState;
		}
		Tuples selections = (Tuples)tuple.get("selections");
		for (Tuple selection: selections) {
			preservedState.addAttribute(selection.get("attribute").toString(), selection.get("ID").toString(), 
					selection.get("SortType").toString(), selection.get("selected").toString());
		}
		return preservedState;
	}
	
	protected void updatePreservedState() {
		Visualiser connected = getConnected(operand);
		if (connected == null) {
			return;
		}
		String selections = "selections relation {";
		int count = 0;
		for (Option option: options) {
			if (option == null) {
				continue;
			}
			if (count > 0)
				selections += ", ";
			JSpinner box = option.getSortOrder();
			int sortID = 0;
			JComboBox<String> sortType = option.getSortType();
			if (sortType != null) {
				sortID = sortType.getSelectedIndex();
			}
			if (box != null) {
				String id = box.getValue().toString();
				selections += "tuple {ID " + id; 
				selections += ", attribute '" + option.getAttributeName() + "'";
				selections += ", selected " + option.isSelected();
				selections += ", SortType " + sortID;
				selections += "}";
				count++;
			}
		}
		selections += "}";
		DatabaseAbstractionLayer.updatePreservedStateOrder(getRev().getConnection(), getName(), connected.getName(), selections);
	}
	
	protected void showAttributes() {
		controlPanel.removeAll();
		if (options == null)
			return;
		options.clear();
		setSize(initialSize);
		Attribute[] attributes = getAttributes(operand);
		if (attributes != null) {
			PreservedState preservedState = getPreservedState();
			int count = attributes.length;
			options.add(null); //Where the all but clause would be on its inherited class
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
				int sortType = preservedState.getSortType(attribute.getName());
				if (sortType == -1) {
					sortType = 0;
				}
				addSelection(controlPanel, attribute, selected, count, id, sortType);
				counter++;
			}
		}
	}
	
	private JComboBox<String> addSortType(int type) {
		//Add a drop down for sort type
		String[] sortTypeStr = {"ASC", "DESC"};
		JComboBox<String> box = new JComboBox<String>(sortTypeStr);
		if (type >= 0 && type < sortTypeStr.length) {
			box.setSelectedIndex(type);
		}
		con.gridx = 1;
		controlPanel.add(box, con);
		//Add an event handler
		box.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				updatePreservedState();
			}
		});
		return box;
	}
	
	protected JSpinner addSpinner(int count, int id) {
		SpinnerModel model = new SpinnerNumberModel(1, 1, count, -1);
		JSpinner spinner = new JSpinner(model);
		spinner.setSize(0, spinner.getSize().height);
		spinner.setValue(id);
		con.gridx = 2;
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
		for (int i=1; i < options.size(); i++) {
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
				int sortType = option.getSortType().getSelectedIndex();
				Option otherOption = options.get(prev);
				option.checkbox.setText(otherOption.checkbox.getText());
				option.checkbox.setSelected(otherOption.checkbox.isSelected());
				option.Update(otherOption.getAttributeName());
				option.getSortType().setSelectedIndex(otherOption.getSortType().getSelectedIndex());
				spinner.setValue(previousValue);
				otherOption.checkbox.setText(currentTxt);
				otherOption.checkbox.setSelected(currentSel);
				otherOption.Update(currentPrompt);
				otherOption.getSortType().setSelectedIndex(sortType);
			}
		}
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
	
	protected void addSelection(JPanel panel, Attribute attribute, boolean selected, int count, int id, int type) {
		String prompt = attribute.getName() + " (" + attribute.getType() + ")";
		options.set(id, new Option(addSelection(panel, prompt, selected, id), attribute, addSortType(type), addSpinner(count, id), id));
	}
	
	public void populateCustom() {
		super.populateCustom();
	}
	
	public void updateVisualiser() {
		super.updateVisualiser();
	}
	
	/** Override to be notified that this Visualiser is being removed from the Model. */
	public void removing() {
		super.removing();
		DatabaseAbstractionLayer.removeOperator_Order(getRev().getConnection(), getName());
	}
}
