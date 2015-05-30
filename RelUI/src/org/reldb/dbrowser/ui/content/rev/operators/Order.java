package org.reldb.dbrowser.ui.content.rev.operators;

import java.util.HashSet;
import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.reldb.dbrowser.ui.content.rev.Rev;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;

public class Order extends Monadic {
	
	private AttributeListPanel attributeListPanel;
	
	private static class AttributeListPanel extends Composite {
	
		private Vector<Label> labelAttributes;
		private Vector<Combo> orderAttributes;
	
		private Vector<String> availableAttributes = new Vector<String>();
		
		private String text = "()";
		
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
			String attributeList = "";
			for (int i=0; i<labelAttributes.size(); i++) {
				String order = orderAttributes.get(i).getText();
				if (order.trim().length() > 0) {
					if (attributeList.length() > 0)
						attributeList += ", ";
					attributeList += order + " " + labelAttributes.get(i).getText();
				}
			}
			return "(" + attributeList + ")";
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
			public String getName() {return name;}
			public String getSort() {return sort;}
		}
		
		private Vector<SortedAttribute> getDefinitionAttributes() {
			String definition = text.trim();
			if (definition.length() == 0)
				return null;
			definition = definition.replaceAll("\\(", "").replaceAll("\\)", "");
			Vector<SortedAttribute> output = new Vector<SortedAttribute>();
			String[] specs = definition.split(",");
			for (String spec: specs) {
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
			
			String tmpComboState = orderAttributes.get(toRow).getText();
			orderAttributes.get(toRow).setText(orderAttributes.get(fromRow).getText());
			orderAttributes.get(fromRow).setText(tmpComboState);
		}
		
		private void addRow(SortedAttribute attribute, int rowNum, boolean last) {
			Label lblNewLabel = new Label(this, SWT.NONE);
			lblNewLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			lblNewLabel.setText(attribute.getName());
			labelAttributes.add(lblNewLabel);
			
			Combo ordering = new Combo(this, SWT.CHECK);
			ordering.add("");
			ordering.add("ASC");
			ordering.add("DESC");
			ordering.setText(attribute.getSort());
			orderAttributes.add(ordering);
		
			Composite buttonPanel = new Composite(this, SWT.NONE);
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
				
		private void setup() {
			for (Control control: getChildren())
				control.dispose();
			labelAttributes = new Vector<Label>();
			orderAttributes = new Vector<Combo>();
			int rowNum = 0;
			Vector<SortedAttribute> definitionAttributes = getDefinitionAttributes();
			HashSet<String> definitionAttributeNames = new HashSet<String>();
			Vector<SortedAttribute> panelAttributeList = new Vector<SortedAttribute>();
			for (SortedAttribute orderSpec: definitionAttributes) {
				definitionAttributeNames.add(orderSpec.getName());
				if (availableAttributes.contains(orderSpec.getName()))
					panelAttributeList.add(orderSpec);
			}
			for (String attribute: availableAttributes)
				if (!definitionAttributeNames.contains(attribute))
					panelAttributeList.add(new SortedAttribute(attribute));
			for (SortedAttribute attribute: panelAttributeList)
				addRow(attribute, rowNum++, rowNum == panelAttributeList.size());
		}
	}
	
	public Order(Rev rev, String name, int xpos, int ypos) {
		super(rev, name, "ORDER", xpos, ypos);
	}
	
	protected void load() {
		Tuples tuples = getDatabase().getPreservedStateOperator(getID());
		Tuple tuple = tuples.iterator().next();
		if (tuple == null)
			operatorLabel.setText("()");
		else
			operatorLabel.setText(tuple.getAttributeValue("Definition").toString());
	}
	
	private void save() {
		getDatabase().updatePreservedStateOperator(getID(), operatorLabel.getText());
	}
	
	@Override
	protected void buildControlPanel(Composite container) {
		container.setLayout(new FillLayout());
		attributeListPanel = new AttributeListPanel(container, SWT.None);
		attributeListPanel.setText(operatorLabel.getText());
		attributeListPanel.setAvailableAttributes(getAttributeNamesOfParameter(0));
	}

	@Override
	protected void controlPanelOkPressed() {
		operatorLabel.setText(attributeListPanel.getText());
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
		return source + " ORDER " + operatorLabel.getText();		
	}

}
