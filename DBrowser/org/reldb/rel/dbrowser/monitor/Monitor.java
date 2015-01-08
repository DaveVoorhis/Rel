package org.reldb.rel.dbrowser.monitor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.reldb.rel.dbrowser.ui.Splash;
import org.reldb.rel.dbrowser.utilities.Preferences;

/**
 * A Rel browser monitor.
 *
 * @author  dave
 */
public class Monitor extends javax.swing.JFrame {
	private final static long serialVersionUID = 0;
	
    private static String AppIcon = "org/reldb/rel/resources/RelIcon1.png";
    
    private static java.net.URL ImageIconResource = null;
    
    private JTextArea outputDisplay;
    private JScrollPane outputScroller;
    private JTextField javaCommandBox = new JTextField();
    private JButton btnRunDBrowser = new JButton("Run DBrowser");
    private JButton btnKillDBrowser = new JButton("Kill DBrowser");
    private JCheckBox checkCloseOnDBrowserExit = new JCheckBox();
    private Process procDBrowser; 
    private JFileChooser jFileChooserSave = new JFileChooser();
    
    /** Creates new monitor form */
    private Monitor() {
    	setName("DBrowserMonitor");
    	Splash.showSplash(this);
    	Splash.resetProgressBar(4);
    	Splash.setProgressBar("Loading DBrowser...");
        initComponents();
        Splash.dismissSplash();
        Preferences.getInstance().obtainMainWindowPositionAndState(this, 0, 0, 384, 240);
    }
    
    // Given an input stream, spew it to out.
    private void captureStream(final InputStream s) {
        (new Thread() {
            public void run() {
                BufferedReader reader = new BufferedReader(new InputStreamReader(s));
                while (true) {
                    try {
                    	String s = reader.readLine();
                    	if (s == null)
                    		break;
                    	log(s);
                    	log("\n");
                    } catch (IOException e) {
                        log(e.toString());
                        log("\n");
                        break;
                    }
                }
                setButtons(true, false);
            }
        }).start();
    }
    
    /** Run an external executable.  Output will be sent to log(). */
    public int run(String command, String arguments[]) {
        try {
        	String[] commandAndArguments = command.split("\\s");        	
        	String commandArray[] = new String[commandAndArguments.length + arguments.length];
        	System.arraycopy(commandAndArguments, 0, commandArray, 0, commandAndArguments.length);
        	System.arraycopy(arguments, 0, commandArray, commandAndArguments.length, arguments.length);
        	procDBrowser = Runtime.getRuntime().exec(commandArray);
        	setButtons(false, true);
            captureStream(procDBrowser.getInputStream());
            captureStream(procDBrowser.getErrorStream());
            (new Thread() {
            	public void run() {
            		try {
						if (procDBrowser.waitFor() == 0 && checkCloseOnDBrowserExit.isSelected())
							shutdown();
					} catch (InterruptedException e) {
					}
					setButtons(true, false);
            	}
            }).start();
        } catch (IOException e) {
        	setButtons(true, false);
            log(e.toString());
            log("\n");
        }
        return 0;
    }

    private void setButtons(final boolean runEnabled, final boolean killEnabled) {
		Runnable runner = new Runnable() {
			public void run() {
	            btnRunDBrowser.setEnabled(runEnabled);
	            btnKillDBrowser.setEnabled(killEnabled);
			}
		};
		if (SwingUtilities.isEventDispatchThread())
			runner.run();
		else
			SwingUtilities.invokeLater(runner);    	
    }
    
    private void log(final String text) {
    	outputDisplay.append(text);
    	if (outputDisplay.getText().length() > 100000)
    		outputDisplay.setText("[...]\n" + outputDisplay.getText().substring(10000));
		Runnable runner = new Runnable() {
			public void run() {
				JScrollBar vscroller = outputScroller.getVerticalScrollBar();
				vscroller.setValue(vscroller.getMaximum());
			}
		};
		if (SwingUtilities.isEventDispatchThread())
			runner.run();
		else
			SwingUtilities.invokeLater(runner);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     */
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Rel - DBrowser Monitor");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                if (btnKillDBrowser.isEnabled() && JOptionPane.showConfirmDialog(Monitor.this, "DBrowser is running.  Are you sure you wish to close the monitor?") != JOptionPane.YES_OPTION)
					return;
                shutdown();
            }
        });
        setIconImage(getAppIcon().getImage());
        
		jFileChooserSave.setDialogType(JFileChooser.SAVE_DIALOG);
		FileNameExtensionFilter filterTXT = new FileNameExtensionFilter("Text files", "txt");
		jFileChooserSave.addChoosableFileFilter(filterTXT);
		jFileChooserSave.setDialogTitle("Save Log");
        
        getContentPane().setLayout(new BorderLayout());
        
        outputDisplay = new JTextArea();
        outputDisplay.setEditable(false);
        outputDisplay.setWrapStyleWord(true);
        outputScroller = new JScrollPane();
        outputScroller.setViewportView(outputDisplay);

        getContentPane().add(outputScroller, BorderLayout.CENTER);
        
        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new GridLayout(3, 1));

        JToolBar vmBar = new JToolBar();
        vmBar.setFloatable(false);
        
        JLabel labelCommandBox = new JLabel("Java VM: "); 
        labelCommandBox.setToolTipText("Command to launch Java Virtual Machine.");
        labelCommandBox.setFont(new Font("Dialog", 0, 10));
        vmBar.add(labelCommandBox);        

        javaCommandBox.setToolTipText("Command to launch Java Virtual Machine.");
        javaCommandBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Preferences.getInstance().setJavaCommand(javaCommandBox.getText());
			}
        });
        javaCommandBox.setMinimumSize(new Dimension(120, 20));
        javaCommandBox.setText(Preferences.getInstance().getJavaCommand());
        vmBar.add(javaCommandBox);
        
        actionPanel.add(vmBar);
        
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        JButton btnClearLog = new JButton("Clear");
        btnClearLog.setToolTipText("Clear log.");
        btnClearLog.setFont(new Font("Dialog", 0, 10));
        btnClearLog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				outputDisplay.setText("");
			}
        });
        toolBar.add(btnClearLog);
        
        JButton btnSaveLog = new JButton("Save");
        btnSaveLog.setToolTipText("Save current log to text file.");
        btnSaveLog.setFont(new Font("Dialog", 0, 10));
        btnSaveLog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (jFileChooserSave.showSaveDialog(Monitor.this) == JFileChooser.APPROVE_OPTION) {
					if (jFileChooserSave.getSelectedFile().isFile())
						if (JOptionPane.showConfirmDialog(Monitor.this, "File "
								+ jFileChooserSave.getSelectedFile()
								+ " already exists.  Overwrite it?") != JOptionPane.YES_OPTION)
							return;
					(new javax.swing.SwingWorker<Object, Object>() {
						protected Object doInBackground() throws Exception {
							try {
								BufferedWriter f = new BufferedWriter(new FileWriter(jFileChooserSave.getSelectedFile()));
								f.write(outputDisplay.getText());
								f.close();
							} catch (IOException ioe) {
								log(ioe.toString());
							}
							return null;
						}
					}).execute();
				}				
			}
        });
        toolBar.add(btnSaveLog);        
        
        btnRunDBrowser.setFont(new Font("Dialog", 0, 10));
        btnRunDBrowser.setToolTipText("Start up DBrowser.");
        btnRunDBrowser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				goAgain();
			}
        });
        btnRunDBrowser.setEnabled(false);
        toolBar.add(btnRunDBrowser);
        
        btnKillDBrowser.setFont(new Font("Dialog", 0, 10));
        btnKillDBrowser.setToolTipText("Forcibly terminate DBrowser.");
        btnKillDBrowser.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (JOptionPane.showConfirmDialog(Monitor.this, "Are you sure you wish to terminate DBrowser?") == JOptionPane.YES_OPTION) {
					procDBrowser.destroy();
					log("DBrowser will be forcibly terminated.  (Please avoid doing this, if possible.)\n");
				}	
			}
        });
        btnKillDBrowser.setEnabled(false);
        toolBar.add(btnKillDBrowser);
        
        actionPanel.add(toolBar);
        
        checkCloseOnDBrowserExit.setSelected(true);
        checkCloseOnDBrowserExit.setFont(new Font("Dialog", 0, 10));
        checkCloseOnDBrowserExit.setText("Close monitor when DBrowser exits normally.");
        checkCloseOnDBrowserExit.setToolTipText("Un-check to make the monitor remain active when DBrowser shuts down normally.");
        actionPanel.add(checkCloseOnDBrowserExit);
        
        getContentPane().add(actionPanel, BorderLayout.NORTH);
        
        pack();
    }
    
    private void shutdown() {
        Preferences.getInstance().preserveMainWindowPositionAndState(Monitor.this);
        System.exit(0);    	
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

    private String[] lastArgs;
    
    private void goAgain() {
    	outputDisplay.setText("");
       	run(javaCommandBox.getText() + " -jar DBrowser.jar -nomonitor ", lastArgs);
    }
    
    private void go(String args[]) {
    	lastArgs = args;
        goAgain();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
    	Monitor monitor = new Monitor();
        monitor.setVisible(true);
        monitor.go(args);
    }
}
