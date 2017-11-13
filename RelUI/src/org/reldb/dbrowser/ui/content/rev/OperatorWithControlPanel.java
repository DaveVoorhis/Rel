package org.reldb.dbrowser.ui.content.rev;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.reldb.dbrowser.ui.content.rev.ControlPanel;
import org.reldb.dbrowser.ui.content.rev.Operator;
import org.reldb.dbrowser.ui.content.rev.Rev;
import org.reldb.dbrowser.ui.content.rev.Visualiser;

public abstract class OperatorWithControlPanel extends Operator {

	protected Label operatorLabel;

	public OperatorWithControlPanel(Rev rev, String name, String title, int xpos, int ypos) {
		super(rev.getModel(), name, title, xpos, ypos);
	}

	@Override
	protected Control obtainControlPanel(Visualiser parent) {
		Composite controlPanel = new Composite(parent, SWT.NONE);
		controlPanel.setLayout(new FillLayout());
		operatorLabel = new Label(controlPanel, SWT.NONE);
		operatorLabel.setBackground(BackgroundColor);
		if (!getModel().getRev().isReadOnly())
			operatorLabel.addListener(SWT.MouseUp, e -> {
				if (operatorLabel.getBounds().contains(e.x, e.y))
					openDetails();
			});
		return controlPanel;
	}

	protected abstract void buildControlPanel(Composite container);

	protected void controlPanelOkPressed() {
	}

	protected void controlPanelCancelPressed() {
	}

	private class Controls extends ControlPanel {
		public Controls(Visualiser visualiser) {
			super(visualiser);
		}

		@Override
		protected void buildContents(Composite container) {
			container.setLayout(new GridLayout(3, false));
			buildControlPanel(container);
		}

		@Override
		protected void okPressed() {
			controlPanelOkPressed();
			super.okPressed();
		}

		@Override
		protected void cancelPressed() {
			controlPanelCancelPressed();
			super.cancelPressed();
		}
	}

	private void openDetails() {
		if (!queryable)
			return;
		new Controls(this).open();
	}

	private boolean queryable = false;

	@Override
	protected void notifyArgumentChanged(boolean queryable) {
		this.queryable = queryable;
	}

	@Override
	public String getQuery() {
		return null;
	}

}
