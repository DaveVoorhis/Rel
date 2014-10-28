package ca.mb.armchair.rel3.dbrowser.ui;

/**
 * This is the main menubar for the application.
 */
public class MenuBar extends javax.swing.JMenuBar {
	private static final long serialVersionUID = 0;

	private DialogOptions optionsDialog = new DialogOptions();
	private Browser browser;
	
	private void createFileMenu() {
		javax.swing.JMenu menu = new javax.swing.JMenu("File");
		menu.setMnemonic(java.awt.event.KeyEvent.VK_F);
		add(menu);
		{
			menu.add(new MenuItem("Open local database...", javax.swing.KeyStroke.getKeyStroke('L', java.awt.event.InputEvent.ALT_DOWN_MASK)) {
				private static final long serialVersionUID = 1L;
				public void action() {
					browser.chooseLocalDatabase();
				}
			});
			menu.add(new MenuItem("Open remote database...", javax.swing.KeyStroke.getKeyStroke('R', java.awt.event.InputEvent.ALT_DOWN_MASK)) {
				private static final long serialVersionUID = 1L;
				public void action() {
					browser.chooseRemoteDatabase();
				}
			});
			menu.add(new javax.swing.JSeparator());
			menu.add(new MenuItem("Exit") {
				private static final long serialVersionUID = 1L;
				public void action() {
					Browser.exit();			
				}
			});
		}
	}
	
	private void createToolsMenu() {
		javax.swing.JMenu menu = new javax.swing.JMenu("Tools");
		menu.setMnemonic(java.awt.event.KeyEvent.VK_T);
		add(menu);
		{
			menu.add(new MenuItem("Options") {
				private static final long serialVersionUID = 1L;
				public void action() {
					optionsDialog.setVisible(true);					
				}
			});
		}		
	}

	private void createHelpMenu() {
		javax.swing.JMenu menu = new javax.swing.JMenu("Help");
		menu.setMnemonic(java.awt.event.KeyEvent.VK_H);
		add(menu);
		{
			menu.add(new MenuItem("About") {
				private static final long serialVersionUID = 1L;
				public void action() {
					Splash.showSplash();					
				}
			});
		}		
	}

	public MenuBar(Browser browser) {
		this.browser = browser;
		createFileMenu();
		createToolsMenu();
		createHelpMenu();
	}
}