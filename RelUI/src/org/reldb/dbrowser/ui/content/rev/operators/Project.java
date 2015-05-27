package org.reldb.dbrowser.ui.content.rev.operators;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.reldb.dbrowser.ui.content.rev.Rev;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;

public class Project extends Monadic {

	private Button checkAllBut;
	private Vector<Label> labelAttributes;
	private Vector<Button> checkAttributes;
	
	public Project(Rev rev, String name, int xpos, int ypos) {
		super(rev, name, "Project", xpos, ypos);
	}
	
	protected void load() {
		Tuples tuples = getDatabase().getPreservedStateOperator(getID());
		Tuple tuple = tuples.iterator().next();
		if (tuple == null)
			operatorLabel.setText("{ALL BUT}");
		else {
			String definition = tuple.getAttributeValue("Definition").toString();
			operatorLabel.setText(definition);
		}
	}
	
	private void save() {
		getDatabase().updatePreservedStateOperator(getID(), operatorLabel.getText());
	}
	
	private void moveAttributeRow(int fromRow, int toRow) {
		String tmpLabelText = labelAttributes.get(toRow).getText();
		labelAttributes.get(toRow).setText(labelAttributes.get(fromRow).getText());
		labelAttributes.get(fromRow).setText(tmpLabelText);
		
		boolean tmpButtonState = checkAttributes.get(toRow).getSelection();
		checkAttributes.get(toRow).setSelection(checkAttributes.get(fromRow).getSelection());
		checkAttributes.get(fromRow).setSelection(tmpButtonState);
	}
	
	private void addRowAllBut(Composite parent, boolean selected) {
		Label lblNewLabel = new Label(parent, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblNewLabel.setText("ALL BUT");

		checkAllBut = new Button(parent, SWT.CHECK);
		checkAllBut.setSelection(selected);

		Label dummy = new Label(parent, SWT.NONE);
		dummy.setVisible(false);		
	}
	
	private void addRow(Composite parent, String name, int rowNum, boolean last, boolean selected) {
		Label lblNewLabel = new Label(parent, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblNewLabel.setText(name);
		labelAttributes.add(lblNewLabel);
		
		Button checkBox = new Button(parent, SWT.CHECK);
		checkBox.setSelection(selected);
		checkAttributes.add(checkBox);
	
		Composite buttonPanel = new Composite(parent, SWT.NONE);
		buttonPanel.setLayout(new FillLayout(SWT.HORIZONTAL));
		Button btnUp = new Button(buttonPanel, SWT.ARROW | SWT.UP | SWT.ARROW_UP);
		btnUp.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				moveAttributeRow(rowNum, rowNum - 1);
			}
		});
		btnUp.setVisible(!(rowNum == 0));
		Button btnDown = new Button(buttonPanel, SWT.ARROW | SWT.DOWN | SWT.ARROW_DOWN);
		btnDown.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				moveAttributeRow(rowNum, rowNum + 1);
			}
		});
		btnDown.setVisible(!last);
	}
	
	private Vector<String> getDefinitionAttributes() {
		String definition = operatorLabel.getText().trim();
		if (definition.length() == 0)
			return null;
		definition = definition.replaceAll("\\{", "").replaceAll("\\}", "");
		Vector<String> output = new Vector<String>();
		if (definition.startsWith("ALL BUT ")) {
			output.add("ALL BUT");
			definition = definition.substring("ALL BUT ".length());
		}
		String[] names = definition.split(",");
		for (String name: names)
			output.add(name.trim());
		return output;
	}
	
	@Override
	protected void buildControlPanel(Composite container) {
		container.setLayout(new GridLayout(3, false));

		labelAttributes = new Vector<Label>();
		checkAttributes = new Vector<Button>();
		int rowNum = 0;
		Vector<String> availableAttributes = getAttributeNamesOfParameter(0);
		Vector<String> definitionAttributes = getDefinitionAttributes();
		if (definitionAttributes == null) {
			addRowAllBut(container, true);
			for (String attribute: availableAttributes)
				addRow(container, attribute, rowNum++, rowNum == availableAttributes.size(), false);
		} else {
			Vector<String> panelAttributeList = new Vector<String>();
			for (String name: definitionAttributes)
				if (availableAttributes.contains(name))
					panelAttributeList.add(name);
			for (String attribute: availableAttributes)
				if (!definitionAttributes.contains(attribute))
					panelAttributeList.add(attribute);
			addRowAllBut(container, definitionAttributes.size() > 0 && definitionAttributes.get(0).equals("ALL BUT"));
			for (String name: panelAttributeList)
				addRow(container, name, rowNum++, rowNum == panelAttributeList.size(), definitionAttributes.contains(name));
		}
	}

	private String getSpecification() {
		String allbut = "";
		if (checkAllBut.getSelection())
			allbut += "ALL BUT";
		String attributeList = "";
		for (int i=0; i<labelAttributes.size(); i++) {
			if (checkAttributes.get(i).getSelection()) {
				if (attributeList.length() > 0)
					attributeList += ", ";
				attributeList += labelAttributes.get(i).getText();
			}
		}
		String attributeSpec = allbut;
		if (attributeList.length() > 0 && attributeSpec.length() > 0)
			attributeSpec += " ";
		attributeSpec += attributeList;
		return "{" + attributeSpec + "}";
	}

	@Override
	protected void controlPanelOkPressed() {
		operatorLabel.setText(getSpecification());
		save();
		pack();
	}
	
	@Override
	public String getQuery() {
		String source = getQueryForParameter(0);
		if (source == null)
			return null;
		if (operatorLabel.getText().length() == 0)
			return null;
		return "(" + source + ") " + operatorLabel.getText();		
	}

}
