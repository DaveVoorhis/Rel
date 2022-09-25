package org.reldb.dbrowser.ui.content.filtersorter;

import java.util.HashSet;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

public class SorterPanel extends Composite {

	private Vector<Label> labelAttributes;
	private Vector<SortOrderPicker> orderAttributes;

	private Vector<String> availableAttributes = new Vector<String>();

	private String text = "";

	public SorterPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(3, false));
		setup();
	}

	public void setAvailableAttributeNames(Vector<String> availableAttributeNames) {
		this.availableAttributes = availableAttributeNames;
		setup();
	}

	public void setText(String text) {
		this.text = text;
		setup();
	}

	public String getText() {
		String attributeList = "";
		for (int i = 0; i < labelAttributes.size(); i++) {
			String order = orderAttributes.get(i).getState();
			if (order.trim().length() > 0) {
				if (attributeList.length() > 0)
					attributeList += ", ";
				attributeList += order + " " + labelAttributes.get(i).getText();
			}
		}
		return attributeList;
	}

	private static class SortedAttribute {
		private String name;
		private String sort;

		public SortedAttribute(String name, String sort) {
			this.name = name;
			this.sort = sort;
		}

		public SortedAttribute(String name) {
			this(name, "");
		}

		public String getName() {
			return name;
		}

		public String getSort() {
			return sort;
		}
	}

	private Vector<SorterPanel.SortedAttribute> getDefinitionAttributes() {
		String definition = text.trim().replaceAll("\\(", "").replaceAll("\\)", "");
		Vector<SorterPanel.SortedAttribute> output = new Vector<SorterPanel.SortedAttribute>();
		String[] specs = definition.split(",");
		for (String spec : specs) {
			String order[] = spec.trim().split("\\s");
			String name;
			String ordering;
			if (order.length < 2) {
				name = spec.trim();
				ordering = "ASC";
			} else {
				ordering = order[0].trim();
				name = order[1].trim();
			}
			output.add(new SortedAttribute(name, ordering));
		}
		return output;
	}

	private void moveAttributeRow(int fromRow, int toRow) {
		String tmpLabelText = labelAttributes.get(toRow).getText();
		labelAttributes.get(toRow).setText(labelAttributes.get(fromRow).getText());
		labelAttributes.get(fromRow).setText(tmpLabelText);

		String tmpComboState = orderAttributes.get(toRow).getState();
		orderAttributes.get(toRow).setState(orderAttributes.get(fromRow).getState());
		orderAttributes.get(fromRow).setState(tmpComboState);
	}

	private void addRow(SorterPanel.SortedAttribute attribute, int rowNum, boolean last) {
		Label lblNewLabel = new Label(this, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblNewLabel.setText(attribute.getName());
		labelAttributes.add(lblNewLabel);

		SortOrderPicker ordering = new SortOrderPicker(this, SWT.NONE);
		ordering.setState(attribute.getSort());
		orderAttributes.add(ordering);

		Composite buttonPanel = new Composite(this, SWT.NONE);
		buttonPanel.setLayout(new FillLayout(SWT.HORIZONTAL));
		Button btnUp = new Button(buttonPanel, SWT.ARROW | SWT.UP | SWT.ARROW_UP);
		btnUp.addListener(SWT.Selection, e -> moveAttributeRow(rowNum, rowNum - 1));
		btnUp.setVisible(!(rowNum == 0));
		btnUp.setToolTipText("Move up; increase sort priority.");
		
		Button btnDown = new Button(buttonPanel, SWT.ARROW | SWT.DOWN | SWT.ARROW_DOWN);
		btnDown.addListener(SWT.Selection, e -> moveAttributeRow(rowNum, rowNum + 1));
		btnDown.setVisible(!last);
		btnDown.setToolTipText("Move down; decrease sort priority.");
	}
	
	private void setup() {
		for (Control control : getChildren())
			control.dispose();		
		labelAttributes = new Vector<Label>();
		orderAttributes = new Vector<SortOrderPicker>();
		int rowNum = 0;
		Vector<SorterPanel.SortedAttribute> definitionAttributes = getDefinitionAttributes();
		HashSet<String> definitionAttributeNames = new HashSet<String>();
		Vector<SorterPanel.SortedAttribute> panelAttributeList = new Vector<SorterPanel.SortedAttribute>();
		for (SorterPanel.SortedAttribute orderSpec : definitionAttributes) {
			definitionAttributeNames.add(orderSpec.getName());
			if (availableAttributes.contains(orderSpec.getName()))
				panelAttributeList.add(orderSpec);
		}
		for (String attribute : availableAttributes)
			if (!definitionAttributeNames.contains(attribute))
				panelAttributeList.add(new SortedAttribute(attribute));
		for (SorterPanel.SortedAttribute attribute : panelAttributeList)
			addRow(attribute, rowNum++, rowNum == panelAttributeList.size());
	}
}