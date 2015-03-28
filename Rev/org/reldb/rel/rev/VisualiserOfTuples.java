package org.reldb.rel.rev;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import org.reldb.rel.client.Attribute;
import org.reldb.rel.client.NullTuples;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;
import org.reldb.rel.client.Value;
import org.reldb.rel.client.Connection.HTMLReceiver;
import org.reldb.rel.rev.graphics.Parameter;

class TextFieldRows
{
	LinkedList<JTextField> TextFieldRow = 	new LinkedList<JTextField>();
	LinkedList<String> PreviousState = 		new LinkedList<String>();
	LinkedList<LinkedList<TextFieldRows>> SubRelation = new LinkedList<LinkedList<TextFieldRows>>(); //Inner = tuple - Outer = attribute
	int attributeID = -1;
	boolean modified = false;
	boolean newField = false;
	
	//Adders
	void add(JTextField box) {
		add(box, false);
	}
	void add(JTextField box, boolean isNew) {
		TextFieldRow.add(box);
		newField = isNew;
		if (box != null) {
			PreviousState.add(box.getText());
		}
		else {
			PreviousState.add(null);
		}
	}
	LinkedList<TextFieldRows> addSubRelation() {
		TextFieldRows singleRow = new TextFieldRows();
		LinkedList<TextFieldRows> rel = new LinkedList<TextFieldRows>();
		rel.add(singleRow);
		SubRelation.add(rel);
		return rel;
	}
	TextFieldRows addSubTuple(int col) {
		if (col >= 0 && col < SubRelation.size()) {
			TextFieldRows rel = new TextFieldRows();
			SubRelation.get(col).add(rel);
			return rel;
		}
		return null;
	}
	//Info
	int size() {
		return TextFieldRow.size();
	}
	boolean isModified() {
		return modified;
	}
	boolean isNew() {
		return newField;
	}
	void findField(JTextField field) {
		//Look for regular boxes
		for (int i=0; i < TextFieldRow.size(); i++) {
			if (TextFieldRow.get(i) == field) {
				modified = true;
			}
		}
		//Look for sub relation boxes
		if (SubRelation != null) {
			//Columns
			for (int i=0; i < SubRelation.size(); i++) {
				//Sub rows
				int subRows = SubRelation.get(i).size();
				for (int k=0; k < subRows; k++) {
					//Each box
					for (int j=0; j < SubRelation.get(i).get(k).size(); j++) {
						if (SubRelation.get(i).get(k).get(j) == field) {
							modified = true;
						}
					}
				}
			}
		}
	}
	int[] findFieldID(JTextField field) {
		int[] result = new int[2];
		result[0] = -1;
		result[1] = -1;
		for (int i=0; i < TextFieldRow.size(); i++) {
			if (getSubRelation(i) == null) {
				if (TextFieldRow.get(i) == field) {
					result[0] = i;
					result[1] = -1;
				}
			}
			else {
				result[1] = -2;
			}
		}
		return result;
	}
	int[] findFieldSubID(JTextField field) {
		int[] result = new int[2];
		result[0] = -1;
		result[1] = -1;
		if (SubRelation != null) {
			//Columns
			for (int i=0; i < SubRelation.size(); i++) {
				//Sub rows
				int subRows = SubRelation.get(i).size();
				for (int k=0; k < subRows; k++) {
					int colid = SubRelation.get(i).get(k).getSubAttribute();
					for (int j=0; j < SubRelation.get(i).get(k).getAttributeCount(); j++) {
						if (SubRelation.get(i).get(k).get(j) == field) {
							result[0] = colid;
							result[1] = j;
						}
					}
				}
			}
		}
		return result;
	}
	//Getters
	JTextField get(int id) {
		if (id >= 0 && id < TextFieldRow.size()) {
			return TextFieldRow.get(id);
		}
		return null;
	}
	String getPreviousState(int id) {
		if (id >= 0 && id < PreviousState.size()) {
			return PreviousState.get(id);
		}
		return null;
	}
	LinkedList<TextFieldRows> getSubRelation(int col) {
		for (int i=0; i < SubRelation.size(); i++) {
			int subAttribute = SubRelation.get(i).get(0).getSubAttribute();
			if (subAttribute == col) {
				return SubRelation.get(i);
			}
		}
		return null;
	}
	TextFieldRows getSubTuple(int col, int row) {
		for (int i=0; i < SubRelation.size(); i++) {
			int subRow = SubRelation.get(i).size();
			if (row >=0 && row < subRow) {
				if (SubRelation.get(i).get(row).getSubAttribute() == col) {
					return SubRelation.get(i).get(row);
				}
			}
		}
		return null;
	}
	int getSubAttribute() {
		return attributeID;
	}
	int getAttributeCount() {
		return TextFieldRow.size();
	}
	//Setters
	void setSubRelationID (int id) {
		attributeID = id;
	}
	void setModified() {
		modified = true;	
	}
	void setNew() {
		newField = true;
	}
	void setSaved() {
		newField = false;
		modified = false;
		for (int i=0; i < PreviousState.size(); i++) {
			if (PreviousState.get(i) != null && TextFieldRow.get(i) != null) {
				String txt = TextFieldRow.get(i).getText();
				PreviousState.set(i, txt);
				txt = VisualiserOfTuples.remQuotes(txt);
				TextFieldRow.get(i).setText(txt);
			}
		}
	}
}

public class VisualiserOfTuples extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private Rev rev;
	private Parameter operand;
	private JPanel controlPanel;
	private int cols;
	private LinkedList<TextFieldRows> DataModel;
	private LinkedList<JLabel> mainHeaderLabels;
	private LinkedList<JLabel[]> subHeaderLabels;
	private LinkedList<JLabel[]> subHeaderSpaces;
	private int[] colsWidths;
	private LinkedList<int[]> subColsWidths;
	private LinkedList<String> groupAndWrappedTypes;
	private GridBagConstraints con = new GridBagConstraints();
	private GridBagConstraints con2 = new GridBagConstraints();
	private GridBagConstraints con3 = new GridBagConstraints();
	private GridBagConstraints con4 = new GridBagConstraints();
	private String query = null;
	private String relvarName = null;
	private JScrollPane scrollPane;
	private boolean lockedHTML;
	private int headerHeight = 70;
	private int headerHeightSub = 35;
	private int textboxHeight = 20;
	private int textboxWidth = 100;
	private int addWidth = 30;
	
	public VisualiserOfTuples(Rev rev, String kind, String name, int xpos, int ypos) {
		this.rev = rev;
		setLocation(xpos, ypos);
		populateCustom();
	}
	
	public static String addQuotes(String txt) {
		if (!txt.startsWith("'") && !txt.endsWith("'")) {
			txt = "'" + txt + "'";
			return txt;
		}
		return txt;
	}
	public static String remQuotes(String txt) {
		if (txt.startsWith("'") && txt.endsWith("'")) {
			txt = txt.substring(1, txt.length() - 1);
			return txt;
		}
		return txt;
	}
	
	public void setQuery(String query, String name) {
		this.query = query;
		this.relvarName = name;
	}
	
	private void getUpdateQuery(String name) {
		Tuples tuples = getTuples(true, false, query);
		Attribute[] attributes = tuples.getHeading().toArray();
		String qry = "";
		int groupedCount = 0;
		
		if (DataModel != null) {
			for (int j=0; j < DataModel.size(); j++) {
				boolean group = false;
				TextFieldRows row = DataModel.get(j);
				if (row != null) {
					//Only check for modified existing values
					if (row.isModified() && !row.isNew()) {
						qry += "DELETE " + name + " WHERE ";
						qry += attributes[0].getName() + " = ";
						String where = "";
						//Standard attributes
						if (row.getPreviousState(0) != null) {
							String prevState = row.getPreviousState(0);
							if (prevState != null) {
								where = prevState;
								//Add the quotes back in
								if (attributes[0].getType().toString().equals("CHARACTER")) {
									where = addQuotes(where);
								}
								qry += where;
							}
						}
						//Modify sub relations
						else if (row.getSubRelation(0) != null) {
							TextFieldRows firstRow = row.getSubTuple(0, 0);
							//Specific to relation groups
							if (groupedCount >= 0 && groupedCount < groupAndWrappedTypes.size()) {
								if (groupAndWrappedTypes.get(groupedCount).equals("RELATION")) {
									group = true;
								}
							}
							if (group) {
								where += "RELATION { ";
							}
							int subRows = row.getSubRelation(0).size();
							//Loop through sub rows
							for (int l=0; l < subRows; l++) {
								TextFieldRows subRow = row.getSubTuple(0, l);
								boolean valid = false;
								//Loop through sub attributes to determine validity
								for (int i=0; i < firstRow.getAttributeCount(); i++) {
									String prev = subRow.getPreviousState(i);
									if (!prev.equals("")) {
										valid = true;
									}
								}
								//Exit if invalid
								if (!valid) {
									continue;
								}
								if (l > 0) {
									where += ", ";
								}
								where += "TUPLE {";
								String attType = attributes[0].getType().toString();
								String[] subAtt = attType.substring(1, attType.length() - 1).split(",");
								//Loop through sub attributes
								for (int i=0; i < firstRow.getAttributeCount(); i++) {
									String[] attAndType = subAtt[i].trim().split(" ");
									if (attAndType.length < 2) {
										continue;
									}
									if (i > 0) {
										where += ", ";
									}
									String prevValue = subRow.getPreviousState(i);
									//Add the quotes back in
									if (attAndType[1].equals("CHARACTER")) {
										prevValue = addQuotes(prevValue);
									}
									where += attAndType[0] + " ";
									where += prevValue;
								}
								where += "}";
							}
							if (group) {
								where += "}";
							}
							qry += where;
						}
						qry += ", ";
					}
				}
			}
		}
		//Insert the data
		getInsertQuery(name, qry);
	}

	private void getInsertQuery(String name, String part1) {
		String qry = "";
		if (part1 != "") {
			qry += part1;
		}
		qry += "INSERT " + name + " RELATION { ";
		Tuples tuples = getTuples(true, false, query);
		Attribute[] attributes = tuples.getHeading().toArray();
		boolean any1 = false;
		
		if (DataModel != null) {
			int count3 = 0;
			//Loop through each tuple (row)
			for (int j=0; j < DataModel.size(); j++) {
				int groupedCount = 0;
				String line = "";
				if (count3 > 0) {
					line += ", ";
				}
				line += "TUPLE { ";	
				TextFieldRows row = DataModel.get(j);
				int rowSize = row.size();
				if (!row.isNew() && !row.isModified()) {
					rowSize = 0;
				}
				boolean any2 = false;
				int count2 = 0;
				//Loop through each attribute (col)
				//Find out if any box in each row contains data
				for (int i=0; i < rowSize; i++) {
					boolean group = false;
					JTextField box = row.get(i);
					//If a box is a normal box
					if (box != null) {		
						if (count2 > 0) {
							line += ", ";
						}
						line += attributes[i].getName() + " ";
						String txt = box.getText();
						if (txt.length() > 0) {
							//Flags
							any1 = true;
							any2 = true;
							//Enclose in quotes
							if (attributes[i].getType().toString().equals("CHARACTER")) {
								txt = addQuotes(txt);
							}
							line += txt;
						}
						else {
							line += "''";
						}
						count2++;
					}
					//Try to find out if it is a sub relation
					else if (row.getSubRelation(i) != null) {
						String attType = attributes[i].getType().toString();
						String[] subAtt = attType.substring(1, attType.length() - 1).split(",");
						int subRows = row.getSubRelation(i).size();
						TextFieldRows firstRow = row.getSubTuple(i, 0);
						if (firstRow.getSubAttribute() == i) {
							//Go through the sub relation
							if (i > 0) {
								line += ", ";
							}
							line += attributes[i].getName() + " ";
							//Specific to relation groups
							if (groupedCount >= 0 && groupedCount < groupAndWrappedTypes.size()) {
								if (groupAndWrappedTypes.get(groupedCount).equals("RELATION")) {
									group = true;
								}
							}
							groupedCount++;
							if (group) {
								line += "RELATION {";
							}
							//Go through the sub relation rows
							for (int l=0; l < subRows; l++) {
								if (l > 0) {
									line += ", ";
								}
								line += "TUPLE {";
								//Go through the sub relation attributes
								int attCount = firstRow.getAttributeCount();
								for (int k=0; k < attCount; k++) {
									String[] attAndType = subAtt[k].trim().split(" ");
									if (attAndType.length < 2) {
										continue;
									}
									TextFieldRows subRow = row.getSubTuple(i, l);
									JTextField field = subRow.get(k);
									if (field != null) {
										if (k > 0) {
											line += ", ";
										}
										line += attAndType[0] + " ";
										String txt = field.getText();
										if (txt.length() > 0) {
											//Flags
											any1 = true;
											any2 = true;
											//Enclose in quotes
											if (attAndType[1].equals("CHARACTER")) {
												txt = addQuotes(txt);
												field.setText(txt);
											}
											line += txt;
										}
										else {
											line += "''";
										}
									}
								}
								line += "}";
							}
							if (group) {
								line += "}";
							}
							count2++;
						}
					}
				}
				line += " }";
				//If any box in the row was modified
				if (any2) {
					qry += line;
					count3++;
				}
				row.setSaved();
			}
		}
		qry += "};";
		//If any box in the whole table was modified
		if (!any1) {
			qry = null;
		}
		//Commit the changes to the database
		System.out.print(qry + "\n");
		if (qry != null) {
			DatabaseAbstractionLayer.executeHandler(rev.getConnection(), qry);
			createNew();
			scrollToEnd();
		}
	}
	
	private void createHeaders(Attribute[] attributes) {
		//Create the table headings
		cols = 0;
		GridBagLayout layout = new GridBagLayout();
		controlPanel.setLayout(layout);
		mainHeaderLabels = new LinkedList<JLabel>();
		mainHeaderLabels.clear();
		subHeaderLabels = new LinkedList<JLabel[]>();
		subHeaderSpaces = new LinkedList<JLabel[]>();
		int groupedCount = 0;
		
		if (attributes != null) {
			for (Attribute attribute: attributes) {
				JPanel subPanel = null;
				boolean extraRow = false;
				boolean group = false;
				String attType = attribute.getType().toString();
				String attTypeLabel = attType;
				//Set up the header for grouped and wrapped attributes
				if (attType.startsWith("{")) {
					while (!getHeadingData()) {
					}
					if (groupedCount >= 0 && groupedCount < groupAndWrappedTypes.size()) {
						attTypeLabel = groupAndWrappedTypes.get(groupedCount);
					} else {
						attTypeLabel = "Error";
					}
					if (attTypeLabel.equals("RELATION")) {
						group = true;
					}
					subPanel = new JPanel();
					extraRow = true;
					groupedCount++;
				}
				//Add the primary row in the label
				String labelTxt = "<html>" + attribute.getName() + "<br/>" + attTypeLabel + "</html>";
				JLabel head = new JLabel(labelTxt);
				head.setBorder(BorderFactory.createMatteBorder(2, 1, 2, 0, Color.black));
				head.setFont(new Font(getName(), Font.BOLD, 12));
				head.setAlignmentY(CENTER_ALIGNMENT);
				head.setAlignmentX(TOP_ALIGNMENT);
				mainHeaderLabels.add(head);
				con.anchor = GridBagConstraints.NORTH;
				con.gridx = cols;
				con.gridy = 1;
				subHeaderLabels.add(null);
				subHeaderSpaces.add(null);
				subColsWidths.add(null);
				
				//Add a secondary row in the label for sub Types
				if (extraRow) {
					con2.gridx = 0;
					con2.gridy = 0;
					con2.anchor = GridBagConstraints.WEST;
					subPanel.add(head, con2);
					con.anchor = GridBagConstraints.NORTH;
					controlPanel.add(subPanel, con);
					String[] subRel = attType.substring(1, attType.length() - 1).split(",");
					int subCols = subRel.length;
					subPanel.setLayout(layout);
					//Add an extra space to cover the buttons for grouped but not wrapped
					int offset = 0;
					if (group) {
						offset++;
					}
					LinkedList<JLabel> subLabels = new LinkedList<JLabel>();
					subHeaderLabels.set(cols, new JLabel[subCols]);
					subHeaderSpaces.set(cols, new JLabel[subCols+offset]);
					subHeaderSpaces.get(cols)[0] = head;

					//Add some blank spaces to make the top part of the header equal to the bottom
					for (int i=1; i < subCols + offset; i++) {
						con2.gridx++;
						JLabel space = new JLabel("");
						space.setBorder(BorderFactory.createMatteBorder(2, 0, 2, 0, Color.black));
						subPanel.add(space, con2);
						subHeaderSpaces.get(cols)[i] = space;
						//Set the final width
						if (offset > 0 && i==subCols) {
							space.setPreferredSize(new Dimension(addWidth, 0));
						}
					}
					con2.gridx = 0;
					con2.gridy = 1;
					subColsWidths.set(cols, new int[subCols]);
					//Add the extra columns
					for (int i=0; i < subCols; i++) {
						String[] attAndType = subRel[i].trim().split(" ");
						if (attAndType.length < 2) {
							continue;
						}
						String subTxt = "<html>" + attAndType[0] + "<br/>" + attAndType[1] + "</html>";
						JLabel sub = new JLabel(subTxt);
						sub.setBorder(BorderFactory.createMatteBorder(2, 1, 2, 0, Color.black));
						sub.setFont(new Font(getName(), Font.BOLD, 10));
						subLabels.add(sub);
						subPanel.add(sub, con2);
						subHeaderLabels.get(cols)[i] = sub;
						con2.gridx++;
						int width = subHeaderLabels.get(cols)[i].getPreferredSize().width;
						if (width < textboxWidth) {
							width = textboxWidth;
						}
						subColsWidths.get(cols)[i] = width;
					}
				}
				//Add just one label
				else {
					con.anchor = GridBagConstraints.NORTH;
					controlPanel.add(head, con);
					//Set up column widths
					int width = head.getPreferredSize().width;
					if (width < textboxWidth) {
						width = textboxWidth;
					}
					colsWidths[cols] = width;
				}
				cols++;
			}
		}
	}
	
	private void createTable(Attribute[] attributes, Tuples tuples) {
		Iterator<Tuple> it = tuples.iterator();
		int rowCount = 0;
		int cols = attributes.length;
		
		//Iterate through the tuples (row id)
		while (it.hasNext()) {
			Tuple row = it.next();
			TextFieldRows nextRow = new TextFieldRows();
			DataModel.add(nextRow);
			int subCount = 0;
			if (row != null) {
				//Iterate through the attributes (col id)
				for (int i=0; i < cols; i++) {
					con.gridx = i;
					con.gridy = rowCount + 2;
					//Retrieve the data
					String attType = attributes[i].getType().toString();
					Value value = row.getAttributeValue(i);
					String valueStr = value.toString();
					LinkedList<LinkedList<String>> subRel = new LinkedList<LinkedList<String>>();
					String[] subAtt = null;
					int subBoxes = 1;
					boolean extraRow = false;
					boolean group = false;
					JPanel subData = null;
					Tuples subRow = null;
					Tuple subWrap = null;
					subAtt = attType.substring(1, attType.length() - 1).split(",");
					subBoxes = subAtt.length;
					int subRowCount = 0;
					
					//For sub attributes
					if (attType.startsWith("{")) {
						//Group
						if (row.get(i) instanceof Tuples) {
							subRow = (Tuples)row.get(i);
							//Add to the list
							if (subBoxes > 1) {
								for (Tuple sub: subRow) {
									subRel.add(new LinkedList<String>());
									for (int z = 0; z < subBoxes; z++) {
										subRel.get(subRowCount).add(sub.getAttributeValue(z).toString());
									}
									subRowCount++;
								}
							}
							group = true;
						}
						//Wrap
						else if (row.get(i) instanceof Tuple) {
							subWrap = (Tuple)row.get(i);
							//Add to the list
							if (subBoxes > 1) {
								subRel.add(new LinkedList<String>());
								for (int z = 0; z < subBoxes; z++) {
									subRel.get(0).add(subWrap.getAttributeValue(z).toString());
								}
								subRowCount++;
							}
							group = false;
						}
						subData = new JPanel();
						subData.setBorder(BorderFactory.createLineBorder(Color.black));
						GridBagLayout layout2 = new GridBagLayout();
						subData.setLayout(layout2);
						extraRow = true;
					}
					else {
						subRowCount++;
					}
					//End for sub attributes
					
					LinkedList<TextFieldRows> subRelation = null;
					int sumWidth = 0;
					//Iterate through the sub rows
					for (int l=0; l < subRowCount; l++) {
						//Iterate through the sub attributes (just 1 for normal data types)
						//Create multiple boxes for grouped items
						for (int k=0; k < subBoxes; k++) {
							//Cancel if it is out of bounds
							String[] attAndType2 = null;
							if (extraRow) {
								if (subAtt != null) {
									attAndType2 = subAtt[k].trim().split(" ");
								}
							}
							//Set up the string value
							if (attAndType2 != null) {
								if (attAndType2.length == 2) {
									if (l < subRel.size()) {
										valueStr = subRel.get(l).get(k);
									}
								}
							}
	
							//Strip quotes
							valueStr = remQuotes(valueStr);
							JTextField field = new JTextField();
							field.setText(valueStr);
							//Set up the column width
							sumWidth += field.getPreferredSize().width;
							//Create a set of boxes for sub relations
							if (extraRow) {
								int width = field.getPreferredSize().width;
								if (width > subColsWidths.get(i)[k]) {
									subColsWidths.get(i)[k] = width;
								}
								con4.gridx = k;
								con4.gridy = l;
								subData.add(field, con4);
								//First sub box only
								if (k == 0) {
									if (l == 0) {
										controlPanel.add(subData, con);
										nextRow.add(null);
									}
									if (subRelation == null) {
										subRelation = nextRow.addSubRelation();
									} else {
										nextRow.addSubTuple(subCount);
									}
									subRelation.get(l).setSubRelationID(i);
								}
								//Add the sub relation
								if (subRelation != null) {
									subRelation.get(l).add(field, false);
								}
							}
							//Create just one
							else {
								controlPanel.add(field, con);
								nextRow.add(field);
							}
							
							//Add a listener for focusing
							field.addFocusListener(new FocusListener() {
								@Override
								public void focusLost(FocusEvent arg0) {
									sanitizeInput(arg0);
								}
								@Override
								public void focusGained(FocusEvent arg0) {
									Component caller = arg0.getComponent();
									if (caller instanceof JTextField) {
										JTextField field = (JTextField)caller;
										field.selectAll();
									}
								}
							});
							//Add a listener for text change
							field.addKeyListener(new KeyListener() {
								@Override
								public void keyTyped(KeyEvent arg0) {
									updateField(arg0);	
								}
								@Override
								public void keyReleased(KeyEvent arg0) {
								}
								@Override
								public void keyPressed(KeyEvent arg0) {
								}
							});
						}
					}
					//Add a button to add more rows
					if (group) {
						JButton add = new JButton("+");
						add.setToolTipText(Integer.toString(subCount) + "," + Integer.toString(i) + "," + Integer.toString(rowCount));
						subData.add(add);
						add.setPreferredSize(new Dimension(addWidth, textboxHeight));
						sumWidth += addWidth;
						add.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								JButton caller = (JButton)arg0.getSource();
								String[] txt = caller.getToolTipText().split(",");
								int att = Integer.parseInt(txt[0]);
								int col = Integer.parseInt(txt[1]);
								int row = Integer.parseInt(txt[2]);
								addSubTuple(att, col, row);
							}
						});
					}
					//Increment column counter
					if (extraRow) {
						subCount++;
					}
					//Add the width to the column
					if (sumWidth > colsWidths[i]) {
						colsWidths[i] = sumWidth;
					}
				}
			}
			rowCount++;
		}
		//Resize the headers and data
		resizeHeadersAndData(cols, rowCount, true, true);
	}
	
	private void sanitizeInput(FocusEvent arg0) {
		Component caller = arg0.getComponent();
		if (caller instanceof JTextField) {
			JTextField field = (JTextField)caller;
			String txt = field.getText();
			int id = -1;
			int subid = -1;
			int count = 0;
			boolean sub = false;
			boolean found = false;
			//Find the id
			while (count < DataModel.size() && !found) {
				int[] ids = DataModel.get(count).findFieldID(field);
				id = ids[0];
				subid = ids[1];
				//If it has been found then exit
				if (id >= 0) {
					sub = false;
					found = true;
				}
				//Look for sub relations
				if (id == -1 && subid == -2) {
					ids = DataModel.get(count).findFieldSubID(field);
					id = ids[0];
					subid = ids[1];
					//Try to exit again
					if (subid >=0) {
						sub = true;
						found = true;
					}
				}
				count++;
			}
			//Find the type
			String type = "";
			JLabel label = null;
			//Normal types
			if (!sub) {
				if (id >= 0 && id < DataModel.get(0).size()) {
					label = mainHeaderLabels.get(id);
				}
			}
			//Grouped types
			else {
				if (id >= 0 && id < DataModel.get(0).size()) {
					if (subid >= 0 && subid < subHeaderLabels.get(id).length) {
						label = subHeaderLabels.get(id)[subid];
					}
				}
			}
			//Setup the text from the label
			if (label != null) {
				String labelTxt = label.getText();
				int start = labelTxt.indexOf(">");
				start = labelTxt.indexOf(">", start+1) + 1;
				int end = labelTxt.lastIndexOf("<");
				type = labelTxt.substring(start, end);
			}
			//Filter out types
			boolean valid = true;
			//Empty
			if (txt.length() == 0) {
				valid = true;
			}
			//Integers
			else if (type.equals("INTEGER")) {
				try {
					Integer.parseInt(txt);
					valid = true;
				} catch (Exception e) {
					valid = false;
				}
			}
			//Rationals
			else if (type.equals("RATIONAL")) {
				try {
					Float.parseFloat(txt);
					valid = true;
				} catch (Exception e) {
					valid = false;
				}
			}
			//Booleans
			else if (type.equals("BOOLEAN")) {
				if (txt.equals("true") || txt.equals("TRUE")
					|| txt.equals("false") || txt.equals("FALSE")) {
					valid = true;
				} else {
					valid = false;
				}
			}
			//Error message
			if (!valid) {
				javax.swing.JOptionPane.showMessageDialog(null, "This field require a type of: " + type + " please enter a new value.", "Invalid input", javax.swing.JOptionPane.ERROR_MESSAGE);
				field.grabFocus();
			}
		}
	}
	
	private void resizeHeadersAndData(int cols, int rowCount, boolean headers, boolean main) {
		//Make sure the table fits in the view
		if (headers) {
			stretchOrFit();
		}	
		//Resize the headers
		if (headers) {
			//If all attributes are normal types then use smaller height
			boolean allSmall = true;
			for (int i=0; i < cols; i++) {
				if (subColsWidths.get(i) != null) {
					allSmall = false;
				}
			}
			for (int i=0; i < cols; i++) {
				//Grouped attributes
				if (subHeaderLabels.get(i) != null) {
					JLabel label = mainHeaderLabels.get(i);
					int width = label.getPreferredSize().width;
					if (subColsWidths.get(i) != null) {
						width = subColsWidths.get(i)[0];
					}
					label.setPreferredSize(new Dimension(width, headerHeightSub));
					for (int k=0; k < subHeaderLabels.get(i).length; k++) {
						//Top part
						JLabel top = subHeaderSpaces.get(i)[k];
						width = top.getPreferredSize().width;
						if (subColsWidths.get(i) != null) {
							width = subColsWidths.get(i)[k];
						}
						top.setPreferredSize(new Dimension(width, headerHeightSub));
						//Bottom part
						JLabel sub = subHeaderLabels.get(i)[k];
						sub.setPreferredSize(new Dimension(width, headerHeightSub));
					}
				}
				//Normal attributes
				else {
					JLabel label = mainHeaderLabels.get(i);
					int height = headerHeight;
					if (allSmall) {
						height = headerHeightSub;
					}
					int width = colsWidths[i];
					label.setPreferredSize(new Dimension(width, height));	
				}
			}
		}
		//Resize the boxes
		if (main) {
			for (int j=0; j < rowCount; j++) {
				TextFieldRows row = DataModel.get(j);
				for (int i=0; i < cols; i++) {
					//Grouped attributes
					if (row.getSubRelation(i) != null) {
						for (int l=0; l < row.getSubRelation(i).size(); l++) {
							TextFieldRows sub = row.getSubTuple(i, l);
							for (int k=0; k < sub.size(); k++) {
								JTextField field = sub.get(k);
								int width = subColsWidths.get(i)[k];
								field.setPreferredSize(new Dimension(width, textboxHeight));
							}
						}
					}
					//Normal attributes
					else {
						JTextField field = row.get(i);
						int width = colsWidths[i];
						field.setPreferredSize(new Dimension(width, textboxHeight));
					}
				}
			}
		}
	}
	
	private void stretchOrFit() {
		boolean overSize = false;
		boolean underSize = false;
		int totalWidth = 0;
		int totalFixed = 0;
		int leftOverWidth = 0;
		int overSizeColWidth = 0;
		int overSizeColCount = 0;
		int panelWidth = getWidth();
		
		//Calculate the total width
		for (int i=0; i < cols; i++) {
			if (subColsWidths.get(i) != null) {
				for (int k=0; k < subColsWidths.get(i).length; k++) {
					totalWidth += subColsWidths.get(i)[k];
				}
			} else {
				totalWidth += colsWidths[i];
			}
			//Too big and needs fitting
			if (totalWidth > panelWidth) {
				overSize = true;
			}
		}			
		//Too small and needs stretching
		if (totalWidth < panelWidth / 2.5) {
			underSize = true;
		}
		
		//For oversized tables only
		if (overSize) {
			//Determine how many columns need adjusting
			for (int i=0; i < cols; i++) {
				//Grouped attributes
				if (subColsWidths.get(i) != null) {
					for (int k=0; k < subColsWidths.get(i).length; k++) {
						if (subColsWidths.get(i)[k] <= panelWidth / 5) {
							totalFixed += subColsWidths.get(i)[k];
						} else {
							overSizeColCount++;
							subColsWidths.get(i)[k] = -1;
						}
					}
				}
				//Normal attributes
				else {
					if (colsWidths[i] <= panelWidth / 5) {
						totalFixed += colsWidths[i];
					} else {
						overSizeColCount++;
						colsWidths[i] = -1;
					}
				}
			}
			//Calculate new width
			leftOverWidth = panelWidth - totalFixed;
			overSizeColWidth = leftOverWidth / overSizeColCount;
			//Update new widths
			for (int i=0; i < cols; i++) {
				if (colsWidths[i] == -1) {
					colsWidths[i] = overSizeColWidth;
				}
				//Repeat for sub relations
				if (subColsWidths.get(i) != null) {
					for (int k=0; k < subColsWidths.get(i).length; k++) {
						if (subColsWidths.get(i)[k] == -1) {
							subColsWidths.get(i)[k] = overSizeColWidth;
						}
					}
				}
			}
		}
		//For undersized tables only
		else if (underSize) {
			//Update new widths
			for (int i=0; i < cols; i++) {
				colsWidths[i] *= 2;
				//Repeat for sub relations
				if (subColsWidths.get(i) != null) {
					for (int k=0; k < subColsWidths.get(i).length; k++) {
						subColsWidths.get(i)[k] *= 2;
					}
				}
			}
		}
	}

	private Tuples getTuples(boolean headingsOnly, boolean createTable, String query) {
		if (query == null) {
			if (operand == null)
				return null;
			if (operand.getConnection(0) == null)
				return null;
			if (operand.getConnection(0).getVisualiser() instanceof VisualiserOfOperand)
				return null;
			VisualiserOfRel connected = (VisualiserOfRel)operand.getConnection(0).getVisualiser();
			query = connected.getQuery();
		}
		if (query == null)
			return null;	
		//Get the tuples
		Tuples tuples = DatabaseAbstractionLayer.evaluate(rev.getConnection(), query);
		//Just return the data
		if (!createTable) {
			return tuples;
		}
		//Create the buttons panel
		con3.gridx = 0;
		con3.gridy = 0;
		createControlButtons(true);
		con3.gridy++;
		add(controlPanel, con3);
		con3.gridy++;
		createControlButtons(false);
		
		//Start building up a table
		if (tuples != null) {
			if (tuples instanceof NullTuples) {
				JLabel message = new JLabel("No tuples or error!");
				controlPanel.add(message);
			}
			else {
				Attribute[] attributes = tuples.getHeading().toArray();
				int cols = attributes.length;
				colsWidths = new int[cols];
				subColsWidths = new LinkedList<int[]>();
				//Create the headers
				createHeaders(attributes);
				//Create the table data
				if (!headingsOnly) {
					createTable(attributes, tuples);
					addTuple(query);
				}
			}
		}
		return null;
	}
	
	private boolean getHeadingData() {
		//Find out the type for grouped and wrapped headers
		//as this is missing is the normal Heading class
		groupAndWrappedTypes = new LinkedList<String>();
		groupAndWrappedTypes.clear();
		lockedHTML = false;
		rev.getConnection().evaluate(query, new HTMLReceiver() {
			@Override
			public void emitInitialHTML(String s) {
				if (s.contains("RELATION")) {
					groupAndWrappedTypes.add("RELATION");
					lockedHTML = true;
				} else if (s.contains("TUPLE")) {
					groupAndWrappedTypes.add("TUPLE");
					lockedHTML = true;
				}
			}
			@Override
			public void endInitialHTML() {
			}
			@Override
			public void emitProgressiveHTML(String s) {
			}
			@Override
			public void endProgressiveHTMLRow() {
			}
		});
		return lockedHTML;
	}

	public void populateCustom() {
		//Make sure the container is initialised
		if (controlPanel == null) {
			controlPanel = new JPanel();
		}
		DataModel = new LinkedList<TextFieldRows>();
		controlPanel.setLayout(new GridLayout(0, 1));
		controlPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		add(controlPanel, BorderLayout.SOUTH);
		this.setLayout(new GridBagLayout());
		//Set scroll pane
		scrollPane = new JScrollPane(this);
		rev.setDetailView(scrollPane);
	}

	private void saveChanges() {
		if (query != null && relvarName != null) {
			getUpdateQuery(relvarName);
		}
	}
	
	private void updateField(KeyEvent arg0) {
		Object caller = arg0.getSource();
		if (caller instanceof JTextField) {
			JTextField field = (JTextField)caller;
			for (int i=0; i < DataModel.size(); i++) {
				DataModel.get(i).findField(field);
			}
			field.setBackground(Color.yellow);
		}
	}
	
	private void addTuple(String query) {
		Tuples header = getTuples(true, false, query);
		TextFieldRows row = new TextFieldRows();
		Attribute[] attributes = header.getHeading().toArray();
		int count = 0;
		int groupWrapCount = 0;
		if (attributes != null) {
			con.gridy++;
			//Get the header to see if sub values are needed
			for (Attribute attribute: attributes) {
				String[] subAtt = null;
				boolean extraRow = false;
				int subBoxes = 1;
				JPanel subPanel = null;
				String attType = attribute.getType().toString();
				if (attType.startsWith("{")) {
					extraRow = true;
					subAtt = attType.substring(1, attType.length() - 1).split(",");
					subBoxes = subAtt.length;
					subPanel = new JPanel();
					GridBagLayout layout2 = new GridBagLayout();
					subPanel.setLayout(layout2);
				}
				LinkedList<TextFieldRows> subRow = null;
				for (int i=0; i < subBoxes; i++) {
					con.gridx = count;
					JTextField field = new JTextField();
					//Group attributes
					if (extraRow) {
						subPanel.add(field);
						if (i == 0) {
							row.add(null, true);
							controlPanel.add(subPanel, con);
							subRow = row.addSubRelation();
							subRow.get(0).setSubRelationID(count);
						}
						if (subRow != null) {
							subRow.get(0).add(field, true);
						}
					}
					//Normal attributes
					else {
						controlPanel.add(field, con);
						row.add(field, true);
					}
					row.setSaved();
					row.setNew();
					//Set event handlers
					field.addFocusListener(new FocusListener() {
						@Override
						public void focusLost(FocusEvent arg0) {
							sanitizeInput(arg0);
						}
						@Override
						public void focusGained(FocusEvent arg0) {
						}
					});
					field.addKeyListener(new KeyListener() {
						@Override
						public void keyTyped(KeyEvent e) {
							updateField(e);
						}
						@Override
						public void keyReleased(KeyEvent e) {
						}	
						@Override
						public void keyPressed(KeyEvent e) {
						}
					});
				}
				//Add a button to add more rows
				if (extraRow) {
					if (groupAndWrappedTypes.get(groupWrapCount).equals("RELATION")) {
						JButton add = new JButton("+");
						subPanel.add(add);
						add.setToolTipText(Integer.toString(groupWrapCount) + "," + Integer.toString(count) + "," + Integer.toString(DataModel.size()));
						add.setPreferredSize(new Dimension(addWidth, textboxHeight));
						add.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								JButton caller = (JButton)arg0.getSource();
								String[] txt = caller.getToolTipText().split(",");
								int att = Integer.parseInt(txt[0]);
								int col = Integer.parseInt(txt[1]);
								int row = Integer.parseInt(txt[2]);
								addSubTuple(att, col, row);
							}
						});
					}
					groupWrapCount++;
				}
				count++;
			}
			DataModel.add(row);
		}
		//Update sizes
		resizeHeadersAndData(count, DataModel.size(), false, true);
	}
	
	private void addSubTuple(int attid, int col, int row) {
		TextFieldRows current = DataModel.get(row);
		TextFieldRows nextRow = current.addSubTuple(attid);
		nextRow.setSubRelationID(col);
		LinkedList<TextFieldRows> subCol = current.getSubRelation(col);
		TextFieldRows subData = subCol.get(0);
		int attCount = subData.size();
		Container container = subData.get(0).getParent();
		JPanel subPanel = (JPanel)container;
		con4.gridy = subCol.size();
		//Add a sub row of boxes
		for (int i=0; i < attCount; i++) {
			con4.gridx = i;
			JTextField field = new JTextField();
			subPanel.add(field, con4);
			nextRow.add(field, true);
			//Set event handlers
			field.addFocusListener(new FocusListener() {
				@Override
				public void focusLost(FocusEvent arg0) {
					sanitizeInput(arg0);
				}
				@Override
				public void focusGained(FocusEvent arg0) {
				}
			});
			field.addKeyListener(new KeyListener() {
				@Override
				public void keyTyped(KeyEvent e) {
					updateField(e);
				}
				@Override
				public void keyReleased(KeyEvent e) {
				}	
				@Override
				public void keyPressed(KeyEvent e) {
				}
			});
		}
		//Update sizes
		int columns = current.getAttributeCount();
		resizeHeadersAndData(columns, DataModel.size(), false, true);
		rev.validate();
	}
	
	private void scrollToTop() {
		scrollPane.validate();
		scrollPane.getVerticalScrollBar().setValue(0);
	}
	private void scrollToEnd() {
		scrollPane.validate();
		scrollPane.getVerticalScrollBar().setValue(this.getHeight());
	}
		
	private void createControlButtons(boolean top) {
		//Add the control buttons
		JToolBar control = new JToolBar();
		control.setFloatable(false);
		JButton save = new JButton("Save");
		JButton newTuple = new JButton("Add");
		JButton topButton = new JButton("Top");
		JButton bottomButton = new JButton("Bottom");
		con.gridx = 0;
		con.gridy = 0;
		con3.anchor = GridBagConstraints.WEST;
		control.add(save);
		control.add(newTuple);
		if (!top) {
			control.add(topButton);
		} else {
			control.add(bottomButton);
		}
		add(control, con3);
		
		//Event handlers	
		save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveChanges();
			}
		});
		newTuple.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addTuple(query);
				scrollToEnd();
			}
		});
		topButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				scrollToTop();
			}
		});
		bottomButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				scrollToEnd();
			}
		});
	}
	
	public void createNew() {
		controlPanel.removeAll();
		DataModel.clear();
		getTuples(false, true, query);
		validate();
	}
}