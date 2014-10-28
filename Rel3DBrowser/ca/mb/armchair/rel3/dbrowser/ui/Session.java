package ca.mb.armchair.rel3.dbrowser.ui;

import java.awt.BorderLayout;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import ca.mb.armchair.rel3.client.string.StringReceiverClient;

/** A UI to encapsulate a user's session with a database. */
public class Session extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	private PanelCommandline commandlinePanel = null;

	private StringReceiverClient client;
	private String dbURL;
	
	public Session(StringReceiverClient client, String dbURL) {
		this.client = client;
		this.dbURL = dbURL;
		commandlinePanel = new PanelCommandline(this);
	}
	
	public void go() {
		setLayout(new BorderLayout());
    	try {
    		Class<?> revclass = Class.forName("ca.mb.armchair.rel3.rev.Rev");
    		Class<?> parms[] = new Class[] {new String().getClass()};
    		Constructor<?> ctor = revclass.getConstructor(parms);
    		Object rev = ctor.newInstance(dbURL);
    		Method go = revclass.getMethod("go");
			go.invoke(rev);
			JTabbedPane tabpane = new JTabbedPane();
			add(tabpane, BorderLayout.CENTER);
			tabpane.addTab("Command Line", commandlinePanel);
			tabpane.addTab("Rev", (JPanel)rev);
			System.out.println("Rev add-on is available.");
    	} catch (Throwable t) {
    		add(commandlinePanel, BorderLayout.CENTER);
    		System.out.println("Rev add-on is not available.");
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
