package org.reldb.dbrowser.ui.content.rev.operators;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.reldb.dbrowser.ui.content.rev.Rev;
import org.reldb.rel.client.Attribute;
import org.reldb.rel.client.Heading;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;

public abstract class UngroupOrUnwrap extends Monadic {
	
	public UngroupOrUnwrap(Rev rev, String name, String opName, int xpos, int ypos) {
		super(rev, name, opName, xpos, ypos);
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
	
	protected Attribute[] getAvailableAttributesForType(String typeName) {
		Heading heading = getHeadingOfParameter(0);
		if (heading == null)
			return null;
		Attribute[] attributes = heading.toArray();
		Vector<Attribute> output = new Vector<Attribute>();
		for (Attribute attribute: attributes) {
			System.out.println("UngroupOrUnwrap: " + attribute.getName() + ": " + attribute.getType().toString());
			if (attribute.getType().toString().startsWith(typeName))
				output.add(attribute);
		}
		return output.toArray(new Attribute[0]);
	}
	
	protected abstract Attribute[] getAvailableAttributes();
	
	@Override
	protected void buildControlPanel(Composite container) {
		container.setLayout(new GridLayout(2, false));

		Attribute[] attributes = getAvailableAttributes();
		for (Attribute attribute: attributes)
			addRow(container, attribute.getName());
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
		return "(" + source + ") " + getTitle() + " " + operatorLabel.getText();		
	}

}
