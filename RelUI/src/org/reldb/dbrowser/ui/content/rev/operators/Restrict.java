package org.reldb.dbrowser.ui.content.rev.operators;

import org.reldb.dbrowser.ui.content.rev.OperatorWithControlPanel;
import org.reldb.dbrowser.ui.content.rev.Rev;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.reldb.dbrowser.ui.content.rev.DatabaseAbstractionLayer;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;

public class Restrict extends OperatorWithControlPanel {
	
	public Restrict(Rev rev, String name, int xpos, int ypos) {
		super(rev, name, "Restrict", xpos, ypos);
		addParameter("Operand"); 
		load();
		pack();
	}
	
	private void load() {
		Tuples tuples = DatabaseAbstractionLayer.getPreservedStateRestrict(getModel().getConnection(), getID());
		Tuple tuple = tuples.iterator().next();
		if (tuple == null)
			operatorLabel.setText("true");
		else {
			String definition = tuple.getAttributeValue("Definition").toString();
			operatorLabel.setText(definition);
		}
	}
	
	private void save() {
		DatabaseAbstractionLayer.updatePreservedStateRestrict(getModel().getConnection(), getID(), operatorLabel.getText());
	}
	
	@Override
	protected void buildControlPanel(Composite container) {
		container.setLayout(new GridLayout(2, false));
		
		Label label = new Label(container, SWT.None);
		label.setText("Boolean expression:");
		
		Text expression = new Text(container, SWT.None);
		expression.setText(operatorLabel.getText());
		expression.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		expression.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				operatorLabel.setText(expression.getText());
				Restrict.this.pack();
			}
		});
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
		return "(" + source + ") WHERE " + operatorLabel.getText();		
	}
}
