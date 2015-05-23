package org.reldb.dbrowser.ui.content.rev.operators;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.reldb.dbrowser.ui.content.rev.ControlPanel;
import org.reldb.dbrowser.ui.content.rev.Operator;
import org.reldb.dbrowser.ui.content.rev.Rev;
import org.reldb.dbrowser.ui.content.rev.Visualiser;

public class Project extends Operator {

	private Label projectClause;
	
	public Project(Rev rev, String name, int xpos, int ypos) {
		super(rev.getModel(), name, "Project", xpos, ypos);
		addParameter("Operand", "Relation passed to " + getKind()); 
	}
	
	@Override
	protected Control obtainControlPanel(Visualiser parent) {
		Composite controlPanel = new Composite(parent, SWT.BORDER);
		controlPanel.setLayout(new FillLayout());
		projectClause = new Label(controlPanel, SWT.NONE);
		projectClause.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				if (projectClause.getBounds().contains(e.x, e.y))
					openDetails();
			}
		});
		return controlPanel;
	}
	
	private static class Controls extends ControlPanel {
		public Controls(Visualiser visualiser) {
			super(visualiser);
		}
		
		@Override
		protected void buildContents(Composite container) {				
			container.setLayout(new GridLayout(3, false));
			for (int i=0; i<10; i++)
				addRow(container);
		}
		
		private void addRow(Composite parent) {
			Label lblNewLabel = new Label(parent, SWT.NONE);
			lblNewLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
			lblNewLabel.setText("New Label");
			
			Button btnCheckButton = new Button(parent, SWT.CHECK);
			
			Spinner spinner = new Spinner(parent, SWT.BORDER);
		}			
	}
	
	private void openDetails() {
		if (!queryable)
			return;		
		(new Controls(this)).open();
	}

	private boolean queryable = false;
	
	@Override
    protected void notifyArgumentChanged(boolean queryable) {
		this.queryable = queryable;
	}

	@Override
	public String getQuery() {
		String source = getQueryForParameter(0);
		if (source == null)
			return null;
		return "(" + source + " {ALL BUT})";		
	}

}
