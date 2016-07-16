package org.reldb.dbrowser.ui.content.rel.welcome;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
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
import org.reldb.dbrowser.ui.preferences.PreferencePageGeneral;
import org.reldb.dbrowser.ui.preferences.Preferences;
import org.reldb.dbrowser.ui.version.Version;

public class WelcomeTab extends DbTreeTab {
	private ScrolledComposite scrollPanel;
	private Composite mainPanel;
	private DbConnection connection;
	private RelPanel parent;
	
	private void refresh() {
		mainPanel = new Composite(scrollPanel, SWT.NONE);
		scrollPanel.setContent(mainPanel);
		RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
		rowLayout.spacing = 7;
		mainPanel.setLayout(rowLayout);
		setContents(mainPanel);
		mainPanel.pack();		
	}
	
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
		if (database.relvarExists("pub.Overview")) {
			RevDatabase.Overview overview = database.getOverview();
			lbl.setText(overview.getContent());
			if (!overview.getRevPrompt())
				return;
		}

		lbl = new Label(mainPanel, SWT.WRAP);
		lbl.setText("_______________________________");
		
		lbl = new Label(mainPanel, SWT.WRAP);
		if (database.hasRevExtensions() >= 0) {
			lbl.setText(
				"The Rev database development extensions are installed.\n\n" +
				"If you'd like to remove the Rev extensions, press the 'Remove Rev' button, below.\n\n" +
				"Please note that removing the Rev extensions will permanently delete everything from the database\n" +
				"except variables, views, operators, types and constraints. Everything else, including preserved settings,\n" +
				"will be permanently deleted."
			);
			Button removeRev = new Button(mainPanel, SWT.PUSH);
			removeRev.setText("Remove Rev");
			removeRev.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (!MessageDialog.openConfirm(mainPanel.getShell(), "Rel", "Are you sure?\n\nThis will remove everything except variables, views, operators, types and constraints."))
						return;
					if (!database.removeRevExtensions())
						MessageDialog.openError(mainPanel.getShell(), "Rel", "Unable to remove Rev extensions.  You may have to remove them manually.");
					else {
						parent.handleRevRemoval();
						refresh();
					}
				}
			});
			
			String checkedMessage = "To stop automatically displaying this Introduction tab when this database is opened, uncheck this box.";
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
				"If you intend to develop this database, you probably want to install the Rev extensions.\n" + 
				"You can always remove them later. Press the 'Install Rev' button\n" +
				"to install the Rev extensions."
			);
			Button installRev = new Button(mainPanel, SWT.PUSH);
			installRev.setText("Install Rev");
			installRev.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (!database.installRevExtensions())
						MessageDialog.openError(mainPanel.getShell(), "Rel", "Unable to install Rev extensions. Check the Rel system log (under Tools on the main menu) for details.");
					parent.handleRevAddition();
					refresh();
				}
			});
		}

		lbl = new Label(mainPanel, SWT.WRAP);
		lbl.setText("_______________________________");
		
		if (!database.relvarExists("pub.Overview")) {
			lbl = new Label(mainPanel, SWT.NONE);
			lbl.setText(
				"No overview description has been set for this database.\n" +
				"To create one, press the 'Create Overview' button.\n" +
				"That will create a variable called pub.Overview, which you can edit.\n" +
				"The 'contents' attribute value will appear at the top of this Introduction tab.\n" +
				"Set the 'revPrompt' attribute to FALSE to stop being prompted to install Rev."
			);
			Button installOverview = new Button(mainPanel, SWT.PUSH);
			installOverview.setText("Create Overview");
			installOverview.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (!database.createOverview())
						MessageDialog.openError(mainPanel.getShell(), "Rel", "Unable to create pub.Overview. Check the Rel system log (under Tools on the main menu) for details.");
					parent.redisplayed();
					refresh();
				}
			});				
		}
		
		lbl = new Label(mainPanel, SWT.NONE);
		lbl.setText("For help getting started with Rel, press the button below.");
		Button furtherInformation = new Button(mainPanel, SWT.PUSH);
		furtherInformation.setText("Get Started!");
		furtherInformation.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {				
				org.eclipse.swt.program.Program.launch(Version.getURL() + "/c/index.php/read/getting-started/");
			}
		});
		
		if (Preferences.getPreferenceBoolean(PreferencePageGeneral.DEFAULT_CMD_MODE)) {
			lbl = new Label(mainPanel, SWT.NONE);
			lbl.setText(
				"If you'd prefer the new Rel interface to appear by default, press the button below."
			);
			Button useNewInterface = new Button(mainPanel, SWT.PUSH);
			useNewInterface.setText("Use New Rel Interface");
			useNewInterface.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Preferences.setPreference(PreferencePageGeneral.DEFAULT_CMD_MODE, false);
					refresh();
				}
			});
		} else {
			lbl = new Label(mainPanel, SWT.NONE);
			lbl.setText(
				"If you've used Rel before and prefer the classic command-line interface to appear by default,\n" +
				"press the button below."
			);
			Button useClassic = new Button(mainPanel, SWT.PUSH);
			useClassic.setText("Use Classic Rel Interface");
			useClassic.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					Preferences.setPreference(PreferencePageGeneral.DEFAULT_CMD_MODE, true);
					parent.switchToCmdMode();
					refresh();
				}
			});	
		}
		
	}
	
	public WelcomeTab(RelPanel parent, DbTreeItem item) {
		super(parent, item);
		this.parent = parent;
		connection = parent.getConnection();
		scrollPanel = new ScrolledComposite(parent.getTabFolder(), SWT.H_SCROLL | SWT.V_SCROLL);
		refresh();
	    setControl(scrollPanel);
	    ready();
	}
	
}
