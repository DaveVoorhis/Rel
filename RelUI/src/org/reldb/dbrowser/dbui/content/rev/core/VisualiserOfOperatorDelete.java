package org.reldb.dbrowser.dbui.content.rev.core;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.reldb.rel.client.Attribute;
import org.reldb.rel.client.Heading;
import org.reldb.rel.client.Tuples;
import org.reldb.rel.rev.graphics.Parameter;
import org.reldb.rel.rev.graphics.Visualiser;

public class VisualiserOfOperatorDelete extends VisualiserOfOperator {
	private static final long serialVersionUID = 1L;
	
	private Parameter operand;
	private JPanel controlPanel;
	private Dimension initialSize;
	private JComboBox<String> deleteState;
	private JComboBox<String> attList;
	private JTextField condition;
	
	public VisualiserOfOperatorDelete(Rev rev, String kind, String name, int xpos, int ypos) {
		super(rev, kind, name, xpos, ypos);
		operand = addParameter("Operand", "Relation to be restricted. Condition example: AttributeName='text' or AttributeName>2 ");
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
		int deleteMethod = 0;
		if (deleteState != null) {
			deleteMethod = deleteState.getSelectedIndex();
		}
		String qry = "";
		boolean valid = true;
		switch (deleteMethod) {
		case 0:
			qry = "DELETE " + connected.getName() + " WHERE ";
			qry += attList.getSelectedItem().toString() + " = ";
			if (condition != null) {
				String conditionText = condition.getText();
				if (conditionText.length() > 0) {
					qry += conditionText;
				} else {
					valid = false;
				}
			}
			qry += ";";
			break;
		case 1:
			qry = "DELETE " + connected.getName() + ";";
			break;
		case 2:
			qry = "DROP VAR " + connected.getName() + ";";
			break;
		}
		if (!valid) {
			return null;
		}
		//Actually commit the query to the catalog
		DatabaseAbstractionLayer.executeHandler(getRev().getConnection(), qry);
		//Delete the visualiser temporarily until refresh is called
		if (deleteMethod == 2) {
			getRev().deleteVisualiser(connected, this);
		}
		return connectedQuery;
	}
	
	public Attribute[] getAttributes() {
		if (operand == null)
			return null;
		if (operand.getConnection(0) == null)
			return null;
		if (operand.getConnection(0).getVisualiser() instanceof VisualiserOfOperand)
			return null;
		VisualiserOfRel connected = (VisualiserOfRel)operand.getConnection(0).getVisualiser();
		String query = connected.getQuery();
		if (query == null)
			return null;
		Tuples tuples = DatabaseAbstractionLayer.evaluate(getRev().getConnection(), query);
		Heading heading = tuples.getHeading();
		return heading.toArray();
	}
	
	public void createForm() {
		//Reset
		int pos = attList.getSelectedIndex();
		attList.removeAllItems();
		controlPanel.removeAll();
		setSize(initialSize);
		
		if (operand == null) {
			return;
		}
		if (operand.getConnection(0) == null) {
			return;
		}
		if (operand.getConnection(0).getVisualiser() instanceof VisualiserOfOperand) {
			return;
		}
		
		//Create the condition box
		JLabel space = new JLabel("");
		controlPanel.add(deleteState);
		if (deleteState != null) {
			if (deleteState.getSelectedIndex() > 0) {
				return;
			}
		}
		controlPanel.add(space);
		controlPanel.add(attList);
		controlPanel.add(condition);
		
		//Populate the attribute list for the drop down menu
		Attribute[] attributes = getAttributes();
		int count = 0;
		if (attributes != null) {
			for (Attribute attribute: attributes) {
				attList.addItem(attribute.getName());
				count++;
			}
		}
		if (pos >= 0 && pos < count) {
			attList.setSelectedIndex(pos);
		}
		else {
			condition.setText("");	
		}
	}
	
	public void populateCustom() {
		super.populateCustom();
		
		//Make sure the containers are initialised
		if (controlPanel == null) {
			controlPanel = new JPanel();
		}
		if (deleteState == null) {
			String[] deleteStrings = {"DELETE WHERE", "DELETE ALL", "DROP RELATION"};
			deleteState = new JComboBox<String>(deleteStrings);
		}
		if (attList == null) {
			attList = new JComboBox<String>();
		}
		if (condition == null) {
			condition = new JTextField();
		}
		deleteState.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				createForm();
			}
		});
		condition.setPreferredSize(new Dimension(100, 16));
		initialSize = getSize();
		controlPanel.setLayout(new GridLayout(0, 2));
		controlPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		add(controlPanel, BorderLayout.SOUTH);
	}
	
	public void updatePositionInDatabase() {
	}
	
	public void updateVisualiser() {
		super.updateVisualiser();
		createForm();
	}
	
	/** Override to be notified that this Visualiser is being removed from the Model. */
	public void removing() {
		super.removing();
		DatabaseAbstractionLayer.removeOperator_Project(getRev().getConnection(), getName());
	}
}
