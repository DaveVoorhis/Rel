package org.reldb.rel.rev;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

import org.reldb.rel.client.Attribute;
import org.reldb.rel.client.Heading;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;
import org.reldb.rel.rev.graphics.Parameter;
import org.reldb.rel.rev.graphics.Visualiser;

class QueryElementPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	private VisualiserOfOperatorRestrict visualiser;
	private JTextField expValue;
	private JComboBox<String> attributeList;
	private JComboBox<String> operatorList;
	private JComboBox<String> andorOp;
	private boolean callOnce = true;
	private boolean doNotUpdate = false;
	
	public QueryElementPanel(VisualiserOfOperatorRestrict vis)
	{
		visualiser = vis;
		//Add the value box to the restrict visualiser
		expValue = new JTextField();
		expValue.addCaretListener(new CaretListener() {
			public void caretUpdate(CaretEvent e) {
				visualiser.getRev().getModel().refresh();
				(new SwingWorker<Object, Object>() {
					protected Object doInBackground() throws Exception {
						visualiser.updatePreservedState();
						return null;
					}					
				}).execute();
			}
		});
		//Add the combo the operator strings
		String[] operatorStrings = { "=", "<>", "<", ">", "<=", ">=" };
		operatorList = new JComboBox<String>(operatorStrings);
		operatorList.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if (doNotUpdate) {
					visualiser.updatePreservedState();
				}
			}
		});
		//Add the combo box for and / or strings
		String[] andorStrings = { "...", "AND", "OR" };
		andorOp = new JComboBox<String>(andorStrings);
		andorOp.addItemListener(new ItemListener() {	
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (doNotUpdate) {
					return;
				}
				//Add a new panel for and / or operations
				if (andorOp.getSelectedIndex() > 0)
				{
					if (callOnce)
					{
						visualiser.createNewPanel(true);
						callOnce = false;
					}
				}
				//Delete the panel as it is no longer needed
				else
				{
					if (!callOnce)
					{
						visualiser.deletePanel(true);
						callOnce = true;
					}
				}
				visualiser.updatePreservedState();
			}
		});
		andorOp.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}	
			@Override
			public void mouseClicked(MouseEvent arg0) {
				doNotUpdate = false;
			}
		});
		//Add the items to the panel
		if (attributeList == null)
		{
			attributeList = new JComboBox<String>();			
		}
		attributeList.addItemListener(new ItemListener() {		
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (doNotUpdate) {
					visualiser.updatePreservedState();
				}
			}
		});
		//Add the items to the form
		this.add(attributeList);
		this.add(operatorList);
		this.add(expValue);
		this.add(andorOp);
		
		//Set the layout of the panel
		this.setLayout(new GridLayout(0, 1));
		this.setBorder(BorderFactory.createLineBorder(Color.black));
	}
	
	public boolean showAttributes() {
		if (attributeList == null) {
			attributeList = new JComboBox<String>();
		}
		else {
			attributeList.removeAllItems();
			Attribute[] attributes = visualiser.getAttributes();
			if (attributes != null) {
				for (Attribute attribute: attributes) {
					attributeList.addItem(attribute.getName());
				}
				return true;
			}
		}
		return false;
	}
	
	//Accessors
	//Getters
	public JTextField getExpValue() {
		return expValue;
	}
	public JComboBox<String> getAttributeList() {
		return attributeList;
	}
	public JComboBox<String> getOperatorList() {
		return operatorList;
	}
	public JComboBox<String> getAndOrOp() {
		return andorOp;
	}
	//Setters
	public void setExpValue(String value) {
		expValue.setText(value);
	}
	public void setAttribute(int id) {
		if (id >=0 && id < attributeList.getItemCount()) {
			attributeList.setSelectedIndex(id);
			doNotUpdate = true;
		}
	}
	public void setOperator(int id) {
		if (id >=0 && id < operatorList.getItemCount()) {
			operatorList.setSelectedIndex(id);
			doNotUpdate = true;
		}
	}
	public void setAndOrOp(int id) {
		if (id >=0 && id < andorOp.getItemCount()) {
			if (id > 0) {
				callOnce = false;
			}
			andorOp.setSelectedIndex(id);
			doNotUpdate = true;
		}
	}
}

public class VisualiserOfOperatorRestrict extends VisualiserOfOperator {
	private static final long serialVersionUID = 1L;
	
	private Parameter operand;
	private JPanel container;
	private LinkedList<QueryElementPanel> controlPanel;
	private Dimension initialSize;
	private Visualiser connect = null;

	public VisualiserOfOperatorRestrict(Rev rev, String kind, String name, int xpos, int ypos) {
		super(rev, kind, name, xpos, ypos);
		operand = addParameter("Operand", "Relation to be restricted. Condition example: AttributeName='text' or AttributeName>2 ");
	}

	private String getRestrictionString() {
		String restrictString = "";
		for (int i=0; i < controlPanel.size(); i++) {
			//Add the standard elements to the where clause
			QueryElementPanel panel = controlPanel.get(i);
			restrictString += panel.getAttributeList().getSelectedItem().toString();
			restrictString += " " + panel.getOperatorList().getSelectedItem().toString() + " ";
			restrictString += panel.getExpValue().getText();
			//Add a condition if needed
			String andor = panel.getAndOrOp().getSelectedItem().toString();
			if (andor == "AND") {
				restrictString += " AND ";
			}
			else if (andor == "OR") {
				restrictString += " OR ";
			}
		}
		return restrictString;
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
		if (connectedQuery.length() == 0) {
			return null;
		}
		String restrictString = getRestrictionString();
		return "(" + connectedQuery + ") WHERE " + restrictString;
	}
	
	private boolean getPreservedState() {
		Visualiser connected = getConnected(operand);
		if (connected == null) {
			return true;
		}
		Tuples tuples = DatabaseAbstractionLayer.getPreservedStateRestrict(getRev().getConnection(), getName(), getRev().getCrashHandler());
		Iterator<Tuple> tupleIterator = tuples.iterator();
		deleteAll();
		int count = 0;
		while (tupleIterator.hasNext()) {
			Tuple tuple = tupleIterator.next();
			//Refresh the preserved state when a new connection is made
			String relvar = tuple.get("Relvar").toString();
			if (!relvar.equals(connected.getName())) {
				DatabaseAbstractionLayer.removeOperator_Restrict(getRev().getConnection(), getName(), getRev().getCrashHandler());
				return true;
			}
			Tuples panels = (Tuples)tuple.get("Panels");
			for (Tuple panel: panels) {
				createNewPanel(false);
				String expValue = panel.get("expression").toString();
				int attribute = Integer.parseInt(panel.get("Attribute").toString());
				int operator = Integer.parseInt(panel.get("Operators").toString()); 
				int andOrOp = Integer.parseInt(panel.get("AndOrOp").toString());
				if (controlPanel.size() > 0) {
					controlPanel.getLast().setExpValue(expValue);
					controlPanel.getLast().setAttribute(attribute);
					controlPanel.getLast().setOperator(operator);
					controlPanel.getLast().setAndOrOp(andOrOp);
					count++;
				}
			}
		}
		return count == 0;
	}
	
	public void updatePreservedState() {
		Visualiser connected = getConnected(operand);
		if (connected == null) {
			return;
		}		
		int count = controlPanel.size();
		String[] expressions = new String[count];
		int[] attributes = new int[count];
		int[] operators = new int[count];
		int[] andOrOps = new int[count];
		for (int i = 0; i < count; i++) {
			expressions[i] = controlPanel.get(i).getExpValue().getText();
			attributes[i] = controlPanel.get(i).getAttributeList().getSelectedIndex();
			operators[i] = controlPanel.get(i).getOperatorList().getSelectedIndex();
			andOrOps[i] = controlPanel.get(i).getAndOrOp().getSelectedIndex();
		}
		DatabaseAbstractionLayer.updatePreservedStateRestrict(getRev().getConnection(), getName(), connected.getName(), expressions, attributes, operators, andOrOps, count, getRev().getCrashHandler());
	}
	
	public Attribute[] getAttributes() {
		Visualiser connect = getConnected(operand);
		if (connect instanceof VisualiserOfOperand) {
			return null;
		}
		VisualiserOfRel connected = (VisualiserOfRel)connect;
		if (connected == null) {
			return null;
		}
		String query = connected.getQuery();
		if (query == null)
			return null;
		if (query.length() == 0) {
			return null;
		}
		Tuples tuples = DatabaseAbstractionLayer.evaluate(getRev().getConnection(), query, getRev().getCrashHandler());
		Heading heading = tuples.getHeading();
		return heading.toArray();
	}
	
	public void populateCustom() {
		super.populateCustom();	
		//Create an overall panel
		if (container == null) {
			container = new JPanel();
			container.setLayout(new GridLayout(1, 2));
			container.setBorder(BorderFactory.createLineBorder(Color.black));
		}
		//Add the panels to the parent panel
		if (controlPanel == null) {
			controlPanel = new LinkedList<QueryElementPanel>();
		}
		initialSize = getSize();
		add(container, BorderLayout.SOUTH);
	}
	
	public void updateVisualiser() {
		super.updateVisualiser();
		if (initialSize != null) {
			setSize(initialSize);
		}
		if (controlPanel != null) {
			if (operand != null) {
				if (operand.getConnection(0) != null) {
					Visualiser temp = operand.getConnection(0).getVisualiser();
					//When connected
					if (temp instanceof VisualiserOfRel && connect == null) {
						//Load the panels from data or create a blank one
						if (getPreservedState()) {
							createNewPanel(false);
						}
					}
					connect = temp;
				}
				//When unconnected
				else if (connect != null) {
					deleteAll();
					connect = null;
				}
			}
		}
	}
	
	public void createNewPanel(boolean update) {
		if (controlPanel != null) {
			QueryElementPanel panel = new QueryElementPanel(this);
			controlPanel.add(panel);
			container.add(panel);
			if (update) {
				updateVisualiser();
			}
			boolean result = controlPanel.getLast().showAttributes();
			//If it fails, then destroy the panel
			if (!result) {
				deleteAll();
			}
		}
	}
	
	public void deletePanel(boolean update) {
		if (controlPanel != null) {
			QueryElementPanel panel = controlPanel.getLast();
			controlPanel.removeLast();
			container.remove(panel);
			if (update) {
				updateVisualiser();
			}
		}
	}
	
	public void deleteAll() {
		if (controlPanel != null) {
			controlPanel.clear();
			container.removeAll();
		}
	}
	
	/** Override to be notified that this Visualiser is being removed from the Model. */
	public void removing() {
		super.removing();
		DatabaseAbstractionLayer.removeOperator_Restrict(getRev().getConnection(), getName(), getRev().getCrashHandler());
	}
}
