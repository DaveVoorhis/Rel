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
import org.reldb.dbrowser.ui.content.rev.DatabaseAbstractionLayer;
import org.reldb.dbrowser.ui.content.rev.OperatorWithControlPanel;
import org.reldb.dbrowser.ui.content.rev.Rev;
import org.reldb.rel.client.Attribute;
import org.reldb.rel.client.Heading;
import org.reldb.rel.client.Tuples;

public class Project extends OperatorWithControlPanel {

	Button checkAllBut;
	Vector<Label> labelAttributes;
	Vector<Button> checkAttributes;
	
	public Project(Rev rev, String name, int xpos, int ypos) {
		super(rev, name, "Project", xpos, ypos);
		addParameter("Operand", "Relation passed to " + getKind()); 
		operatorLabel.setText("{ALL BUT}");
	}
	
	private void addRowAllBut(Composite parent) {
		Label lblNewLabel = new Label(parent, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblNewLabel.setText("ALL BUT");

		checkAllBut = new Button(parent, SWT.CHECK);
		checkAllBut.setSelection(true);

		Label dummy = new Label(parent, SWT.NONE);
		dummy.setVisible(false);		
	}
	
	private void moveAttributeRow(int fromRow, int toRow) {
		String tmpLabelText = labelAttributes.get(toRow).getText();
		labelAttributes.get(toRow).setText(labelAttributes.get(fromRow).getText());
		labelAttributes.get(fromRow).setText(tmpLabelText);
		
		boolean tmpButtonState = checkAttributes.get(toRow).getSelection();
		checkAttributes.get(toRow).setSelection(checkAttributes.get(fromRow).getSelection());
		checkAttributes.get(fromRow).setSelection(tmpButtonState);
	}
	
	private void addRow(Composite parent, Attribute attribute, int rowNum, boolean last) {
		Label lblNewLabel = new Label(parent, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblNewLabel.setText(attribute.getName());
		labelAttributes.add(lblNewLabel);
		
		checkAttributes.add(new Button(parent, SWT.CHECK));
	
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
	
	protected Attribute[] getAttributes() {
		String query = getQueryForParameter(0);
		if (query == null)
			return null;
		Tuples tuples = DatabaseAbstractionLayer.evaluate(getModel().getConnection(), query);
		Heading heading = tuples.getHeading();
		return heading.toArray();
	}
	
	@Override
	protected void buildControlPanel(Composite container) {
		container.setLayout(new GridLayout(3, false));
		addRowAllBut(container);
		DatabaseAbstractionLayer.removeRelvar(getModel().getConnection(), getID());
		labelAttributes = new Vector<Label>();
		checkAttributes = new Vector<Button>();
		int rowNum = 0;
		Attribute[] attributes = getAttributes();
		for (Attribute attribute: attributes)
			addRow(container, attribute, rowNum++, rowNum == attributes.length);
	}

	@Override
	protected void controlPanelOkPressed() {
		operatorLabel.setText(getAttributeSpecification());
		pack();
	}
	
	@Override
	protected void controlPanelCancelPressed() {}

	public String getAttributeSpecification() {
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
	public String getQuery() {
		String source = getQueryForParameter(0);
		if (source == null)
			return null;
		if (operatorLabel.getText().length() == 0)
			return null;
		return "(" + source + " " + operatorLabel.getText() + ")";		
	}

}
