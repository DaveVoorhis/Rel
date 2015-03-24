package org.reldb.rel.dbrowser.ui;

import java.awt.BorderLayout;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.reldb.rel.client.string.StringReceiverClient;
import org.reldb.rel.dbrowser.crash.CrashTrap;
import org.reldb.rel.dbrowser.version.Version;

/** A UI to encapsulate a user's session with a database. */
public class SessionPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private PanelCommandline commandlinePanel = null;

	private StringReceiverClient client;
	private String dbURL;

	private JPanel rev = null;
	private Method revGo = null;
	
	public SessionPanel(StringReceiverClient client, String dbURL) {
		this.client = client;
		this.dbURL = dbURL;
		CrashTrap crashTrap = new CrashTrap(Version.getVersion());
		commandlinePanel = new PanelCommandline(this, crashTrap);
		setLayout(new BorderLayout());
		try {
			Class<?> revClass = Class.forName("org.reldb.rel.rev.Rev");
    		Class<?> parms[] = new Class[] {new String().getClass(), CrashTrap.class};
    		Constructor<?> revCtor = revClass.getConstructor(parms);
			rev = (JPanel)revCtor.newInstance(dbURL, crashTrap);
			revGo = revClass.getMethod("go");
			System.out.println("Rev add-on is available.");
			JTabbedPane tabpane = new JTabbedPane();
			add(tabpane, BorderLayout.CENTER);
			tabpane.addTab("Command Line", commandlinePanel);
			tabpane.addTab("Rev", rev);
		} catch (Throwable e) {
    		System.out.println("Rev add-on is not available.");
    		add(commandlinePanel, BorderLayout.CENTER);			
		}
	}

	public void loadSourceFile(String loadFile) {
		if (commandlinePanel != null)
			commandlinePanel.loadSourceFile(loadFile);
	}
	
	public void go() {
		if (rev != null && revGo != null)
			try {
				revGo.invoke(rev);
			} catch (Throwable t) {
				System.out.println("Rev add-on could not be activated.");
				t.printStackTrace();
			}
		commandlinePanel.go();		
	}
	
	public StringReceiverClient getClient() {
		return client;
	}

	public String getDbURL() {
		return dbURL;
	}
	
	/** Invoked when panel is closed. */
	public void close() {
		try {
			client.close();
		} catch (IOException ioe) {
			if (commandlinePanel != null)
				commandlinePanel.badResponse("Error closing connection: " + ioe.toString());
		}
	}
	
}
