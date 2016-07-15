package org.reldb.dbrowser.ui.content.rel.welcome;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.reldb.dbrowser.ui.RevDatabase;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.DbTreeTab;
import org.reldb.dbrowser.ui.content.rel.RelPanel;

public class WelcomeTab extends DbTreeTab {
	private Composite mainPanel;
	private RevDatabase database;
	
	public WelcomeTab(RelPanel parent, DbTreeItem item) {
		super(parent, item);
		database = new RevDatabase(parent.getConnection());
		mainPanel = new Composite(parent.getTabFolder(), SWT.NONE);
		RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
		rowLayout.spacing = 7;
		mainPanel.setLayout(rowLayout);
		
		Label lbl = new Label(mainPanel, SWT.WRAP);
		FontData[] fontdata = lbl.getFont().getFontData();
		Font newFont = SWTResourceManager.getFont(fontdata[0].getName(), 18, SWT.BOLD); 
		lbl.setFont(newFont);
		lbl.setText("Welcome to the Rel database at " + parent.getConnection().getDbURL());
		
		lbl = new Label(mainPanel, SWT.WRAP);
		if (database.hasRevExtensions() >= 0) {
			lbl.setText(
				"The Rev database development extensions are installed.\n\n" +
				"If you'd like to remove them, press the 'Remove Rev' button, below.\n\n" +
				"Please note that removing the extensions will permanently delete everything from the database\n" +
				"except variables, views, operators, types and constraints. Everything else, including preserved settings,\n" +
				"will be permanently deleted."
			);
			Button removeRev = new Button(mainPanel, SWT.PUSH);
			removeRev.setText("Remove Rev");
			Button welcomeShow = new Button(mainPanel, SWT.CHECK);
			welcomeShow.setText("Check this box to display this Welcome tab next time this database is opened.");
			welcomeShow.setSelection(!database.getSetting(parent.getClass().getName() + "-showWelcome").equals("no"));
			welcomeShow.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					database.setSetting(parent.getClass().getName() + "-showWelcome", (welcomeShow.getSelection()) ? "yes" : "no");
				}
			});
		} else {
			lbl.setText(
				"The Rev database development extensions are not installed.\n\nIf you'd like to develop this database,\n" +
				"you probably want to install them. You can always remove them later. Press the 'Install Rev' button\n" +
				"to install the Rev extensions."
			);
			Button installRev = new Button(mainPanel, SWT.PUSH);
			installRev.setText("Install Rev");
		}
		
		mainPanel.pack();
		
	    setControl(mainPanel);
	    ready();
	}
	
}
