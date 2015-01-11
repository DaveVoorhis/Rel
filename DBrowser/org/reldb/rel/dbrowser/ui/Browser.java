package org.reldb.rel.dbrowser.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.reldb.rel.client.string.*;
import org.reldb.rel.dbrowser.ui.monitors.FreeCPUDisplay;
import org.reldb.rel.dbrowser.ui.monitors.FreeMemoryDisplay;
import org.reldb.rel.dbrowser.utilities.ClassPathHack;
import org.reldb.rel.dbrowser.utilities.Preferences;

/**
 * A Rel browser.
 *
 * @author  dave
 */
public class Browser extends JFrame {
	private final static long serialVersionUID = 0;
	
    private static String AppIcon = "org/reldb/rel/resources/RelIcon1.png";
    
    private static java.net.URL ImageIconResource = null;
    
    private String databasePath;
    
    private static boolean noLocalRel = true;
    
    /** Creates new form Browser */
    private Browser(String databasePath) {
    	this.databasePath = databasePath;
    	setName("DBrowserMain");
    	Splash.showSplash(this);
    	Splash.resetProgressBar(4);
    	Splash.setProgressBar("Loading Rel...");
    	Splash.setProgressBar("Initialising...");
        initComponents();
    	Splash.setProgressBar("Setting up menu...");
        setJMenuBar(new MenuBar(this));
    	Splash.setProgressBar("Opening main window...");
        Splash.dismissSplash();
        Preferences.getInstance().obtainMainWindowPositionAndState(this);
    }
    
    private JPanel makeLocationPanel() {
        JPanel jPanelLocation = new JPanel();
        JLabel jLabelLocation = new JLabel();

        jPanelLocation.setLayout(new BorderLayout());

        jLabelLocation.setFont(new Font("Dialog", 0, 10));
        jLabelLocation.setText("Location: ");
        jPanelLocation.add(jLabelLocation, BorderLayout.WEST);

        jButtonPickLocal.setFont(new Font("Dialog", 0, 8));
        jButtonPickLocal.setText("...");
        jButtonPickLocal.setToolTipText("Click to open a local database.");
        jButtonPickLocal.setMargin(new Insets(0, 0, 0, 0));
        jButtonPickLocal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                chooseLocalDatabase();
            }
        });
        jPanelLocation.add(jButtonPickLocal, BorderLayout.EAST);
        
        jTextFieldLocation.setFont(new Font("Dialog", 0, 10));
        jTextFieldLocation.setToolTipText("Enter the domain name of a remote Rel server here, or 'local:' followed by the directory of a local database.");
        jTextFieldLocation.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	setLocationFromTextFieldLocation(false);
            }
        });
        jPanelLocation.add(jTextFieldLocation, BorderLayout.CENTER);
        
        return jPanelLocation;
    }
    
    private JTabbedPaneWithCloseIcons makeContentPanel() {
        jTabbedPaneContent = new JTabbedPaneWithCloseIcons();
        jTabbedPaneContent.addTabSelectedListener(new TabSelectedListener() {
        	public void tabSelected(Component tabComponent, String tabTitle) {
        		jTextFieldLocation.setForeground(Color.BLACK);
        		jTextFieldLocation.setText(tabTitle);
        	}
        });
    	return jTabbedPaneContent;
    }
    
    private JPanel makeStatusPanel() {    
        FreeMemoryDisplay memoryDisplay = new FreeMemoryDisplay();
        FreeCPUDisplay cpuDisplay = new FreeCPUDisplay();
        
        JPanel jPanelStatus = new JPanel();
        
        jPanelStatus.setLayout(new BorderLayout());
        
        jLabelStatus.setFont(new Font("Dialog", 0, 10));
        jLabelStatus.setText("Status");
        
        jPanelStatus.add(jLabelStatus, BorderLayout.WEST);
        
        JPanel monitors = new JPanel();
        monitors.setLayout(new FlowLayout());
        
        cpuDisplay.setFont(new Font("Dialog", 0, 10));
        cpuDisplay.setOpaque(true);
        cpuDisplay.setBorder(BorderFactory.createEtchedBorder());
        cpuDisplay.setPreferredSize(new Dimension(100, 30));
        monitors.add(cpuDisplay);

        memoryDisplay.setFont(new Font("Dialog", 0, 10));
        memoryDisplay.setOpaque(true);
        memoryDisplay.setBorder(BorderFactory.createEtchedBorder());
        memoryDisplay.setPreferredSize(new Dimension(100, 30));
        monitors.add(memoryDisplay);
        
        jPanelStatus.add(monitors, BorderLayout.EAST);

        return jPanelStatus;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     */
	private void initComponents() {
        jMenuBarMain = new JMenuBar();
        FileMenu = new JMenu();
        OptionsMenu = new JMenu();
        HelpMenu = new JMenu();

        jTextFieldLocation = new JTextField();
        jButtonPickLocal = new JButton();
        jLabelStatus = new JLabel();
        
        localDatabaseChooser = new DirectoryChooser("Open Local Database", "Open");
        localDatabaseCreator = new DirectoryChooser("New Local Database", "Accept");
		remoteDatabaseChooser = new DialogRemoteDatabase(this);
        
        FileMenu.setText("Menu");
        jMenuBarMain.add(FileMenu);

        OptionsMenu.setText("Menu");
        jMenuBarMain.add(OptionsMenu);

        OptionsMenu.setText("Menu");
        jMenuBarMain.add(HelpMenu);
        
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Rel");
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
            	handleClosing();
            }
        });

        getContentPane().add(makeLocationPanel(), BorderLayout.NORTH);
        getContentPane().add(makeContentPanel(), BorderLayout.CENTER);
        getContentPane().add(makeStatusPanel(), BorderLayout.SOUTH);

        setIconImage(getAppIcon().getImage());
        
        pack();
    }
    
    /** Get resource filename of application icon image. */
    private String getAppIconFilename() {
        return AppIcon;
    }
    
    /** Get ImageIcon of application icon image. */
    private ImageIcon getAppIcon() {
        if (ImageIconResource == null) {
        	ClassLoader cl = this.getClass().getClassLoader();
        	ImageIconResource = cl.getResource(getAppIconFilename());
        }
        return new ImageIcon(ImageIconResource);        
    }

    /** Set status display. */
    public void setStatus(String s) {
        jLabelStatus.setText(s);
    }
    
    private String shortened(String s) {
    	if (s.length() < 80)
    		return s;
    	return s.replace(": ",":\n");
    }
    
    private static class AttemptConnectionResult {
    	Throwable exception;
    	StringReceiverClient client;
    	public AttemptConnectionResult(Throwable exception) {
    		this.exception = exception;
    		this.client = null;
    	}
    	public AttemptConnectionResult(StringReceiverClient client) {
    		this.exception = null;
    		this.client = client;
    	}
    }
    
    /** Attempt to open a connection.  Return null if succeeded (!) and exception if failed. */
    private AttemptConnectionResult attemptConnectionOpen(String dbURL, boolean createAllowed) {
        setStatus("Opening connection to " + dbURL);
        try {
        	StringReceiverClient client = ClientFromURL.openConnection(dbURL, createAllowed);
            return new AttemptConnectionResult(client);
        } catch (Throwable exception) {
        	return new AttemptConnectionResult(exception);
        }
    }

    private SessionPanel lastPermanentSessionPanel = null;
    
    private void doConnectionResultSuccess(StringReceiverClient client, String dbURL, boolean permanent) {
    	SessionPanel panel = new SessionPanel(client, dbURL);
        if (permanent) {
        	jTabbedPaneContent.addPermanentTab(dbURL, panel);
        	lastPermanentSessionPanel = panel;
        } else
        	jTabbedPaneContent.addTab(dbURL, panel);
        jTabbedPaneContent.setSelectedComponent(panel);
        panel.requestFocus();
        panel.go();
        setStatus("Ok");    	
    }
    
    private void doConnectionResultFailed(String reason, String dbURL) {
    	String msg = "Unable to establish connection to " + dbURL + " - " + reason;
        setStatus(msg);
        msg = shortened(msg);
        if (msg.contains("The environment cannot be locked for single writer access. ENV_LOCKED")) {
        	JOptionPane.showMessageDialog(null, "A copy of Rel is already accessing the database you're trying to open at " + dbURL, 
        			"Unable to open local database", JOptionPane.ERROR_MESSAGE);
    	} else if (msg.contains("Connection refused")) {
        	JOptionPane.showMessageDialog(null, "A Rel server doesn't appear to be running or available at " + dbURL, 
        			"Unable to open remote database", JOptionPane.ERROR_MESSAGE);
    	} else if (msg.contains("RS0406:")) {
    		JOptionPane.showMessageDialog(null, dbURL + " doesn't contain a Rel database.",
    				"Unable to open local database", JOptionPane.ERROR_MESSAGE);
    	} else if (msg.contains("RS0307:")) {
    		JOptionPane.showMessageDialog(null, dbURL + " doesn't exist.",
    				"Unable to open local database", JOptionPane.ERROR_MESSAGE);    		
    	} else 
        	JOptionPane.showMessageDialog(null, msg, "Unable to open database", JOptionPane.ERROR_MESSAGE);
    }
    
    private void doConnectionResultFailed(Throwable exception, String dbURL) {
    	doConnectionResultFailed(exception.toString(), dbURL);
    }
    
    /** Open a connection and associated panel. */
    private boolean openConnection(String dbURL, boolean permanent, boolean canCreate) {
    	if (noLocalRel && dbURL.startsWith("local:")) {
    		doConnectionResultFailed("Local Rel server is not installed.", dbURL);
    		return false;
    	}
    	AttemptConnectionResult result = attemptConnectionOpen(dbURL, canCreate);
    	if (result.client != null) {
    		doConnectionResultSuccess(result.client, dbURL, permanent);
    		jTextFieldLocation.setForeground(Color.BLACK);
    		return true;
    	} else {
    		doConnectionResultFailed(result.exception, dbURL);
    		jTextFieldLocation.setForeground(Color.RED);
    		return false;
    	}
    }

    public void go() {
        if (databasePath != null) {
        	if (noLocalRel) {
        		String dbURL = "localhost";
        		AttemptConnectionResult result = attemptConnectionOpen(dbURL, false);
            	if (result.client != null) {
            		doConnectionResultSuccess(result.client, dbURL, true);
            		return;
            	}
            } else {
            	if (openConnection("local:" + databasePath, true, true))
            		return;
                if (openConnection("localhost", false, false))
                    return;
            }
        }
        setStatus("Ok.  Please open a database.");        	
    }
    
    private void setLocation(String location, boolean canCreate) {
    	if (location.equalsIgnoreCase("local"))
    		if (noLocalRel) {
    			JOptionPane.showMessageDialog(null, "Local Rel server is not installed.", "Unable to open database", JOptionPane.INFORMATION_MESSAGE);    			
    		} else {
    			if (databasePath == null)
        			JOptionPane.showMessageDialog(null, "Default 'local' database access has been disabled.  Please specify a database as local:<directory>", "Unable to open database", JOptionPane.INFORMATION_MESSAGE);
        		else
        			openConnection("local:" + databasePath, false, canCreate);
    		}
    	else 
    		openConnection(location, false, canCreate);
    }
    
    private void setLocationFromTextFieldLocation(boolean canCreate) {
    	setLocation(jTextFieldLocation.getText().trim(), canCreate);
    }

    public void chooseLocalDatabase() {
		int returnVal = localDatabaseChooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			jTextFieldLocation.setText("local:" + localDatabaseChooser.getSelectedFile());
			setLocationFromTextFieldLocation(false);
		}
    }
    
    public static boolean isRelDatabase(File f) {
    	return (f.isDirectory() && (new File(f + File.separator + "Reldb").exists()));
    }
    
    public void createLocalDatabase() {
    	int returnVal = localDatabaseCreator.showSaveDialog(this);
    	if (returnVal == JFileChooser.APPROVE_OPTION) {
	    	File dir = localDatabaseCreator.getSelectedFile();
	        if (!dir.exists())
	        	dir = dir.getParentFile();
	        if (isRelDatabase(dir)) {
	        	if (JOptionPane.showConfirmDialog(null, dir + " already contains a Rel database.  Open it?", "Database Already Exists", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION)
	        		return;
	        } else {
	        	if (JOptionPane.showConfirmDialog(null, "Create a new database in " + dir + "?", "Create Database", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION)
	        		return;
	        }
			jTextFieldLocation.setText("local:" + dir);
			setLocationFromTextFieldLocation(true);
    	}
    }
    
    public void openRemoteDatabase(String reference) {
		jTextFieldLocation.setText(reference);
		setLocationFromTextFieldLocation(false);   	
    }
    
    public void chooseRemoteDatabase() {
    	remoteDatabaseChooser.setVisible(true);
    }

    private void handleClosing() {
		Preferences.getInstance().preserveMainWindowPositionAndState(this);
    	try {
    		for (int i=0; i<jTabbedPaneContent.getTabCount(); i++)
    			((SessionPanel)jTabbedPaneContent.getComponentAt(i)).close();
    	} catch (Throwable t) {
    		System.out.println("Problem closing tabs: " + t.getMessage());
    	}
    }
    
    private void loadSourceFile(String loadFile) {
    	if (lastPermanentSessionPanel != null)
    		lastPermanentSessionPanel.loadSourceFile(loadFile);
	}
    
    private static Browser browser;
    
    /** Exit the app. */
    public static void exit() {
    	browser.handleClosing();
    	System.exit(0);
    }
        
	private static void usage() {
		System.out.println("Usage: Rel [-nomonitor] [-f[<databaseDir>] | <sourcefile.rel>]");
		System.out.println(" -nomonitor      -- do not open monitor window");
		System.out.println(" -f<databaseDir> -- open specified local database directory");
		System.out.println(" -f              -- do not open a local database");
	}

	public boolean isLocalRelAvailable() {
		return !noLocalRel;
	}

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
		try {
			ClassPathHack.addFile("relclient.jar");
			ClassPathHack.addFile("relshared.jar");
			ClassPathHack.addFile("RelDBMS.jar");
			ClassPathHack.addFile("rev.jar");
		} catch (IOException ioe) {
			System.out.println(ioe.toString());
			System.exit(1);
		}
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			private int failureCount = 0;
			public void uncaughtException(Thread t, Throwable e) {
				if (failureCount > 1) {
					System.err.println("SYSTEM ERROR!  It's gotten even worse.  This is a last-ditch attempt to escape.");
					failureCount++;
					Thread.setDefaultUncaughtExceptionHandler(null);
					System.exit(1);
					return;
				} if (failureCount > 0) {
					System.err.println("SYSTEM ERROR!  Things have gone so horribly wrong that we can't recover or even pop up a message.  I hope someone sees this...\nShutting down now, if we can.");
					failureCount++;
					System.exit(1);
					return;
				}
				failureCount++;
				if (e instanceof OutOfMemoryError) {
					System.err.println("Out of memory!");
					if (browser != null) {
						browser.dispose();
						browser = null;
					}
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, "Out of memory!  Shutting down NOW!", "OUT OF MEMORY", JOptionPane.ERROR_MESSAGE);
				} else {
					System.err.println("Unknown error: " + t);
					if (browser != null) {
						browser.dispose();
						browser = null;
					}
					e.printStackTrace();
					JOptionPane.showMessageDialog(null, e, "Unexpected Error", JOptionPane.ERROR_MESSAGE);
				}
				System.exit(1);						
			}				
		});
    	try {
    		Class.forName("org.reldb.rel.Rel");
    		noLocalRel = false;
    	} catch (ClassNotFoundException cnfe) {
    		noLocalRel = true;
        }
    	String databaseDir = System.getProperty("user.home");
    	String loadFile = null;
    	if (args.length > 1) {
    		System.out.println("More than the expected number of command-line arguments.");
    		usage();
    		System.exit(1);
    	} else if (args.length == 1) {
    		if (!(args[0].startsWith("-f"))) {
    			if (args[0].endsWith(".rel")) {
    				loadFile = args[0];
    			} else {
    				System.out.println("Expected -f command-line argument or a Rel source file, but got " + args[0]);
    				usage();
    				System.exit(1);
    			}
    		}
    		else if (args[0].length() == 2)
    			databaseDir = null;
    		else
    			databaseDir = args[0].substring(2);
    	}
        browser = new Browser(databaseDir);
        browser.setVisible(true);
        browser.go();
        if (loadFile != null)
        	browser.loadSourceFile(loadFile);
        browser.requestFocusInWindow();
    }

	private JMenu FileMenu;
    private JMenu OptionsMenu;
    private JMenu HelpMenu;
    private JLabel jLabelStatus;
    private JMenuBar jMenuBarMain;
    private JTabbedPaneWithCloseIcons jTabbedPaneContent;
    private JTextField jTextFieldLocation;
    private JButton jButtonPickLocal;
    private JFileChooser localDatabaseChooser;
    private JFileChooser localDatabaseCreator;
    private DialogRemoteDatabase remoteDatabaseChooser;
}
