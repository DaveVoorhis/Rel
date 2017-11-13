package org.reldb.dbrowser.ui.content.rev;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class AttributeListPanel extends Composite {

	private Button checkAllBut;
	private Vector<Label> labelAttributes;
	private Vector<Button> checkAttributes;

	private Vector<String> availableAttributes = new Vector<String>();

	private String text = "{ALL BUT}";

	public AttributeListPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(3, false));
		setup();
	}

	public void setAvailableAttributes(Vector<String> availableAttributes) {
		this.availableAttributes = availableAttributes;
		setup();
	}

	public void setText(String text) {
		this.text = text;
		setup();
	}

	public String getText() {
		String allbut = "";
		if (checkAllBut.getSelection())
			allbut += "ALL BUT";
		String attributeList = "";
		for (int i = 0; i < labelAttributes.size(); i++) {
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

	private Vector<String> getDefinitionAttributes() {
		String definition = text.trim();
		if (definition.length() == 0)
			return null;
		definition = definition.replaceAll("\\{", "").replaceAll("\\}", "");
		Vector<String> output = new Vector<String>();
		if (definition.startsWith("ALL BUT ")) {
			output.add("ALL BUT");
			definition = definition.substring("ALL BUT ".length());
		}
		String[] names = definition.split(",");
		for (String name : names)
			output.add(name.trim());
		return output;
	}

	private void setup() {
		for (Control control : getChildren())
			control.dispose();
		labelAttributes = new Vector<Label>();
		checkAttributes = new Vector<Button>();
		int rowNum = 0;
		Vector<String> definitionAttributes = getDefinitionAttributes();
		if (definitionAttributes == null) {
			addRowAllBut(true);
			for (String attribute : availableAttributes)
				addRow(attribute, rowNum++, rowNum == availableAttributes.size(), false);
		} else {
			Vector<String> panelAttributeList = new Vector<String>();
			for (String name : definitionAttributes)
				if (availableAttributes.contains(name))
					panelAttributeList.add(name);
			for (String attribute : availableAttributes)
				if (!definitionAttributes.contains(attribute))
					panelAttributeList.add(attribute);
			addRowAllBut(definitionAttributes.size() > 0 && definitionAttributes.get(0).equals("ALL BUT"));
			for (String name : panelAttributeList)
				addRow(name, rowNum++, rowNum == panelAttributeList.size(), definitionAttributes.contains(name));
		}
	}

	private void moveAttributeRow(int fromRow, int toRow) {
		String tmpLabelText = labelAttributes.get(toRow).getText();
		labelAttributes.get(toRow).setText(labelAttributes.get(fromRow).getText());
		labelAttributes.get(fromRow).setText(tmpLabelText);

		boolean tmpButtonState = checkAttributes.get(toRow).getSelection();
		checkAttributes.get(toRow).setSelection(checkAttributes.get(fromRow).getSelection());
		checkAttributes.get(fromRow).setSelection(tmpButtonState);
	}

	private void addRowAllBut(boolean selected) {
		Label lblNewLabel = new Label(this, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblNewLabel.setText("ALL BUT");

		checkAllBut = new Button(this, SWT.CHECK);
		checkAllBut.setSelection(selected);

		Label dummy = new Label(this, SWT.NONE);
		dummy.setVisible(false);
	}

	private void addRow(String name, int rowNum, boolean last, boolean selected) {
		Label lblNewLabel = new Label(this, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblNewLabel.setText(name);
		labelAttributes.add(lblNewLabel);

		Button checkBox = new Button(this, SWT.CHECK);
		checkBox.setSelection(selected);
		checkAttributes.add(checkBox);

		Composite buttonPanel = new Composite(this, SWT.NONE);
		buttonPanel.setLayout(new FillLayout(SWT.HORIZONTAL));
		Button btnUp = new Button(buttonPanel, SWT.ARROW | SWT.UP | SWT.ARROW_UP);
		btnUp.addListener(SWT.Selection, e -> moveAttributeRow(rowNum, rowNum - 1));
		btnUp.setVisible(!(rowNum == 0));
		Button btnDown = new Button(buttonPanel, SWT.ARROW | SWT.DOWN | SWT.ARROW_DOWN);
		btnDown.addListener(SWT.Selection, e -> moveAttributeRow(rowNum, rowNum + 1));
		btnDown.setVisible(!last);
	}

}
