package org.reldb.dbrowser.ui.content.filtersorter;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.reldb.rel.client.Attribute;
import org.reldb.rel.client.ScalarType;
import org.reldb.rel.client.Type;
import org.reldb.rel.v0.values.StringUtils;

public class FilterPanel extends Composite {
	
	private static final String[] queryOperationDisplay = new String[] {"=", "≠", "<", ">", "≤", "≥", "contains", "starts with", "doesn’t contain"};
	private static final String[] queryOperationCode = new String[] {"=", "!=", "<", ">", "<=", ">=", "LIKE", "LIKE", "NOT LIKE"}; 
	
	private Vector<Attribute> attributes;
	private String whereClause = "";
	private Vector<Control[]> controls = new Vector<Control[]>();
	private Vector<String[]> finderSavedState = null;
	
	public FilterPanel(Vector<Attribute> attributes, Composite parent, Vector<String[]> finderSavedState) {
		super(parent, SWT.NONE);
		this.finderSavedState = finderSavedState;
		this.attributes = attributes;
		createFindPanelContent();		
		doResize();
	}

	public void clear() {
		finderSavedState.clear();
		finderSavedState.add(new String[] {"","","",""});
		createFindPanelContent();			
		doResize();		
	}

	public void ok() {
		removeEmptyRows();
		removeTrailingAndOr();
		preserveState();
		buildWhere();		
	}
	
	public void cancel() {
		removeEmptyRows();
		removeTrailingAndOr();
		preserveState();
		whereClause = "";		
	}

	public String getWhereClause() {
		return whereClause;
	}
	
	protected void doResize() {
		getParent().pack();	
	}
	
	private void removeEmptyRows() {
		boolean doRemoveRows = true;
		while (doRemoveRows) {
			doRemoveRows = false;
			for (int row = 0; row < controls.size(); row++) {
				Control[] controlArray = controls.get(row);
				if (((Combo)controlArray[0]).getText().isEmpty() || ((Combo)controlArray[1]).getText().isEmpty()) {
					removeRow(row);
					doRemoveRows = true;
				}
			}
		}
	}
	
	private void removeTrailingAndOr() {
		if (controls.size() == 0)
			return;
		Control[] controlArray = controls.get(controls.size() - 1);
		Combo comboAndOr = (Combo)controlArray[3];
		if (!comboAndOr.getText().trim().isEmpty())
			comboAndOr.setText("");
	}
	
	private void preserveState() {
		if (finderSavedState == null)
			return;
		finderSavedState.clear();
		for (Control[] controlArray: controls) {
			String[] savedText = new String[4];
			savedText[0] = ((Combo)controlArray[0]).getText();
			savedText[1] = ((Combo)controlArray[1]).getText();
			savedText[2] = ((Text)controlArray[2]).getText();
			savedText[3] = ((Combo)controlArray[3]).getText();
			finderSavedState.add(savedText);
		}
	}
	
	private void initialiseRow(int rowNum) {
		Control[] controlArray = controls.get(rowNum);
		if (finderSavedState != null && rowNum < finderSavedState.size()) {
			String[] savedState = finderSavedState.get(rowNum);
			((Combo)controlArray[0]).setText(savedState[0]);
			((Combo)controlArray[1]).setText(savedState[1]);
			((Text)controlArray[2]).setText(savedState[2]);
			((Combo)controlArray[3]).setText(savedState[3]);
		} else
			((Combo)controlArray[1]).setText("=");
	}
	
	private int findRowOf(Control control, int columnNumber) {
		for (int row = 0; row < controls.size(); row++)
			if (controls.get(row)[columnNumber] == control)
				return row;
		return -1;
	}

	private void removeRow(int rowNum) {
		if (rowNum >= controls.size())
			return;
		for (Control control: controls.get(rowNum))
			control.dispose();
		controls.remove(rowNum);
	}
	
	private void addRow(int rowNum) {
		Control[] rowControls = new Control[4];

		Combo newComboColumn;
		newComboColumn = new Combo(this, SWT.READ_ONLY);
		newComboColumn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		Vector<String> attributeNames = new Vector<>();
		for (Attribute attribute: attributes)
			attributeNames.add(attribute.getName());
		newComboColumn.setItems(attributeNames.toArray(new String[0]));
		rowControls[0] = newComboColumn;
		if (rowNum == 0)
			newComboColumn.setFocus();
		
		Combo comboOperation = new Combo(this, SWT.READ_ONLY);
		String[] itemList = queryOperationDisplay;
		comboOperation.setItems(itemList);
		comboOperation.setVisibleItemCount(itemList.length);
		comboOperation.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		rowControls[1] = comboOperation;
		
		Text textValue = new Text(this, SWT.BORDER);
		GridData gd_textValue = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_textValue.widthHint = 500;
		textValue.setLayoutData(gd_textValue);
		rowControls[2] = textValue;
		
		Combo comboBoolean = new Combo(this, SWT.READ_ONLY);
		comboBoolean.setItems(new String[] {"", "AND", "OR"});
		comboBoolean.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		comboBoolean.addListener(SWT.Selection, evt -> {
			int row = findRowOf(comboBoolean, 3);
			if (comboBoolean.getText().trim().length() > 0) {
				if (row >= controls.size() - 1) {
					addRow(row + 1);
					doResize();
				}
			} else {
				if (row < controls.size() - 1) {
					removeRow(row + 1);
					doResize();
				}
			}
		});
		rowControls[3] = comboBoolean;
		
		if (rowNum >= controls.size())
			controls.add(rowControls);
		else
			controls.set(rowNum, rowControls);

		initialiseRow(rowNum);
	}
	
	private void createFindPanelContent() {		
		controls.clear();
		
		Control[] contents = this.getChildren();
		for (Control control: contents)
			control.dispose();
		
		this.setLayout(new GridLayout(4, false));
		
		Label lblColumn = new Label(this, SWT.NONE);
		lblColumn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		lblColumn.setText("Column");
		
		Label lblOperation = new Label(this, SWT.NONE);
		lblOperation.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		lblOperation.setText("Operation");
		
		Label lblValue = new Label(this, SWT.NONE);
		lblValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblValue.setText("Value");
		
		Label lblBoolean = new Label(this, SWT.NONE);
		lblBoolean.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		lblBoolean.setText("More...");
		
		addRow(0);

		if (finderSavedState != null)
			for (int row = 1; row < finderSavedState.size(); row++)
				addRow(row);
	}
	
	private void buildWhere() {
		whereClause = "";
		Vector<String> arguments = new Vector<String>();
		for (Control[] control: controls) {
			String comparison = "";
			int columnIndex = ((Combo)control[0]).getSelectionIndex();			
			if (columnIndex < 0)
				continue;
			if (whereClause.length() > 0)
				comparison += " ";
			Attribute attribute = attributes.get(columnIndex);
			comparison += attribute.getName();
			int operationIndex = ((Combo)control[1]).getSelectionIndex();
			if (operationIndex < 0)
				continue;
			String value = ((Text)control[2]).getText();
			Type type = attribute.getType();
			if (type instanceof ScalarType)
				if (((ScalarType)type).getName().equals("CHARACTER"))
					value = "'" + StringUtils.quote(value) + "'";
			comparison += " " + queryOperationCode[operationIndex] + " " + value;
			if (queryOperationDisplay[operationIndex].contains("contain"))
				arguments.add("%" + value + "%");
			else if (queryOperationDisplay[operationIndex].contains("starts with"))
				arguments.add(value + "%");
			else
				arguments.add(value);
			String booleanOp = ((Combo)control[3]).getText().trim();
			if (booleanOp.length() > 0)
				comparison += " " + booleanOp;
			whereClause += comparison;
		}
	}

}
