package org.reldb.dbrowser.ui.content.rev.operators;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.reldb.dbrowser.ui.content.rev.AttributeListPanel;
import org.reldb.dbrowser.ui.content.rev.Rev;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;

public class Project extends Monadic {
	
	private AttributeListPanel attributeListPanel;
	
	public Project(Rev rev, String name, int xpos, int ypos) {
		super(rev, name, "Project", xpos, ypos);
	}
	
	protected void load() {
		Tuples tuples = getDatabase().getPreservedStateOperator(getID());
		Tuple tuple = tuples.iterator().next();
		if (tuple == null)
			operatorLabel.setText("{ALL BUT}");
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
		return source + " " + operatorLabel.getText();		
	}

}
