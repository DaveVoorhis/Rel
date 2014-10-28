/*
 * ProgressPanel.java
 *
 * Created on October 27, 2002, 5:30 PM
 */

package ca.mb.armchair.Utilities.Widgets;

/**
 * A JPanel extended to handle progress indication.
 *
 *
 * @author  Dave Voorhis
 */
public class ProgressPanel extends javax.swing.JPanel {
    
	private static final long serialVersionUID = 1L;
	
	private javax.swing.JLabel status;
    private javax.swing.JProgressBar progressBar;
    
    /** Creates a new instance of ProgressPanel */
    public ProgressPanel() {
        status = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        setLayout(new java.awt.BorderLayout());
        add(status, java.awt.BorderLayout.WEST);
        add(progressBar, java.awt.BorderLayout.CENTER);
        resetProgressBar(0);
    }
    
    /** Get the status label. */
    public javax.swing.JLabel getStatusLabel() {
        return status;
    }
    
    /** Get the progress bar. */
    public javax.swing.JProgressBar getProgressBar() {
        return progressBar;
    }
    
    /** Update the status. */
    public void setStatus(String s) {
        status.setText(s + " ");
    }
    
    /** Reset progress bar to given length, with bar position at 0 and status cleared.
     * If 'n' is zero, the progress bar will be run in Indeterminate mode. */
    public void resetProgressBar(int n) {
        progressBar.setIndeterminate(n==0);
        progressBar.setMinimum(0);
        progressBar.setMaximum(n);
        progressBar.setValue(0);
        setStatus("");
    }
    
    /** Update progress bar to given position. */
    public void setProgressBar(int n) {
        progressBar.setValue(n);
    }
    
    /** Move progress bar to next position. */
    public void updateProgressBar() {
        progressBar.setValue(progressBar.getValue() + 1);
    }
    
    /** Update status and set progress bar to given position. */
    public void setProgress(String status, int n) {
        setProgressBar(n);
        setStatus(status);
    }
    
    /** Update status and move progress bar to next position. */
    public void updateProgress(String status) {
        updateProgressBar();
        setStatus(status);
    }
}
