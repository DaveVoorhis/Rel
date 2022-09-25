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
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;
import org.reldb.rel.utilities.StringUtils;

public class Expression extends OperatorWithControlPanel {

	public Expression(Rev rev, String name, int xpos, int ypos) {
		super(rev, name, "Expression", xpos, ypos);
		load();
		pack();
		notifyArgumentChanged(true);
	}

	protected void load() {
		Tuples tuples = getDatabase().getPreservedStateOperator(getID());
		Tuple tuple = tuples.iterator().next();
		if (tuple == null)
			operatorLabel.setText("true");
		else {
			String definition = tuple.getAttributeValue("Definition").toString();
			operatorLabel.setText(definition);
		}
	}

	private void save() {
		String quotedDefinition = StringUtils.quote(operatorLabel.getText());
		getDatabase().updatePreservedStateOperator(getID(), quotedDefinition);
	}

	@Override
	protected void buildControlPanel(Composite container) {
		container.setLayout(new GridLayout(2, false));

		Label label = new Label(container, SWT.None);
		label.setText("Expression:");

		Text expression = new Text(container, SWT.None);
		expression.setText(operatorLabel.getText());
		expression.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		expression.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				operatorLabel.setText(expression.getText());
				Expression.this.pack();
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
		return operatorLabel.getText();
	}

	protected void delete() {
		getDatabase().removeOperator(getID());
		super.delete();
	}

}
