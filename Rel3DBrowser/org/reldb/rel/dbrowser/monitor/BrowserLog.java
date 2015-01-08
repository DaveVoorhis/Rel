package org.reldb.rel.dbrowser.monitor;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.reldb.rel.dbrowser.ui.Browser;
import org.reldb.rel.dbrowser.ui.Splash;
import org.reldb.rel.dbrowser.utilities.Preferences;

/**
 * A Rel browser monitor.
 *
 * @author  dave
 */
public class BrowserLog extends javax.swing.JFrame {
	private final static long serialVersionUID = 0;
	
    private static String AppIcon = "org/reldb/rel/resources/RelIcon1.png";
    
    private static java.net.URL ImageIconResource = null;
    
    private JTextArea outputDisplay;
    private JScrollPane outputScroller;
    private JFileChooser jFileChooserSave = new JFileChooser();
    
    /** Creates new monitor form */
    private BrowserLog() {
    	setName("DBrowserLog");
    	Splash.showSplash(this);
    	Splash.resetProgressBar(4);
    	Splash.setProgressBar("Loading DBrowser...");
        initComponents();
        Splash.dismissSplash();
        Preferences.getInstance().obtainMainWindowPositionAndState(this, 0, 0, 384, 240);
    }
    
    /** Run Browser.  Output will be sent to log(). */
    @SuppressWarnings("resource")
	public int run(String arguments[]) {
    	class Log implements Logger {
			public void log(String s) {
				BrowserLog.this.log(s);
			}        		
    	};
		Interceptor outInterceptor = new Interceptor(System.out, new Log());
		outInterceptor.attachOut();
		Interceptor errInterceptor = new Interceptor(System.err, new Log());
		errInterceptor.attachErr();
    	Browser.main(arguments);
        return 0;
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
        setTitle("Rel - DBrowser Log");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                Preferences.getInstance().preserveMainWindowPositionAndState(BrowserLog.this);
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
				if (jFileChooserSave.showSaveDialog(BrowserLog.this) == JFileChooser.APPROVE_OPTION) {
					if (jFileChooserSave.getSelectedFile().isFile())
						if (JOptionPane.showConfirmDialog(BrowserLog.this, "File "
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
                
        getContentPane().add(toolBar, BorderLayout.NORTH);
        
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
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
    	BrowserLog logwin = new BrowserLog();
        logwin.setVisible(true);
        logwin.run(args);
    }
}
