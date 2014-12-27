package ca.mb.armchair.rel3.dbrowser.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import ca.mb.armchair.rel3.client.string.*;
import ca.mb.armchair.rel3.dbrowser.utilities.ClassPathHack;
import ca.mb.armchair.rel3.dbrowser.utilities.Preferences;

/**
 * A Rel browser.
 *
 * @author  dave
 */
public class Browser extends javax.swing.JFrame {
	private final static long serialVersionUID = 0;
	
    private static String AppIcon = "ca/mb/armchair/rel3/resources/RelIcon1.png";
    
    private static java.net.URL ImageIconResource = null;
    
    private String databaseDir;
    
    private static boolean noLocalRel = true;
    
    /** Get memory free as a percentage value between 0 and 100. */
    private int getMemoryPercentageFree() {
    	java.lang.Runtime runtime = java.lang.Runtime.getRuntime();
    	long maxmemory = runtime.totalMemory();
    	int memfree = (int)((double)runtime.freeMemory() / (double)maxmemory * 100.0);
    	if (memfree < 10) {
    		runtime.gc();
        	return (int)((double)runtime.freeMemory() / (double)maxmemory * 100.0);
    	} else
    		return memfree;
    }
    
    /** Creates new form Browser */
    private Browser(String databaseDir) {
    	this.databaseDir = databaseDir;
    	setName("DBrowserMain");
    	Splash.showSplash(this);
    	Splash.resetProgressBar(4);
    	Splash.setProgressBar("Loading DBrowser...");
    	Splash.setProgressBar("Initialising...");
        initComponents();
    	Splash.setProgressBar("Setting up menu...");
        setJMenuBar(new MenuBar(this));
    	Splash.setProgressBar("Opening main window...");
        Splash.dismissSplash();
        Preferences.getInstance().obtainMainWindowPositionAndState(this);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents() {
        jMenuBarMain = new javax.swing.JMenuBar();
        FileMenu = new javax.swing.JMenu();
        OptionsMenu = new javax.swing.JMenu();
        HelpMenu = new javax.swing.JMenu();
        jPanelLocation = new javax.swing.JPanel();
        jPanelStatus = new javax.swing.JPanel();
        jLabelLocation = new javax.swing.JLabel();
        jTextFieldLocation = new javax.swing.JTextField();
        jLabelStatus = new javax.swing.JLabel();
        jTabbedPaneContent = new JTabbedPaneWithCloseIcons();
        jButtonPickLocal = new javax.swing.JButton();
        localDatabaseChooser = new javax.swing.JFileChooser();
        jProgressBarMemory = new javax.swing.JProgressBar();
        
		localDatabaseChooser.setFileSelectionMode(javax.swing.JFileChooser.DIRECTORIES_ONLY);
		localDatabaseChooser.setDialogTitle("Open Local Database");
        remoteDatabaseChooser = new DialogRemoteDatabase(this);
		
        FileMenu.setText("Menu");
        jMenuBarMain.add(FileMenu);

        OptionsMenu.setText("Menu");
        jMenuBarMain.add(OptionsMenu);

        OptionsMenu.setText("Menu");
        jMenuBarMain.add(HelpMenu);
        
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Rel - DBrowser");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
            	handleClosing();
            }
        });

        jPanelLocation.setLayout(new java.awt.BorderLayout());

        jLabelLocation.setFont(new java.awt.Font("Dialog", 0, 10));
        jLabelLocation.setText("Location: ");
        jPanelLocation.add(jLabelLocation, java.awt.BorderLayout.WEST);

        jButtonPickLocal.setFont(new java.awt.Font("Dialog", 0, 8));
        jButtonPickLocal.setText("...");
        jButtonPickLocal.setToolTipText("Click to open a local database.");
        jButtonPickLocal.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButtonPickLocal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chooseLocalDatabase();
            }
        });
        jPanelLocation.add(jButtonPickLocal, java.awt.BorderLayout.EAST);
        
        jTextFieldLocation.setFont(new java.awt.Font("Dialog", 0, 10));
        jTextFieldLocation.setToolTipText("Enter the domain name of a remote Rel server here, or 'local:' followed by the directory of a local database.");
        jTextFieldLocation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
            	setLocationFromTextFieldLocation();
            }
        });
        jPanelLocation.add(jTextFieldLocation, java.awt.BorderLayout.CENTER);

        getContentPane().add(jPanelLocation, java.awt.BorderLayout.NORTH);

        jPanelStatus.setLayout(new java.awt.BorderLayout());
        
        jLabelStatus.setFont(new java.awt.Font("Dialog", 0, 10));
        jLabelStatus.setText("Status");
        
        jPanelStatus.add(jLabelStatus, java.awt.BorderLayout.WEST);
        
        jProgressBarMemory.setFont(new java.awt.Font("Dialog", 0, 10));
        jProgressBarMemory.setMinimum(0);
        jProgressBarMemory.setMaximum(100);
        jProgressBarMemory.setValue(0);
        jProgressBarMemory.setString("Memory Used");
        jProgressBarMemory.setStringPainted(true);
        
        jPanelStatus.add(jProgressBarMemory, java.awt.BorderLayout.EAST);
        
        getContentPane().add(jPanelStatus, java.awt.BorderLayout.SOUTH);

        getContentPane().add(jTabbedPaneContent, java.awt.BorderLayout.CENTER);
        jTabbedPaneContent.addTabSelectedListener(new TabSelectedListener() {
        	public void tabSelected(java.awt.Component tabComponent, String tabTitle) {
        		jTextFieldLocation.setText(tabTitle);
        	}
        });

        setIconImage(getAppIcon());
        
		javax.swing.Timer memoryCheckTimer = new javax.swing.Timer(500, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				int memoryFree = getMemoryPercentageFree(); 
				jProgressBarMemory.setValue(memoryFree);
				jProgressBarMemory.setString(memoryFree + "% memory free");
		    	if (memoryFree < 10)
		    		jProgressBarMemory.setForeground(new Color(210, 10, 10));
		    	else
		            jProgressBarMemory.setForeground(new Color(150, 210, 150));
			}
		});
		memoryCheckTimer.setRepeats(true);
		memoryCheckTimer.start();
        
        pack();
    }
    
    /** Get resource filename of application icon image. */
    private String getAppIconFilename() {
        return AppIcon;
    }
    
    /** Get ImageIcon of application icon image. */
    private java.awt.Image getAppIcon() {
        if (ImageIconResource == null) {
        	ClassLoader cl = this.getClass().getClassLoader();
        	ImageIconResource = cl.getResource(getAppIconFilename());
        }
        return java.awt.Toolkit.getDefaultToolkit().getImage(ImageIconResource);        
    }

    /** Set status display. */
    public void setStatus(String s) {
        jLabelStatus.setText(s);
    }
    
    private String shortened(String s) {
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
    private AttemptConnectionResult attemptConnection(String dbURL) {
        setStatus("Opening connection to " + dbURL);
        try {
        	StringReceiverClient client = ClientFromURL.openConnection(dbURL);
            return new AttemptConnectionResult(client);
        } catch (Throwable exception) {
        	return new AttemptConnectionResult(exception);
        }
    }

    private void doConnectionResultSuccess(StringReceiverClient client, String dbURL, boolean permanent) {
        Session panel = new Session(client, dbURL);
        if (permanent)
        	jTabbedPaneContent.addPermanentTab(dbURL, panel);
        else
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
        	javax.swing.JOptionPane.showMessageDialog(null, "A copy of Rel is already accessing the database you're trying to open at " + dbURL, 
        			"Unable to open local database", javax.swing.JOptionPane.ERROR_MESSAGE);
    	} else if (msg.contains("Connection refused")) {
        	javax.swing.JOptionPane.showMessageDialog(null, "A Rel server doesn't appear to be running or available at " + dbURL, 
        			"Unable to open remote database", javax.swing.JOptionPane.ERROR_MESSAGE);
    	} else 
        	javax.swing.JOptionPane.showMessageDialog(null, msg, "Unable to open database", javax.swing.JOptionPane.ERROR_MESSAGE);
    }
    
    private void doConnectionResultFailed(Throwable exception, String dbURL) {
    	doConnectionResultFailed(exception.toString(), dbURL);
    }
    
    /** Open a connection and associated panel. */
    private boolean openConnection(String dbURL, boolean permanent) {
    	if (noLocalRel && dbURL.startsWith("local:")) {
    		doConnectionResultFailed("Local Rel server is not installed.", dbURL);
    		return false;
    	}
    	AttemptConnectionResult result = attemptConnection(dbURL);
    	if (result.client != null) {
    		doConnectionResultSuccess(result.client, dbURL, permanent);
    		return true;
    	} else {
    		doConnectionResultFailed(result.exception, dbURL);
    		return false;
    	}
    }

    public void go() {
        if (databaseDir != null) {
        	if (noLocalRel) {
        		String dbURL = "localhost";
        		AttemptConnectionResult result = attemptConnection(dbURL);
            	if (result.client != null) {
            		doConnectionResultSuccess(result.client, dbURL, true);
            		return;
            	}
            } else {
            	if (openConnection("local:" + databaseDir, true))
            		return;
                if (openConnection("localhost", false))
                    return;
            }
        }
        setStatus("Ok.  Please open a database.");        	
    }
    
    private void setLocation(String location) {
    	if (location.equalsIgnoreCase("local"))
    		if (noLocalRel) {
    			javax.swing.JOptionPane.showMessageDialog(null, "Local Rel server is not installed.", "Unable to open database", javax.swing.JOptionPane.INFORMATION_MESSAGE);    			
    		} else {
    			if (databaseDir == null)
        			javax.swing.JOptionPane.showMessageDialog(null, "Default 'local' database access has been disabled.  Please specify a database as local:<directory>", "Unable to open database", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        		else
        			openConnection("local:" + databaseDir, false);
    		}
    	else 
    		openConnection(location, false);
    }
    
    private void setLocationFromTextFieldLocation() {
    	setLocation(jTextFieldLocation.getText().trim());    	
    }

    public void chooseLocalDatabase() {
		int returnVal = localDatabaseChooser.showOpenDialog(this);
		if (returnVal == javax.swing.JFileChooser.APPROVE_OPTION) {
			jTextFieldLocation.setText("local:" + localDatabaseChooser.getSelectedFile());
			setLocationFromTextFieldLocation();
		}    	
    }
    
    public void openRemoteDatabase(String reference) {
		jTextFieldLocation.setText(reference);
		setLocationFromTextFieldLocation();   	
    }
    
    public void chooseRemoteDatabase() {
    	remoteDatabaseChooser.setVisible(true);
    }

    private void handleClosing() {
		Preferences.getInstance().preserveMainWindowPositionAndState(this);
    	try {
    		for (int i=0; i<jTabbedPaneContent.getTabCount(); i++)
    			((Session)jTabbedPaneContent.getComponentAt(i)).close();
    	} catch (Throwable t) {
    		System.out.println("Problem closing tabs: " + t.getMessage());
    	}
    }
    
    private static Browser browser;
    
    /** Exit the app. */
    public static void exit() {
    	browser.handleClosing();
    	System.exit(0);
    }
        
	private static void usage() {
		System.out.println("Usage: DBrowser [-nomonitor] [-f[<databaseDir>]]");
		System.out.println(" -nomonitor      -- do not open monitor window");
		System.out.println(" -f<databaseDir> -- open specified local database directory");
		System.out.println(" -f              -- do not open a local database");
	}
		
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
		try {
			ClassPathHack.addFile("relclient.jar");
			ClassPathHack.addFile("relshared.jar");
			ClassPathHack.addFile("Rel.jar");
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
					javax.swing.JOptionPane.showMessageDialog(null, "Out of memory!  Shutting down NOW!", "OUT OF MEMORY", javax.swing.JOptionPane.ERROR_MESSAGE);
				} else {
					System.err.println("Unknown error: " + t);
					if (browser != null) {
						browser.dispose();
						browser = null;
					}
					e.printStackTrace();
					javax.swing.JOptionPane.showMessageDialog(null, e, "Unexpected Error", javax.swing.JOptionPane.ERROR_MESSAGE);
				}
				System.exit(1);						
			}				
		});
    	try {
    		Class.forName("ca.mb.armchair.rel3.version.Version");
    		noLocalRel = false;
    	} catch (ClassNotFoundException cnfe) {
    		noLocalRel = true;
        }
    	String databaseDir = "./";
    	if (args.length > 1) {
    		System.out.println("More than the expected number of command-line arguments.");
    		usage();
    		System.exit(1);
    	} else if (args.length == 1) {
    		if (!(args[0].startsWith("-f"))) {
    			System.out.println("Expected -f command-line argument, but got " + args[0]);
    			usage();
    			System.exit(1);
    		}
    		if (args[0].length() == 2)
    			databaseDir = null;
    		else
    			databaseDir = args[0].substring(2);
    	}
        browser = new Browser(databaseDir);
        browser.setVisible(true);
        browser.go();
        browser.requestFocusInWindow();
    }
    
    private javax.swing.JMenu FileMenu;
    private javax.swing.JMenu OptionsMenu;
    private javax.swing.JMenu HelpMenu;
    private javax.swing.JLabel jLabelLocation;
    private javax.swing.JLabel jLabelStatus;
    private javax.swing.JMenuBar jMenuBarMain;
    private javax.swing.JPanel jPanelLocation;
    private javax.swing.JPanel jPanelStatus;
    private JTabbedPaneWithCloseIcons jTabbedPaneContent;
    private javax.swing.JTextField jTextFieldLocation;
    private javax.swing.JButton jButtonPickLocal;
    private javax.swing.JFileChooser localDatabaseChooser;
    private DialogRemoteDatabase remoteDatabaseChooser;
    private javax.swing.JProgressBar jProgressBarMemory;
}
