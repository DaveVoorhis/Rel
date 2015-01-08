package org.reldb.rel.dbrowser.ui;

import javax.swing.*;

import org.reldb.rel.dbrowser.version.Version;

import java.awt.*;
import java.awt.event.*;
import java.net.URL;

/**
 * Splash screen.
 *
 * @author  Dave Voorhis
 */
public class Splash extends JWindow {
	private static final long serialVersionUID = 0;
	
	private static int dismissalTime = 2000;   // 2 seconds to fade
	
    private static URL SplashImageResource = null;
    
    private static Component parent;
    
    // Widgets
    private JLabel jLabelStatus;
    private JProgressBar jProgressBar;
   
    protected String getSplashImageFilename() {
    	return "org/reldb/rel/resources/RelLogo4.png";
    }
    
    protected String getSplashText() {
    	return
	    	"<font face=\"sans-serif\" size=4><b>" + Version.getVersion() + " for&nbsp;<i>Rel</i></b><br>" +
			"<font size=2>" + Version.getCopyright() + "<br>" +
			"All Rights Reserved<br>" +
			"For further information, please see<br>" +
			"<a href=\"http://dbappbuilder.sourceforge.net\">http://dbappbuilder.sourceforge.net</a></font></font>";
    }
    
    private Splash(Component parent) {
    	Splash.parent = parent;
        initComponents();
    }
    
    /** Get ImageIcon of splash screen image. */
    private ImageIcon getAppSplashImage() {
        if (SplashImageResource == null) {
        	ClassLoader loader = this.getClass().getClassLoader();
        	SplashImageResource = loader.getResource(getSplashImageFilename());
        }
        return new ImageIcon(SplashImageResource);
    }

    // widgets
    private void initComponents() {
    	addMouseListener(new MouseAdapter() {
    		public void mouseReleased(MouseEvent evt) {
    			hideSplash();
    		}
    	});
    	
    	setAlwaysOnTop(true);
    	    	
        getContentPane().setLayout(new UserLayout());
        JTextPane jLabelVersion = new JTextPane();
        jLabelVersion.setOpaque(false);
        jLabelVersion.setBounds(10, 105, 364, 70);
        jLabelVersion.setContentType("text/html");
        jLabelVersion.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        jLabelVersion.setText(getSplashText());
        jLabelVersion.setFont(new Font("sans-serif", Font.PLAIN, 10));
    	jLabelVersion.addMouseListener(new MouseAdapter() {
    		public void mouseReleased(MouseEvent evt) {
    			hideSplash();
    		}
    	});
        getContentPane().add(jLabelVersion);
        
        jProgressBar = new JProgressBar();
        jProgressBar.setOpaque(false);
        jProgressBar.setForeground(new Color(150, 210, 150));
        jProgressBar.setBounds(10, 210, 364, 2);
        jProgressBar.setValue(0);
       
        getContentPane().add(jProgressBar);

        jLabelStatus = new JLabel();
        jLabelStatus.setText("Starting...");
        jLabelStatus.setOpaque(false);
        jLabelStatus.setBounds(10, 190, 364, 20);
        getContentPane().add(jLabelStatus);
        
        setProgressVisible(false);
        
        JLabel jLabel1 = new JLabel();
        jLabel1.setIcon(getAppSplashImage());
        getContentPane().add(jLabel1);
        
        pack();
        
        // Centered
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((int)(screenSize.getWidth() - getContentPane().getWidth()) / 2, (int)(screenSize.getHeight() - getContentPane().getHeight()) / 2);
    }
    
    // Set visibility of progress display
    private void setProgressVisible(boolean flag) {
        jProgressBar.setVisible(flag);
        jLabelStatus.setVisible(flag);    	
    }
    
    // Update status display with a string.
    private void setStatusRaw(String s) {
        jLabelStatus.setText(s);
    }
    
    // Reset progress bar length
    private void resetProgressBarRaw(int n) {
        jProgressBar.setMinimum(0);
        jProgressBar.setMaximum(n);
        jProgressBar.setValue(0);
        setProgressVisible(true);
    }
    
    // Set progress bar length
    private void setProgressBarRaw(int p) {
        jProgressBar.setValue(p);
    }
    
    // Get progress bar length
    private int getProgressBarRaw() {
        return jProgressBar.getValue();
    }

    // Set progress bar to completed
    private void completeProgressBarRaw() {
        jProgressBar.setValue(jProgressBar.getMaximum());
    }
    
    // Sole instance of splash screen.
    private static Splash splash = null;
    
    /** Show splash */
    public static void showSplash(JFrame parent) {
        if (splash == null)
            splash = new Splash(parent);
        splash.setProgressVisible(false);
        splash.setVisible(true);
    }
    
    public static void showSplash() {
    	if (splash == null)
    		splash = new Splash(parent);
    	splash.setProgressVisible(false);
    	splash.setVisible(true);
    }
    
    /** Hide splash */
    public static void hideSplash() {
    	if (splash == null)
    		return;
        if (splash != null)
            splash.setVisible(false);
        parent.requestFocusInWindow();
        splash = null;
    }
    
    /** Dismiss the splash.  Set timer.  When timer goes off, window closes. */
    public static void dismissSplash() {
        if (splash == null)
            return;
        Timer visibleSplashTimer = new Timer(dismissalTime, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                hideSplash();
            }
        });
        visibleSplashTimer.setRepeats(false);
        visibleSplashTimer.start();
        Timer visibleProgressTimer = new Timer(1000, new ActionListener() {
        	public void actionPerformed(ActionEvent evt) {
        		if (splash == null)
        			return;
                splash.setProgressVisible(false);        		
        	}
        });
        visibleProgressTimer.setRepeats(false);
        visibleProgressTimer.start();
    }
    
    /** Reset progress bar length. */
    public static void resetProgressBar(int n) {
        if (splash == null)
            return;
        splash.resetProgressBarRaw(n);
    }
    
    /** Set progress bar length. */
    public static void setProgressBar(int p) {
        if (splash == null)
            return;
        splash.setProgressBarRaw(p);
    }
    
    /** Set progress bar length and set status. */
    public static void setProgressBar(int p, String status) {
    	if (splash == null)
    		return;
    	splash.setProgressBarRaw(p);
    	splash.setStatusRaw(status);
    }
    
    /** Increment progress bar length and set status. */
    public static void setProgressBar(String status) {
    	if (splash == null)
    		return;
        splash.setProgressBarRaw(splash.getProgressBarRaw() + 1);
    	splash.setStatusRaw(status);    	
    }
    
    /** Increment progress bar length by one tick. */
    public static void incrementProgressBar() {
        if (splash == null)
            return;
        splash.setProgressBarRaw(splash.getProgressBarRaw() + 1);
    }
    
    /** Show status as completed. */
    public static void completeProgressBar() {
        if (splash == null)
            return;
        splash.completeProgressBarRaw();
    }
        
    /** Print status to splash. */
    public static void setStatus(String s) {
        if (splash != null)
            splash.setStatusRaw(s);
    }
}
