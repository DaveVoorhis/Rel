package org.reldb.dbrowser.ui.content.rev.core;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.eclipse.swt.graphics.Point;
import org.reldb.dbrowser.ui.content.rev.core.graphics.Parameter;
import org.reldb.dbrowser.ui.content.rev.core.graphics.Visualiser;
import org.reldb.rel.client.Attribute;
import org.reldb.rel.client.Heading;
import org.reldb.rel.client.Tuples;

public class VisualiserOfOperatorDelete extends VisualiserOfOperator {	
	private Parameter operand;
	private JPanel controlPanel;
	private Point initialSize;
	private JComboBox<String> deleteState;
	private JComboBox<String> attList;
	private JTextField condition;
	
	public VisualiserOfOperatorDelete(Rev rev) {
		super(rev, "DELETE");
		operand = addParameter("Operand", "Relation to be restricted. Condition example: AttributeName='text' or AttributeName>2 ");
	}
	
	public String getQuery() {
		Visualiser connect = getConnected(operand);
		if (connect == null) {
			return null;
		}
		VisualiserOfRelation connected = (VisualiserOfRelation)connect;
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
			qry = "DELETE " + connected.getVisualiserName() + " WHERE ";
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
			qry = "DELETE " + connected.getVisualiserName() + ";";
			break;
		case 2:
			qry = "DROP VAR " + connected.getVisualiserName() + ";";
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
		VisualiserOfRelation connected = (VisualiserOfRelation)operand.getConnection(0).getVisualiser();
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
		/** TODO Fixme
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
		*/
	}
	
	public void updatePositionInDatabase() {
	}
	
	public void updateVisualiser() {
		super.updateVisualiser();
		createForm();
	}
}
