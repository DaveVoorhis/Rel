package org.reldb.dbrowser.ui.content.rev.operators;

import org.reldb.dbrowser.ui.content.rev.OperatorWithControlPanel;
import org.reldb.dbrowser.ui.content.rev.Rev;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;
import org.reldb.rel.utilities.StringUtils;

public class Comment extends OperatorWithControlPanel {
	
	protected final static Color CommentColor = new Color(Display.getDefault(), 160, 200, 160);

	public Comment(Rev rev, String name, int xpos, int ypos) {
		super(rev, name, "Comment", xpos, ypos);
		lblTitle.setBackground(CommentColor);
		operatorLabel.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_BACKGROUND));
		load();
		notifyArgumentChanged(true);
		btnRun.dispose();
		btnInfo.dispose();
		pack();
	}

	public boolean isQueryable() {
		return false;
	}

	public boolean isDropCandidate() {
		return false;
	}
	
	protected void load() {
		Tuples tuples = getDatabase().getPreservedStateOperator(getID());
		Tuple tuple = tuples.iterator().next();
		if (tuple == null)
			operatorLabel.setText("");
		else {
			String definition = StringUtils.unquote(tuple.getAttributeValue("Definition").toString());
			operatorLabel.setText(definition);
		}
	}

	private void save() {
		String quotedDefinition = StringUtils.quote(operatorLabel.getText());
		getDatabase().updatePreservedStateOperator(getID(), quotedDefinition);
	}

	@Override
	protected void buildControlPanel(Composite container) {
		container.setLayout(new GridLayout(1, false));

		Label label = new Label(container, SWT.None);
		label.setText("Comment:");

		StyledText expression = new StyledText(container, SWT.MULTI);
		expression.setText(operatorLabel.getText());
		expression.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		expression.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				operatorLabel.setText(expression.getText());
				Comment.this.pack();
				container.getShell().pack();
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

	protected void delete() {
		getDatabase().removeOperator(getID());
		super.delete();
	}

}
