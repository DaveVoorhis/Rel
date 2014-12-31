package ca.mb.armchair.rel3.dbrowser.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileFilter;

import ca.mb.armchair.rel3.client.string.*;
import ca.mb.armchair.rel3.dbrowser.utilities.ClassPathHack;
import ca.mb.armchair.rel3.dbrowser.utilities.Preferences;

/**
 * A Rel browser.
 *
 * @author  dave
 */
public class Browser extends JFrame {
	private final static long serialVersionUID = 0;
	
    private static String AppIcon = "ca/mb/armchair/rel3/resources/RelIcon1.png";
    
    private static java.net.URL ImageIconResource = null;
    
    private String databasePath;
    
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
    private Browser(String databasePath) {
    	this.databasePath = databasePath;
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

    private JFileChooser createLocalDatabaseChooser(String title, String buttonText) {
    	JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle(title);
        chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory();
            }
            @Override
            public String getDescription() {
                return "Any folder";
            }
        });
        chooser.setDialogType(JFileChooser.SAVE_DIALOG);
        chooser.setApproveButtonText(buttonText);
        ArrayList<JPanel> ldcpanels = new ArrayList<JPanel>();
        for (Component c: chooser.getComponents()) {
            if (c instanceof JPanel) {
            	ldcpanels.add((JPanel)c);
            }
        }
        ldcpanels.get(0).getComponent(0).setVisible(false);
        ldcpanels.get(2).getComponent(0).setVisible(false);
    	return chooser;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents() {
        jMenuBarMain = new JMenuBar();
        FileMenu = new JMenu();
        OptionsMenu = new JMenu();
        HelpMenu = new JMenu();
        jPanelLocation = new JPanel();
        jPanelStatus = new JPanel();
        jLabelLocation = new JLabel();
        jTextFieldLocation = new JTextField();
        jLabelStatus = new JLabel();
        jTabbedPaneContent = new JTabbedPaneWithCloseIcons();
        jButtonPickLocal = new JButton();
        jProgressBarMemory = new JProgressBar();
        
        localDatabaseChooser = createLocalDatabaseChooser("Open Local Database", "Open");
        localDatabaseCreator = createLocalDatabaseChooser("New Local Database", "Accept");
		remoteDatabaseChooser = new DialogRemoteDatabase(this);
        
        FileMenu.setText("Menu");
        jMenuBarMain.add(FileMenu);

        OptionsMenu.setText("Menu");
        jMenuBarMain.add(OptionsMenu);

        OptionsMenu.setText("Menu");
        jMenuBarMain.add(HelpMenu);
        
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Rel - DBrowser");
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
            	handleClosing();
            }
        });

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

        getContentPane().add(jPanelLocation, BorderLayout.NORTH);

        jPanelStatus.setLayout(new BorderLayout());
        
        jLabelStatus.setFont(new Font("Dialog", 0, 10));
        jLabelStatus.setText("Status");
        
        jPanelStatus.add(jLabelStatus, BorderLayout.WEST);
        
        jProgressBarMemory.setFont(new Font("Dialog", 0, 10));
        jProgressBarMemory.setMinimum(0);
        jProgressBarMemory.setMaximum(100);
        jProgressBarMemory.setValue(0);
        jProgressBarMemory.setString("Memory Used");
        jProgressBarMemory.setStringPainted(true);
        
        jPanelStatus.add(jProgressBarMemory, BorderLayout.EAST);
        
        getContentPane().add(jPanelStatus, BorderLayout.SOUTH);

        getContentPane().add(jTabbedPaneContent, BorderLayout.CENTER);
        jTabbedPaneContent.addTabSelectedListener(new TabSelectedListener() {
        	public void tabSelected(Component tabComponent, String tabTitle) {
        		jTextFieldLocation.setText(tabTitle);
        	}
        });

        setIconImage(getAppIcon().getImage());
        
		Timer memoryCheckTimer = new Timer(500, new ActionListener() {
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
        	JOptionPane.showMessageDialog(null, "A copy of Rel is already accessing the database you're trying to open at " + dbURL, 
        			"Unable to open local database", JOptionPane.ERROR_MESSAGE);
    	} else if (msg.contains("Connection refused")) {
        	JOptionPane.showMessageDialog(null, "A Rel server doesn't appear to be running or available at " + dbURL, 
        			"Unable to open remote database", JOptionPane.ERROR_MESSAGE);
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
    		return true;
    	} else {
    		doConnectionResultFailed(result.exception, dbURL);
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
    
    public void createLocalDatabase() {
    	int returnVal = localDatabaseCreator.showSaveDialog(this);
    	if (returnVal == JFileChooser.APPROVE_OPTION) {
	    	File dir = localDatabaseCreator.getSelectedFile();
	        if (!dir.exists()) {
	            JOptionPane.showMessageDialog(null, "No directory was selected.", "Database Not Created", JOptionPane.INFORMATION_MESSAGE);
	        	return;
	        }
	        if (dir.isDirectory() && (new File(dir + File.separator + "Reldb.rel").exists())) {
	        	if (JOptionPane.showConfirmDialog(null, "The selected directory already contains a Rel database.  Open it?", "Database Already Exists", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION)
	        		return;
	        }
			jTextFieldLocation.setText("local:" + localDatabaseCreator.getSelectedFile());
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
    		Class.forName("ca.mb.armchair.rel3.version.Version");
    		noLocalRel = false;
    	} catch (ClassNotFoundException cnfe) {
    		noLocalRel = true;
        }
    	String databaseDir = System.getProperty("user.home");
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
    
    private JMenu FileMenu;
    private JMenu OptionsMenu;
    private JMenu HelpMenu;
    private JLabel jLabelLocation;
    private JLabel jLabelStatus;
    private JMenuBar jMenuBarMain;
    private JPanel jPanelLocation;
    private JPanel jPanelStatus;
    private JTabbedPaneWithCloseIcons jTabbedPaneContent;
    private JTextField jTextFieldLocation;
    private JButton jButtonPickLocal;
    private JFileChooser localDatabaseChooser;
    private JFileChooser localDatabaseCreator;
    private DialogRemoteDatabase remoteDatabaseChooser;
    private JProgressBar jProgressBarMemory;
}
