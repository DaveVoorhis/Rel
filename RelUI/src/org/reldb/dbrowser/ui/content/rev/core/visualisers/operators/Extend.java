package org.reldb.dbrowser.ui.content.rev.core.visualisers.operators;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.eclipse.swt.graphics.Point;
import org.reldb.dbrowser.ui.content.rev.core.DatabaseAbstractionLayer;
import org.reldb.dbrowser.ui.content.rev.core.Rev;
import org.reldb.dbrowser.ui.content.rev.core.graphics.Parameter;
import org.reldb.dbrowser.ui.content.rev.core.graphics.Visualiser;
import org.reldb.dbrowser.ui.content.rev.core.visualisers.Operator;
import org.reldb.dbrowser.ui.content.rev.core.visualisers.Relation;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;

public class Extend extends Operator {
	protected Parameter operand;
	protected JPanel controlPanel;
	protected LinkedList<JTextField> attributeList;
	protected LinkedList<JTextField> initialValues;
	protected String KeyWord = "EXTEND";
	protected Point initialSize;
	
	public Extend(Rev rev) {
		super(rev, "EXTEND");
		operand = addParameter("Operand", "Relation to be extended.");
	}
	
	public Extend(Rev rev, String name) {
		super(rev, "EXTEND", name);
		operand = addParameter("Operand", "Relation to be extended.");
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
		String qry = "";
		if (attributeList.size() > 0) {
			qry = KeyWord + " " + connected.getVisualiserName() + " : { ";
			int count = 0;
			for (int i=0; i < attributeList.size(); i++) {
				String attribute = attributeList.get(i).getText();
				String initValue = initialValues.get(i).getText();
				//Don't allow empty fields
				if (attribute.length() > 0 && initValue.length() > 0) {
					if (count > 0) {
						qry += ", ";
					}
					qry += attribute + " := ";
					qry += initValue;
					count++;
				}
			}
			qry += " }";
		}
		return qry;
	}
	
	protected void getPreservedState() {
		Visualiser connected = getConnected(operand);
		if (connected == null) {
			return;
		}
		createForm(0);
		Tuples tuples = load();
		int count = 0;
		if (tuples != null) {
			Iterator<Tuple> tupleIterator = tuples.iterator();
			if (tupleIterator.hasNext()) {
				Tuple tuple = tupleIterator.next();
				//Refresh the preserved state when a new connection is made
				String relvar = tuple.get("Relvar").toString();
				boolean skip = false;
				if (!relvar.equals(connected.getVisualiserName())) {
					if (KeyWord.equals("EXTEND")) {
						DatabaseAbstractionLayer.removeOperator_Extend(getRev().getConnection(), getVisualiserName());
					} else if (KeyWord.equals("SUMMARIZE")) {
						DatabaseAbstractionLayer.removeOperator_Summarize(getRev().getConnection(), getVisualiserName());
					}
					skip = true;
				}
				if (!skip) {
					Tuples subRelvar = (Tuples)tuple.get("subRelvar");
					for (Tuple row: subRelvar) {
						addRow();
						String attribute = row.get("attribute").toString();
						String expression = row.get("expression").toString();
						if (attribute != null) {
							attributeList.get(count).setText(attribute);
						}
						if (expression != null) {
							initialValues.get(count).setText(expression);
						}
						count++;
					}
				}
			}
		}
		//Add a couple extra to make 3 min
		boolean extra = false;
		while (count < 3) {
			addRow();
			extra = true;
			count++;
		}
		if (extra) {
			updatePreservedState();
		}
	}
	
	private void updatePreservedState() {
		String subRelvar = "subRelvar relation {";
		for (int i=0; i < attributeList.size(); i++) {
			if (i > 0) {
				subRelvar += ",";
			}
			subRelvar += "Tuple {ID " + i + ", attribute '" + attributeList.get(i).getText() + "', expression '" + initialValues.get(i).getText() + "'}";
		}
		subRelvar += "}";
		save(subRelvar);
	}
	
	protected Tuples load() {
		Tuples tuples = DatabaseAbstractionLayer.getPreservedStateExtend(getRev().getConnection(), getVisualiserName());
		return tuples;
	}
	
	protected void save(String save) {
		Visualiser connected = getConnected(operand);
		if (connected == null) {
			return;
		}
		DatabaseAbstractionLayer.updatePreservedStateExtend(getRev().getConnection(), getVisualiserName(), connected.getVisualiserName(), save);
	}
	
	protected void createForm(int rows) {
		//Create some header labels
		JButton add = new JButton("Add row:");
		add.setPreferredSize(new Dimension(100, 20));
		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				addRow();
				getRev().update();
				updatePreservedState();
			}
		});
		JLabel space = new JLabel("");
		controlPanel.add(add);
		controlPanel.add(space);
		JLabel label = new JLabel("Attribute Name:");
		JLabel label2 = new JLabel("Expression:");
		controlPanel.add(label);
		controlPanel.add(label2);
		//Create some rows to start
		for (int i=0; i < rows; i++) {
			addRow();
		}
	}
	
	protected void clear() {
		controlPanel.removeAll();
		attributeList.clear();
		initialValues.clear();
	}
	
	protected void addRow() {
		JTextField attribute = new JTextField();
		JTextField initValue = new JTextField();
		//Events handlers
		attribute.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent arg0) {
				updatePreservedState();
			}
		});
		initValue.addCaretListener(new CaretListener() {
			@Override
			public void caretUpdate(CaretEvent e) {
				updatePreservedState();
			}
		});	
		//Add boxes to the arrays
		attributeList.add(attribute);
		initialValues.add(initValue);
		controlPanel.add(attribute);
		controlPanel.add(initValue);
	}
	
	public void populateCustom() {
		super.populateCustom();	
		//Make sure the containers are initialised
		if (controlPanel == null) {
			controlPanel = new JPanel();
		}
		if (attributeList == null) {
			attributeList = new LinkedList<JTextField>();
		}
		if (initialValues == null) {
			initialValues = new LinkedList<JTextField>();
		}
		//Set up the form
		initialSize = getSize();
		createForm(3);
		controlPanel.setLayout(new GridLayout(0, 2));
		controlPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		/** TODO Fixme 
		add(controlPanel, BorderLayout.SOUTH); */
	}
	
	public void updateVisualiser() {
		super.updateVisualiser();
		if (initialSize != null) {
			setSize(initialSize);
		}
		clear();
		getPreservedState();
	}
	
	/** Override to be notified that this Visualiser is being removed from the Model. */
	public void removing() {
		super.removing();
		DatabaseAbstractionLayer.removeOperator_Extend(getRev().getConnection(), getVisualiserName());
	}
}
