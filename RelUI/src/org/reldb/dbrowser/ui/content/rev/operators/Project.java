package org.reldb.dbrowser.ui.content.rev.operators;

import org.eclipse.swt.SWT;
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

	public Project(Rev rev, String name, int xpos, int ypos) {
		super(rev, name, "Project", xpos, ypos);
		addParameter("Operand", "Relation passed to " + getKind()); 
	}
	
	private void addRowAllBut(Composite parent) {
		Label lblNewLabel = new Label(parent, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblNewLabel.setText("ALL BUT");

		Button btnCheckButton = new Button(parent, SWT.CHECK);

		Label dummy = new Label(parent, SWT.NONE);
		dummy.setVisible(false);		
	}
	
	private void addRow(Composite parent, Attribute attribute, int rowNum, boolean last) {
		Label lblNewLabel = new Label(parent, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		lblNewLabel.setText(attribute.getName());
		
		Button btnCheckButton = new Button(parent, SWT.CHECK);
	
		Composite buttonPanel = new Composite(parent, SWT.NONE);
		buttonPanel.setLayout(new FillLayout(SWT.HORIZONTAL));
		Button btnUp = new Button(buttonPanel, SWT.ARROW | SWT.UP | SWT.ARROW_UP);
		Button btnDown = new Button(buttonPanel, SWT.ARROW | SWT.DOWN | SWT.ARROW_DOWN);
		btnUp.setVisible(!(rowNum == 0));
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
		int rowNum = 0;
		Attribute[] attributes = getAttributes();
		for (Attribute attribute: attributes)
			addRow(container, attribute, rowNum++, rowNum == attributes.length);
	}

	@Override
	protected void controlPanelOkPressed() {}
	
	@Override
	protected void controlPanelCancelPressed() {}

	@Override
	public String getQuery() {
		String source = getQueryForParameter(0);
		if (source == null)
			return null;
		return "(" + source + " {ALL BUT})";		
	}

}
