package org.reldb.dbrowser.ui.content.rev.operators;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.reldb.dbrowser.ui.content.rev.Rev;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;

public class GroupOrWrap extends Monadic {

	private Text as;
	private Button checkAllBut;
	private Vector<Label> labelAttributes;
	private Vector<Button> checkAttributes;

	public GroupOrWrap(Rev rev, String name, String opName, int xpos, int ypos) {
		super(rev, name, opName, xpos, ypos);
	}

	private String getDefaultAttributeName() {
		return getTitle() + "attr";
	}

	protected void load() {
		Tuples tuples = getDatabase().getPreservedStateOperator(getID());
		Tuple tuple = tuples.iterator().next();
		if (tuple == null)
			operatorLabel.setText("{} AS " + getDefaultAttributeName());
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

	private void addRowAllButAndAs(Composite parent, boolean selected, String astext) {
		Label lblNewLabel = new Label(parent, SWT.NONE);
		lblNewLabel.setText("ALL BUT");

		checkAllBut = new Button(parent, SWT.CHECK);
		checkAllBut.setSelection(selected);

		Label dummy = new Label(parent, SWT.NONE);
		dummy.setVisible(false);

		Label asPrompt = new Label(parent, SWT.NONE);
		asPrompt.setAlignment(SWT.RIGHT);
		asPrompt.setText("As:");

		as = new Text(parent, SWT.NONE);
		as.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		as.setText(astext);
	}

	private void addRow(Composite parent, String name, int rowNum, boolean last, boolean selected) {
		Label lblNewLabel = new Label(parent, SWT.NONE);
		lblNewLabel.setText(name);
		labelAttributes.add(lblNewLabel);

		Button checkBox = new Button(parent, SWT.CHECK);
		checkBox.setSelection(selected);
		checkAttributes.add(checkBox);

		Composite buttonPanel = new Composite(parent, SWT.NONE);
		buttonPanel.setLayout(new FillLayout(SWT.HORIZONTAL));
		Button btnUp = new Button(buttonPanel, SWT.ARROW | SWT.UP | SWT.ARROW_UP);
		btnUp.addListener(SWT.Selection, e -> moveAttributeRow(rowNum, rowNum - 1));
		btnUp.setVisible(!(rowNum == 0));
		Button btnDown = new Button(buttonPanel, SWT.ARROW | SWT.DOWN | SWT.ARROW_DOWN);
		btnDown.addListener(SWT.Selection, e -> moveAttributeRow(rowNum, rowNum + 1));
		btnDown.setVisible(!last);

		Label dummy = new Label(parent, SWT.NONE);
		dummy.setVisible(false);

		dummy = new Label(parent, SWT.NONE);
		dummy.setVisible(false);
	}

	private Vector<String> getDefinitionAttributes() {
		String definition = operatorLabel.getText().trim();
		if (definition.length() == 0)
			return null;
		String[] parts = definition.split("\\sAS\\s");
		String attributes = parts[0].trim();
		attributes = attributes.replaceAll("\\{", "").replaceAll("\\}", "");
		Vector<String> output = new Vector<String>();
		if (attributes.startsWith("ALL BUT ")) {
			output.add("ALL BUT");
			attributes = attributes.substring("ALL BUT ".length());
		}
		String[] names = attributes.split(",");
		for (String name : names)
			output.add(name.trim());
		return output;
	}

	private String getDefinitionAs() {
		String definition = operatorLabel.getText().trim();
		if (definition.length() == 0)
			return null;
		String[] parts = definition.split("\\sAS\\s");
		String as = parts[1].trim();
		return as;
	}

	@Override
	protected void buildControlPanel(Composite container) {
		container.setLayout(new GridLayout(5, false));

		labelAttributes = new Vector<Label>();
		checkAttributes = new Vector<Button>();
		int rowNum = 0;
		Vector<String> availableAttributes = getAttributeNamesOfParameter(0);
		Vector<String> definitionAttributes = getDefinitionAttributes();
		String as = getDefinitionAs();
		if (definitionAttributes == null) {
			addRowAllButAndAs(container, true, as);
			for (String attribute : availableAttributes)
				addRow(container, attribute, rowNum++, rowNum == availableAttributes.size(), false);
		} else {
			Vector<String> panelAttributeList = new Vector<String>();
			for (String name : definitionAttributes)
				if (availableAttributes.contains(name))
					panelAttributeList.add(name);
			for (String attribute : availableAttributes)
				if (!definitionAttributes.contains(attribute))
					panelAttributeList.add(attribute);
			addRowAllButAndAs(container,
					definitionAttributes.size() > 0 && definitionAttributes.get(0).equals("ALL BUT"), as);
			for (String name : panelAttributeList)
				addRow(container, name, rowNum++, rowNum == panelAttributeList.size(),
						definitionAttributes.contains(name));
		}
	}

	private String getSpecification() {
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
		if (as.getText().trim().length() == 0)
			as.setText(getDefaultAttributeName());
		return "{" + attributeSpec + "} AS " + as.getText();
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
		return source + " " + getTitle() + " " + operatorLabel.getText();
	}

}
