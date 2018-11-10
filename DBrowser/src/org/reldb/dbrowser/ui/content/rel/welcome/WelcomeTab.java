package org.reldb.dbrowser.ui.content.rel.welcome;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;

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
		if (mainPanel != null)
			mainPanel.dispose();
		mainPanel = new Composite(scrollPanel, SWT.NONE);
		scrollPanel.setContent(mainPanel);
		RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
		rowLayout.spacing = 7;
		mainPanel.setLayout(rowLayout);
		setContents(mainPanel);
		mainPanel.pack();
	}
	
	private void setContents(Composite mainPanel) {
		RevDatabase database = new RevDatabase(connection);
		new WelcomeText(mainPanel, "Welcome to the Rel database at " + connection.getDbURL(), 18);
		
		if (database.relvarExists("pub.Overview")) {
			RevDatabase.Overview overview = database.getOverview();
			new WelcomeText(mainPanel, overview.getContent());
			if (!overview.getRevPrompt())
				return;
		}

		new WelcomeText(mainPanel, "_______________________________");
		
		new WelcomeText(mainPanel,
			"Also, welcome to the new Rel interface. This version is an early release, still incomplete but hopefully useful,\n" +
			"and indicative of the kind of functionality that will be available in forthcoming updates."
		);
		
		if (!database.relvarExists("pub.Overview")) {
			new WelcomeText(mainPanel,
				"No overview description has been set for this database. To create one, press the 'Create Overview' button.\n\n" +
				"That will create a variable called pub.Overview, which you can edit.\n\n" +
				"Its 'contents' attribute value will appear at the top of this Introduction tab.\n" +
				"Set its 'revPrompt' attribute to FALSE to stop being prompted to install Rev."
			);
			new WelcomeButton(mainPanel, "Create Overview", (SelectionEvent e) -> {
				if (!database.createOverview())
					MessageDialog.openError(mainPanel.getShell(), "Rel", "Unable to create pub.Overview. Check the Rel system log (under Tools on the main menu) for details.");
				parent.redisplayed();
				refresh();
			});
		}

		new WelcomeText(mainPanel, "_______________________________");
		
		if (database.hasRevExtensions() >= 0) {
			new WelcomeText(mainPanel,
				"The Rev database development extensions are installed.\n\n" +
				"If you'd like to remove the Rev extensions, press the 'Remove Rev' button, below.\n\n" +
				"Please note that removing the Rev extensions will permanently delete everything except variables, views, operators, types and constraints.\n" +
				"Everything else, including preserved settings, will be permanently deleted."
			);
			new WelcomeButton(mainPanel, "Remove Rev", (SelectionEvent e) -> {
				if (!MessageDialog.openConfirm(mainPanel.getShell(), "Rel", "Are you sure?\n\nThis will remove everything except variables, views, operators, types and constraints."))
					return;
				if (!database.removeRevExtensions())
					MessageDialog.openError(mainPanel.getShell(), "Rel", "Unable to remove Rev extensions.  You may have to remove them manually.");
				else {
					parent.handleRevRemoval();
					refresh();
				}
			});
			
			String checkedMessage = "Uncheck this box to stop automatic display of this Introduction tab when this database is opened.";
			String uncheckedMessage = "Check this box to automatically display this Introduction tab the next time this database is opened.";
			String welcomeSettingKey = parent.getClass().getName() + "-showWelcome";
			boolean showWelcome = !database.getSetting(welcomeSettingKey).equals("no");
			String welcomeShowText = showWelcome ? checkedMessage : uncheckedMessage;
			(new WelcomeButton(mainPanel, welcomeShowText, SWT.CHECK, (SelectionEvent e) -> {
				WelcomeButton button = (WelcomeButton)e.getSource();
				database.setSetting(welcomeSettingKey, button.getSelection() ? "yes" : "no");
				button.setText(button.getSelection() ? checkedMessage : uncheckedMessage);
				button.pack();				
			})).setSelection(showWelcome);
			
		} else {
			new WelcomeText(mainPanel,
				"The Rev database development extensions are not installed.\n\n" +
				"If you intend to develop this database, you probably want to install the Rev extensions,\n" + 
				"which are designed to 'rev up' a Rel database with additional capabilities like a visual query editor and saved settings.\n\n" +
				"You can always remove the Rev extensions later.\n\n" + 
				"Press the 'Install Rev' button to install the Rev extensions."
			);
			new WelcomeButton(mainPanel, "Install Rev", (SelectionEvent e) -> {
				if (!database.installRevExtensions())
					MessageDialog.openError(mainPanel.getShell(), "Rel", "Unable to install Rev extensions. Check the Rel system log (under Tools on the main menu) for details.");
				parent.handleRevAddition();
				refresh();
			});
		}
		
		new WelcomeText(mainPanel, "For help getting started with Rel, press the button below.");
		new WelcomeButton(mainPanel, "Get Started!", (SelectionEvent e) -> {				
			org.eclipse.swt.program.Program.launch(Version.getURL() + "/c/index.php/read/getting-started/");
		});
		
		if (Preferences.getPreferenceBoolean(PreferencePageGeneral.DEFAULT_CMD_MODE)) {
			new WelcomeText(mainPanel,
				"If you'd prefer the new Rel interface to appear by default, press the button below."
			);
			new WelcomeButton(mainPanel, "Use New Rel Interface", (SelectionEvent e) -> {
				Preferences.setPreference(PreferencePageGeneral.DEFAULT_CMD_MODE, false);
				refresh();
			});
		} else {
			new WelcomeText(mainPanel,
				"If you've used Rel before and prefer the classic command-line interface to appear by default, press the button below."
			);
			new WelcomeButton(mainPanel, "Use Classic Rel Interface", (SelectionEvent e) -> {
				Preferences.setPreference(PreferencePageGeneral.DEFAULT_CMD_MODE, true);
				parent.switchToCmdMode();
				refresh();
			});	
		}
		
	}
	
	public WelcomeTab(RelPanel parent, DbTreeItem item) {
		super(parent, item);
		this.parent = parent;
		connection = parent.getConnection();
		scrollPanel = new ScrolledComposite(parent.getTabFolder(), SWT.V_SCROLL | SWT.H_SCROLL);
		refresh();
	    setControl(scrollPanel);
	    ready();
	}
	
}
