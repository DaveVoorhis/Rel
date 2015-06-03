package org.reldb.dbrowser.ui.content.rev.operators;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.reldb.dbrowser.ui.content.rev.Rev;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;

public class From extends Monadic {
	
	public From(Rev rev, String name, int xpos, int ypos) {
		super(rev, name, "FROM", xpos, ypos);
	}
	
	protected void load() {
		Tuples tuples = getDatabase().getPreservedStateOperator(getID());
		Tuple tuple = tuples.iterator().next();
		if (tuple == null)
			operatorLabel.setText("");
		else {
			String definition = tuple.getAttributeValue("Definition").toString();
			operatorLabel.setText(definition);
		}
	}
	
	private void save() {
		getDatabase().updatePreservedStateOperator(getID(), operatorLabel.getText());
	}
	
	private void addRow(Composite parent, String name) {
		Label lblNewLabel = new Label(parent, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblNewLabel.setText(name);
		
		Button radioButton = new Button(parent, SWT.RADIO);
		radioButton.setSelection(name.equals(operatorLabel.getText()));
		radioButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				operatorLabel.setText(name);
			}
		});
	}
	
	@Override
	protected void notifyArgumentChanged(boolean queryable) {
		super.notifyArgumentChanged(queryable);
		if (queryable && operatorLabel.getText().trim().length() == 0) {
			String[] attributes = getAttributeNamesOfParameter(0).toArray(new String[0]);
			if (attributes.length > 0)
				operatorLabel.setText(attributes[0]);
		}
	}
	
	@Override
	protected void buildControlPanel(Composite container) {
		container.setLayout(new GridLayout(2, false));

		String[] attributes = getAttributeNamesOfParameter(0).toArray(new String[0]);
		if (attributes.length == 0)
			(new Label(container, SWT.None)).setText("No attributes found in operand.");
		for (String attribute: attributes)
			addRow(container, attribute);
	}

	@Override
	protected void controlPanelOkPressed() {
		save();
		pack();
	}

	@Override
	protected void controlPanelCancelPressed() {
		load();
		pack();		
	}
	
	@Override
	public String getQuery() {
		String source = getQueryForParameter(0);
		if (source == null)
			return null;
		if (operatorLabel.getText().length() == 0)
			return null;
		return operatorLabel.getText() + " FROM " + source;		
	}

}
