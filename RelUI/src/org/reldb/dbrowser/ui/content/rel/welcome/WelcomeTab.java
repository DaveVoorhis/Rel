package org.reldb.dbrowser.ui.content.rel.welcome;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.reldb.dbrowser.ui.DbConnection;
import org.reldb.dbrowser.ui.RevDatabase;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.DbTreeTab;
import org.reldb.dbrowser.ui.content.rel.RelPanel;

public class WelcomeTab extends DbTreeTab {
	private Composite mainPanel;
	private DbConnection connection;
	private RelPanel parent;
	
	private void setContents(Composite mainPanel) {
		for (Control control: mainPanel.getChildren())
			control.dispose();
		
		RevDatabase database = new RevDatabase(connection);
		Label lbl = new Label(mainPanel, SWT.WRAP);
		FontData[] fontdata = lbl.getFont().getFontData();
		Font newFont = SWTResourceManager.getFont(fontdata[0].getName(), 18, SWT.BOLD); 
		lbl.setFont(newFont);
		lbl.setText("Welcome to the Rel database at " + connection.getDbURL());
		
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
			removeRev.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (!MessageDialog.openConfirm(mainPanel.getShell(), "Rev", "Are you sure?\n\nThis will remove everything except variables, views, operators, types and constraints."))
						return;
					if (!database.removeRevExtensions())
						MessageDialog.openError(mainPanel.getShell(), "Rev", "Unable to remove Rev extensions.  You may have to remove them manually.");
					else {
						setContents(mainPanel);
						parent.handleRevRemoval();
						return;
					}
				}
			});
			
			String checkedMessage = "Uncheck this box, and this Introduction tab won't be automatically displayed the next time this database is opened.";
			String uncheckedMessage = "Check this box to automatically display this Introduction tab the next time this database is opened.";
			Button welcomeShow = new Button(mainPanel, SWT.CHECK);
			welcomeShow.setSelection(!database.getSetting(parent.getClass().getName() + "-showWelcome").equals("no"));
			welcomeShow.setText(welcomeShow.getSelection() ? checkedMessage : uncheckedMessage);
			welcomeShow.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					database.setSetting(parent.getClass().getName() + "-showWelcome", (welcomeShow.getSelection()) ? "yes" : "no");
					welcomeShow.setText(welcomeShow.getSelection() ? checkedMessage : uncheckedMessage);
					welcomeShow.pack();
				}
			});
		} else {
			lbl.setText(
				"The Rev database development extensions are not installed.\n\n" +
				"If you intend to develop this database, you probably want to install them.\n" + 
				"You can always remove them later. Press the 'Install Rev' button\n" +
				"to install the Rev extensions."
			);
			Button installRev = new Button(mainPanel, SWT.PUSH);
			installRev.setText("Install Rev");
			installRev.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (!database.installRevExtensions())
						MessageDialog.openError(mainPanel.getShell(), "Rev", "Unable to install Rev extensions. Check the Rel system log (under Tools on the main menu) for details.");
					setContents(mainPanel);
					parent.handleRevAddition();
					return;
				}
			});
		}
		
		mainPanel.pack();		
	}
	
	public WelcomeTab(RelPanel parent, DbTreeItem item) {
		super(parent, item);
		this.parent = parent;
		connection = parent.getConnection();
		mainPanel = new Composite(parent.getTabFolder(), SWT.NONE);
		RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
		rowLayout.spacing = 7;
		mainPanel.setLayout(rowLayout);
		setContents(mainPanel);
	    setControl(mainPanel);
	    ready();
	}
	
}
