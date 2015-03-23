package org.reldb.rel.rev;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;
import org.reldb.rel.client.Value;

class TableAttribute {
	JComboBox<String> varType;
	JTextField varName;
	JCheckBox primaryKey;
	LinkedList<TableAttribute> subRelation = new LinkedList<TableAttribute>();
	int nested = 0;
	int type = 0; //0 = Normal, 1 = Grouped, 2 = Wrapped
	
	public TableAttribute(JComboBox<String> box, JTextField field, JCheckBox check, int nested) {
		this.varType = box;
		this.varName = field;
		this.primaryKey = check;
		this.nested = nested;
	}
	public void addSubRelation(JComboBox<String> box, JTextField field, int type) {
		TableAttribute sub = new TableAttribute(box, field, null, 1);
		subRelation.add(sub);
		this.type = type;
	}
	public void clearSubRelation() {
		subRelation.clear();
		type = 0;
	}
	
	//Getters
	public TableAttribute getSubRelation(int id) {
		if (id >= 0 && id < getSubRelCount()) {
			return subRelation.get(id);
		}
		return null;
	}
	public int getSubRelCount() {
		return subRelation.size();
	}
	public JComboBox<String> getType() {
		return varType;
	}
	public JTextField getName() {
		return varName;
	}
	public JCheckBox getKey() {
		return primaryKey;
	}
	public int getNested() {
		return nested;
	}
	public int getRelType() {
		return type;
	}
	//Setters
	public void setType(JComboBox<String> newType) {
		varType = newType;
	}
	public void setRelType(int newType) {
		type = newType;
	}
}

public class VisualiserOfNewRelvar extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private Rev rev;
	private JPanel controlPanel;
	private JTextField relvarName;
	private LinkedList<TableAttribute> tableAttributes;
	private Dimension initialSize;
	private boolean callOnce = false;
	private JScrollPane scrollPane;
	private int tempType;
	private int tempParent;
	private int insert;
	
	public VisualiserOfNewRelvar(Rev rev, String kind, String name, int xpos, int ypos) {	
		//super(rev, kind, name, xpos, ypos);
		this.rev = rev;
		setLocation(xpos, ypos);
		populateCustom();
		getOperators(0, true);
	}
	
	
	private void getOperators(int nested, boolean fullRefresh) {
		String query = "sys.Types";
		if (rev == null)
			return;
		//Get the data from the tuples in sys.Types
		Tuples tuples = DatabaseAbstractionLayer.evaluate(rev.getConnection(), query, rev.getCrashHandler());
		if (tuples != null) {
			Iterator<Tuple> it = tuples.iterator();
			LinkedList<String> operators = new LinkedList<String>();
			while (it.hasNext()) {
				Tuple row = it.next();
				if (row != null) {
					Value value = row.getAttributeValue(0);
					operators.add(value.toString());
				}
			}
			//Go through the control panel and remove and then replace the combo boxes
			int boxCount = tableAttributes.size();
			for (int i=0; i < boxCount; i++) {
				int subCount = tableAttributes.get(i).getSubRelCount();
				if (nested == 0) {
					subCount = 1;
				}
				//Go through the sub count for group and wrap operators
				for (int k=0; k < subCount; k++) {
					//Add a couple of extra operators for grouping and wrapping
					LinkedList<String> allOperators = new LinkedList<String>();
					for (int j=0; j < operators.size(); j++)
					{
						allOperators.add(operators.get(j));
					}
					boolean callEvents = false;
					if (tableAttributes.get(i).getNested() == 0 && nested == 0) {
						allOperators.add("RELATION (GROUP)");
						allOperators.add("TUPLE (WRAP)");
						callEvents = true;
					}
					int opCount = allOperators.size();
					JComboBox<String> nextBox = null;
					if (nested == 0) {
						nextBox = tableAttributes.get(i).getType();
					}
					else {
						nextBox = tableAttributes.get(i).getSubRelation(k).getType();
					}
					//Either refresh all or just those not currently set
					if (nextBox.getItemCount() == 0 || fullRefresh) {
						nextBox.removeAllItems();
						nextBox.setToolTipText(Integer.toString(i));
						for (int j=0; j < opCount; j++) {
							nextBox.addItem(allOperators.get(j));
						}
						//Don't add events for sub attributes
						if (!callEvents) {
							continue;
						}
						//Add event handler
						nextBox.addItemListener(new ItemListener() {
							@Override
							public void itemStateChanged(ItemEvent arg0) {
								if (callOnce) {
									callOnce = false;
									return;
								}
								callOnce = true;
								Object caller = arg0.getSource();
								if (caller instanceof JComboBox) {
									@SuppressWarnings("unchecked")//caller already identified as JComboBox above
									JComboBox<String> box = (JComboBox<String>)caller;
									String parentStr = box.getToolTipText();
									int parentid = 0;
									try {
										parentid = Integer.parseInt(parentStr);
									}
									catch (Exception ex) {
										parentid = 0;
									}
									String id = "";
									if (box.getSelectedItem() != null) {
										id = box.getSelectedItem().toString();
									}
									//Group
									if (id == "RELATION (GROUP)") {
										createSubRel(1, parentid);
									}
									//Wrap
									else if (id == "TUPLE (WRAP)") {
										createSubRel(2, parentid);
									}
									//Remove
									else {
										removeSubRel(parentid);
									}
								}
							}
						});
					}
				}
			}
		}
	}
	
	private void createSubRel(int type, int parent) {
		tempType = type;
		tempParent = parent;
		if (parent >= 0 && parent < tableAttributes.size()) {
			int relType = tableAttributes.get(parent).getRelType();
			if (relType == 0) {
				tableAttributes.get(parent).setRelType(type);
			}
			else {
				return;
			}
			//Find the insert position
			insert = 0;
			JCheckBox last = tableAttributes.get(parent).getKey();
			for (int i=0; i < controlPanel.getComponentCount(); i++) {
				if (controlPanel.getComponent(i) == last) {
					insert = i;
				}
			}		
			//Add the headers
			JLabel sub = new JLabel("Sub Relation  |  Type:");
			JLabel attribute = new JLabel("Attribute Name:");
			JLabel space1 = new JLabel("");
			Font subDescFont = new Font("Arial", Font.PLAIN, 10);
			sub.setFont(subDescFont);
			attribute.setFont(subDescFont);
			insert++;
			controlPanel.add(sub, insert);
			insert++;
			controlPanel.add(attribute, insert);
			insert++;
			controlPanel.add(space1, insert);
			//Add the boxes and controls
			for (int i = 0; i < 3; i++) {
				insert = addSubRow(type, parent, insert);
			}
			//Add footer
			JButton addSubRow = new JButton("Add sub-row");
			addSubRow.setToolTipText(Integer.toString(insert));
			addSubRow.setPreferredSize(new Dimension(75, 20));
			addSubRow.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					JButton caller = (JButton)arg0.getSource();
					insert = Integer.parseInt(caller.getToolTipText());
					insert = addSubRow(tempType, tempParent, insert);
					caller.setToolTipText(Integer.toString(insert));
					getOperators(1, false);
				}
			});
			JLabel sub2 = new JLabel("Sub Relation End");
			JLabel space4 = new JLabel("");
			sub2.setFont(subDescFont);
			insert++;
			controlPanel.add(addSubRow, insert);
			insert++;
			controlPanel.add(sub2, insert);
			insert++;
			controlPanel.add(space4, insert);
			//Refresh the combo boxes
			getOperators(1, false);
		}
	}
	
	private int addSubRow(int type, int parent, int insert) {
		JComboBox<String> box = new JComboBox<String>();
		JTextField field = new JTextField();
		tableAttributes.get(parent).addSubRelation(box, field, type);		
		//Add the actual controls
		JLabel space2 = new JLabel("");
		insert++;
		controlPanel.add(box, insert);
		insert++;
		controlPanel.add(field, insert);
		insert++;
		controlPanel.add(space2, insert);
		return insert;
	}
	
	private void removeSubRel(int parent) {
		if (parent >= 0 && parent < tableAttributes.size()) {
			if (tableAttributes.get(parent).getRelType() == 0) {
				return;
			}
			int count = 3 * (tableAttributes.get(parent).getSubRelCount()+2);
			//Remove from the collection
			tableAttributes.get(parent).clearSubRelation();
			//Find the insert position
			int insert = 0;
			JCheckBox last = tableAttributes.get(parent).getKey();
			for (int i=0; i < controlPanel.getComponentCount(); i++) {
				if (controlPanel.getComponent(i) == last) {
					insert = i;
				}
			}
			//Remove the controls
			if (initialSize != null) {
				setSize(initialSize);
			}
			for (int i=insert; i < insert+count; i++) {
				controlPanel.remove(insert+1);
			}
		}
	}
	
	private void addRow() {
		JComboBox<String> nextField = new JComboBox<String>();
		JTextField varName = new JTextField();
		JCheckBox key = new JCheckBox();
		TableAttribute att = new TableAttribute(nextField, varName, key, 0);
		tableAttributes.add(att);
		controlPanel.add(nextField);
		controlPanel.add(varName);
		controlPanel.add(key);
	}
	
	private void scrollToEnd() {
		scrollPane.validate();
		scrollPane.getVerticalScrollBar().setValue(this.getHeight());
	}
	
	public String getQuery() {
		//Don't complete if the table name is empty
		if (relvarName.getText().length() == 0) {
			javax.swing.JOptionPane.showMessageDialog(null, "Relvar requires a name, name field cannot be empty", "Relvar name required", javax.swing.JOptionPane.ERROR_MESSAGE);
			return null;
		}
		String qry = "";
		String tableName = "";
		int attCount = tableAttributes.size();
		if (attCount > 0)
		{
			tableName = relvarName.getText();
			qry = "VAR " + tableName + " REAL RELATION { ";
			//Iterate through each attribute
			int actualCount = 0;
			for (int i=0; i < attCount; i++) {
				//Add the basic outer attributes
				JComboBox<String> type = tableAttributes.get(i).getType();
				JTextField name = tableAttributes.get(i).getName();
				String nameStr = name.getText();
				String typeStr = type.getSelectedItem().toString();
				//RELATION = GROUP
				if (typeStr == "RELATION (GROUP)") {
					typeStr = "RELATION";
				}
				//TUPLE = WRAP
				else if (typeStr == "TUPLE (WRAP)") {
					typeStr = "TUPLE";
				}
				//For normal types, only track those boxes entered
				else if (nameStr.length() == 0) {
					continue;
				}
				if (actualCount > 0) {
					qry += ", ";
				}
				actualCount++;
				qry += nameStr + " ";
				qry += typeStr;
				
				//Add any inner attributes
				int relType = tableAttributes.get(i).getRelType();
				if (relType > 0) {
					qry += "{";
					int actualCount2 = 0;
					for (int j=0; j < tableAttributes.get(i).getSubRelCount(); j++) {
						JComboBox<String> type2 = tableAttributes.get(i).getSubRelation(j).getType();
						JTextField name2 = tableAttributes.get(i).getSubRelation(j).getName();
						String nameStr2 = name2.getText();
						String typeStr2 = type2.getSelectedItem().toString();
						//Skip any empty boxes
						if (nameStr2.length() == 0) {
							continue;
						}
						if (actualCount2 > 0) {
							qry += ", ";
						}
						qry += nameStr2 + " ";
						qry += typeStr2;
						actualCount2++;
					}
					qry += "}";
				}
			}
			qry += " }";
			qry += " KEY { ";
			//Add the primary key
			int count = 0;
			for (int i=0; i < attCount; i++) {
				boolean checked = tableAttributes.get(i).getKey().isSelected();
				if (checked) {
					if (count > 0) {
						qry += ", ";
					}
					qry += tableAttributes.get(i).getName().getText();
					count++;
				}
			}
			qry += " };";
		}
		System.out.print(qry + "\n");
		//Actually commit the query to the catalog
		DatabaseAbstractionLayer.executeHandler(rev.getConnection(), qry, rev.getCrashHandler());
		//Create a temporary visualiser until refresh is called
		//Confirm entry was successful
		Tuples tuples = DatabaseAbstractionLayer.evaluate(rev.getConnection(), "sys.Catalog WHERE Name = '" + tableName + "'", rev.getCrashHandler());
		if (tuples != null) {
			if (tuples.iterator().hasNext()) {
				rev.createNewRelvarVisualier(tableName);
			} else {
		        javax.swing.JOptionPane.showMessageDialog(null, "Relvar could not be created.", "Relvar could not be created", javax.swing.JOptionPane.ERROR_MESSAGE);
		        return null;
			}
		} else {
	        javax.swing.JOptionPane.showMessageDialog(null, "Relvar could not be created.", "Relvar could not be created", javax.swing.JOptionPane.ERROR_MESSAGE);
	        return null;
		}
		return tableName;
	}
	
	public void populateCustom() {
		//Make sure the containers are initialised
		if (controlPanel == null) {
			controlPanel = new JPanel();
		}
		controlPanel.removeAll();
		if (tableAttributes == null) {
			tableAttributes = new LinkedList<TableAttribute>();
		}
		tableAttributes.clear();
		initialSize = getSize();
		
		//Add the action buttons
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		//Add row
		JButton addRow = new JButton("Add Row");
		toolBar.add(addRow);
		addRow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				addRow();
				getOperators(0, false);
				scrollToEnd();
			}
		});
		//Save
		JButton save = new JButton("Save");
		toolBar.add(save);
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				getQuery();
			}
		});
		//Clear
		JButton clear = new JButton("Clear");
		toolBar.add(clear);
		clear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				populateCustom();
				getOperators(0, true);
			}
		});
		controlPanel.add(toolBar);
		//New line
		JLabel toolBarSpace1 = new JLabel();
		JLabel toolBarSpace2 = new JLabel();
		controlPanel.add(toolBarSpace1);
		controlPanel.add(toolBarSpace2);
	
		//Add the table name
		JLabel label = new JLabel("Relvar Name: ");
		relvarName = new JTextField();
		JLabel space = new JLabel(""); //Blank label to keep column integrity
		controlPanel.add(label);
		controlPanel.add(relvarName);
		controlPanel.add(space);
		
		//Add the header
		JLabel type = new JLabel("Type:");
		JLabel attribute = new JLabel("Attribute Name:");
		JLabel primary = new JLabel("Key?");
		controlPanel.add(type);
		controlPanel.add(attribute);
		controlPanel.add(primary);
		
		//Create the form fields
		for (int i=0; i < 3; i++)
		{
			addRow();
		}
		controlPanel.setLayout(new GridLayout(0, 3));
		controlPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		add(controlPanel, BorderLayout.SOUTH);	
		//Set scroll pane
		scrollPane = new JScrollPane(this);
		rev.setDetailView(scrollPane);
	}
}
