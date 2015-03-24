package org.reldb.rel.dbrowser.crash;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.JScrollPane;
import javax.swing.JProgressBar;
import javax.swing.ImageIcon;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Insets;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.beans.XMLEncoder;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

public class CrashDialog extends JDialog {

    private static String errorLoggerURL = "http://rel.armchair.mb.ca/errorlog/";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private final JPanel contentPanel = new JPanel();
	
	private JTextField textFieldEmail;
	private JTextArea textAreaWhatHappened;
	private JTree treeDetails;
	
    private JButton okButton;
	private JLabel lblProgress;
	private JProgressBar progressBar;
    private SendWorker sendWorker = null;
	
	private DefaultMutableTreeNode report;
	private Object sendWorkerMutex = new Integer(0);
	
	/**
	 * Launch the dialog.
	 */
	public static void launch(Throwable t, String lastQuery, String serverInitialResponse, String clientVersion) {
		try {
			CrashDialog dialog = new CrashDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setInformation(t, lastQuery, serverInitialResponse, clientVersion);
			dialog.setVisible(true);			
			dialog.textAreaWhatHappened.requestFocus();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private DefaultMutableTreeNode newPair(String key, Object value) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(key);
		node.add(new DefaultMutableTreeNode(value));
		return node;
	}

	private static String getCurrentTimeStamp() {
	    return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z")).format(new Date());
	}	

	private void putStacktraceInTree(DefaultMutableTreeNode root, StackTraceElement[] trace) {
		DefaultMutableTreeNode traceHeading = new DefaultMutableTreeNode("StackTrace");
		for (StackTraceElement element: trace)
			traceHeading.add(new DefaultMutableTreeNode(element.toString()));
		root.add(traceHeading);
	}

	private static class RelServerInfo {
		String prompt;
		String lookFor;
		public RelServerInfo(String prompt, String lookFor) {
			this.prompt = prompt;
			this.lookFor = lookFor;
		}
	};
	
	private static final RelServerInfo[] relServerInfo =  {
		new RelServerInfo("Version", "Rel version"),
		new RelServerInfo("Host", "Rel is running on"),
		new RelServerInfo("Storage", "Persistence provided by")
	};
	
	private void putServerInfoInTree(DefaultMutableTreeNode root, String serverInitialResponse) {
		DefaultMutableTreeNode serverInfo = new DefaultMutableTreeNode("RelServerInfo");
		String[] lines = serverInitialResponse.split("\\r?\\n");
		for (String line: lines)
			for (RelServerInfo info: relServerInfo)
				if (line.startsWith(info.lookFor))
					serverInfo.add(newPair(info.prompt, line.substring(info.lookFor.length() + 1)));
		root.add(serverInfo);
	}
	
	private void putExceptionInTree(DefaultMutableTreeNode root, Throwable t) {
		root.add(newPair("ErrorClass", t.getClass()));
		putStacktraceInTree(root, t.getStackTrace());
		root.add(newPair("Message", t.toString()));
		if (t.getCause() != null) {
			DefaultMutableTreeNode cause = new DefaultMutableTreeNode("Cause");
			root.add(cause);
			putExceptionInTree(cause, t.getCause());
		}
	}
	
	private void setInformation(Throwable t, String lastQuery, String serverInitialResponse, String clientVersion) {
		report = new DefaultMutableTreeNode("Rel Error Report");
		report.add(newPair("Timestamp", getCurrentTimeStamp()));
		report.add(newPair("Query", lastQuery));
		report.add(newPair("Client version", clientVersion));
		report.add(newPair("Java version", System.getProperty("java.version")));
		report.add(newPair("Java vendor", System.getProperty("java.vendor")));
		putServerInfoInTree(report, serverInitialResponse);
		putExceptionInTree(report, t);
		treeDetails.setModel(new DefaultTreeModel(report));		
	}
	
	private void initialiseProgress(String msg, int steps) {
		okButton.setEnabled(false);
		lblProgress.setEnabled(true);
		progressBar.setEnabled(true);
		progressBar.setMaximum(steps);
		updateProgress(msg, 0);
	}
	
	private void updateProgress(String msg, int step) {
		lblProgress.setText(msg);
		progressBar.setValue(step);
	}
	
	private void resetProgress() {
		updateProgress("Progress...", 0);
		lblProgress.setEnabled(false);
		progressBar.setEnabled(false);
		okButton.setEnabled(true);
	}
		
    private static class SendProgress {
    	String msg;
    	int progress;
    	public SendProgress(String msg, int progress) {
    		this.msg = msg;
    		this.progress = progress;
    	}
    }
    
    private static class SendStatus {
    	Exception exception;
    	String response;
    	public SendStatus(Exception e) {
    		this.exception = e;
    		this.response = null;
    	}
    	public SendStatus(String response) {
    		this.exception = null;
    		this.response = response;
    	}
    }
    
    private class SendWorker extends javax.swing.SwingWorker<SendStatus, SendProgress> {
		protected SendStatus doInBackground() throws Exception {
			
			publish(new SendProgress("Preparing to generate message...", 0));
			
			CrashInfo crashInfo = new CrashInfo(textAreaWhatHappened.getText(), textFieldEmail.getText(), report);
			XMLEncoder encoder;

			publish(new SendProgress("Generating message...", 10));

	        ByteArrayOutputStream message = new ByteArrayOutputStream();
			encoder = new XMLEncoder(new BufferedOutputStream(message));
			encoder.writeObject(crashInfo);
			encoder.close();

	        HttpClient client = new DefaultHttpClient();
	        try {
	            HttpPost httppost = new HttpPost(errorLoggerURL);

	            List<NameValuePair> formparams = new ArrayList<NameValuePair>();
	            formparams.add(new BasicNameValuePair("RelErrorReport", message.toString()));
	            
	            HttpEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
	            httppost.setEntity(entity);
	            
	            publish(new SendProgress("Sending message...", 50));
	            HttpResponse response = client.execute(httppost);
	            entity = response.getEntity();
	            
	            publish(new SendProgress("Getting response...", 75));	            
	            BufferedReader is = new BufferedReader(new InputStreamReader(entity.getContent()));
	            String input;
	            String result = "";
	            while ((input = is.readLine()) != null) {
	            	if (input.startsWith("Success") || input.startsWith("ERROR"))
	            		result = input;
	            }
	            is.close();
	            
	            publish(new SendProgress("Done", 100));
	            Thread.sleep(1000);
	            
	            return new SendStatus(result);
	        } catch (Exception e) {
	        	return new SendStatus(e);
	        }
		}

		protected void process(List<SendProgress> pieces) {
		    for (SendProgress state: pieces) {
		    	updateProgress(state.msg, state.progress);
		    }
		}
		
		protected void done() {
			synchronized (sendWorkerMutex) {
				sendWorker = null;
			}
			try {
				SendStatus sendStatus = get();
				if (sendStatus.response.startsWith("Success")) {
					quit();
		    		JOptionPane.showMessageDialog(null, sendStatus.response, "Error Report Sent", JOptionPane.INFORMATION_MESSAGE);
		        } else
		        	if (sendStatus.exception != null)
		        		JOptionPane.showMessageDialog(null, "Unable to send error report: " + sendStatus.exception.toString(), "Error Report Failed", JOptionPane.ERROR_MESSAGE);
		        	else
		        		JOptionPane.showMessageDialog(null, "Unable to send error report: " + sendStatus.response, "Error Report Failed", JOptionPane.ERROR_MESSAGE);
			} catch (Exception e1) {
        		String exceptionName = e1.getClass().getName().toString(); 
        		if (exceptionName.equals("java.util.concurrent.CancellationException"))
        			JOptionPane.showMessageDialog(null, "Send Error Report Cancelled", "Error Report Failed", JOptionPane.ERROR_MESSAGE);
        		else
        			JOptionPane.showMessageDialog(null, "Unable to send error report: " + e1.toString(), "Error Report Failed", JOptionPane.ERROR_MESSAGE);
			}
			resetProgress();
		}	    	
    }
        
	private void doSend() {
		initialiseProgress("Sending...", 100);
		synchronized (sendWorkerMutex) {
			sendWorker = new SendWorker();
		}
		sendWorker.execute();
	}
	
	private void doCancel() {
		synchronized (sendWorkerMutex) {
			if (sendWorker != null)
				sendWorker.cancel(true);
			else
				quit();
		}
	}
	
	private void quit() {
		setVisible(false);
		dispose();
	}
	
	/**
	 * Create the dialog.
	 */
	public CrashDialog() {
		setTitle("Crash Notification");
		setBounds(100, 100, 684, 520);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JPanel panelPrompt = new JPanel();
			contentPanel.add(panelPrompt, BorderLayout.NORTH);
			GridBagLayout gbl_panelPrompt = new GridBagLayout();
			gbl_panelPrompt.columnWidths = new int[]{0, 0, 0};
			gbl_panelPrompt.rowHeights = new int[]{0, 0, 0};
			gbl_panelPrompt.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
			gbl_panelPrompt.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
			panelPrompt.setLayout(gbl_panelPrompt);
			{
				JLabel labelIcon = new JLabel((String) null);
				try {
					labelIcon.setIcon(new ImageIcon(CrashDialog.class.getResource("org/reldb/rel/resources/nuclear-explosion.png")));
				} catch (NullPointerException npe) {
					System.out.println("CrashDialog: Unable to load CrashDialog icon.");
				}
				GridBagConstraints gbc_labelIcon = new GridBagConstraints();
				gbc_labelIcon.insets = new Insets(5, 5, 5, 5);
				gbc_labelIcon.anchor = GridBagConstraints.NORTH;
				gbc_labelIcon.gridx = 0;
				gbc_labelIcon.gridy = 0;
				panelPrompt.add(labelIcon, gbc_labelIcon);
			}
			{
				JTextPane txtpnUnfortunatelySomethingWent = new JTextPane();
				txtpnUnfortunatelySomethingWent.setText("Unfortunately, something went wrong.  We'd like to send the developers a message about it, so they can fix it in a future update.\n\nIf you'd rather not send anything, that's ok.  Press the Cancel button and nothing will be sent.\n\nOtherwise, please answer the following questions as best you can and remove any information that you don't want to send.  Then press the Send button to transmit it to the developers.\n");
				txtpnUnfortunatelySomethingWent.setOpaque(false);
				txtpnUnfortunatelySomethingWent.setEditable(false);
				GridBagConstraints gbc_txtpnUnfortunatelySomethingWent = new GridBagConstraints();
				gbc_txtpnUnfortunatelySomethingWent.weightx = 1.0;
				gbc_txtpnUnfortunatelySomethingWent.fill = GridBagConstraints.HORIZONTAL;
				gbc_txtpnUnfortunatelySomethingWent.gridx = 1;
				gbc_txtpnUnfortunatelySomethingWent.gridy = 0;
				panelPrompt.add(txtpnUnfortunatelySomethingWent, gbc_txtpnUnfortunatelySomethingWent);
			}
		}
		{
			JPanel panelInformation = new JPanel();
			contentPanel.add(panelInformation, BorderLayout.CENTER);
			GridBagLayout gridBagLayout = new GridBagLayout();
			gridBagLayout.columnWidths = new int[]{0, 0};
			gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0};
			gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
			gridBagLayout.rowWeights = new double[]{1.0, 0, 1.0, 0, Double.MIN_VALUE};
			panelInformation.setLayout(gridBagLayout);
			{
				JPanel panelWhatHappened = new JPanel();
				GridBagConstraints gbc_panelWhatHappened = new GridBagConstraints();
				gbc_panelWhatHappened.insets = new Insets(0, 0, 5, 0);
				gbc_panelWhatHappened.fill = GridBagConstraints.BOTH;
				gbc_panelWhatHappened.gridx = 0;
				gbc_panelWhatHappened.gridy = 0;
				panelInformation.add(panelWhatHappened, gbc_panelWhatHappened);
				panelWhatHappened.setLayout(new BorderLayout(0, 0));
				{
					JLabel lblWhatHappened = new JLabel("1. What were you doing when this happened?");
					lblWhatHappened.setFont(new Font("Dialog", Font.PLAIN, 12));
					panelWhatHappened.add(lblWhatHappened, BorderLayout.NORTH);
				}
				{
					textAreaWhatHappened = new JTextArea();
					textAreaWhatHappened.setWrapStyleWord(true);
					textAreaWhatHappened.setLineWrap(true);
					JScrollPane scrollPaneWhatHappened = new JScrollPane(textAreaWhatHappened);
					panelWhatHappened.add(scrollPaneWhatHappened, BorderLayout.CENTER);
				}
			}
			{
				JPanel panelEmail = new JPanel();
				GridBagConstraints gbc_panelEmail = new GridBagConstraints();
				gbc_panelEmail.insets = new Insets(0, 0, 5, 0);
				gbc_panelEmail.fill = GridBagConstraints.HORIZONTAL;
				gbc_panelEmail.gridx = 0;
				gbc_panelEmail.gridy = 1;
				panelInformation.add(panelEmail, gbc_panelEmail);
				panelEmail.setLayout(new GridLayout(2, 1, 0, 0));
				{
					JLabel lblEmail = new JLabel("2. What is your email address? (optional - we'll only use it if we need to ask you further questions)");
					lblEmail.setFont(new Font("Dialog", Font.PLAIN, 12));
					panelEmail.add(lblEmail);
				}
				{
					textFieldEmail = new JTextField();
					panelEmail.add(textFieldEmail);
				}
			}
			{
				JPanel panelDetails = new JPanel();
				GridBagConstraints gbc_panelDetails = new GridBagConstraints();
				gbc_panelDetails.insets = new Insets(0, 0, 5, 0);
				gbc_panelDetails.fill = GridBagConstraints.BOTH;
				gbc_panelDetails.gridx = 0;
				gbc_panelDetails.gridy = 2;
				panelInformation.add(panelDetails, gbc_panelDetails);
				panelDetails.setLayout(new BorderLayout(0, 0));
				{
					JLabel lblDetails = new JLabel("3. Check these further details and delete anything you don't want to send.");
					lblDetails.setFont(new Font("Dialog", Font.PLAIN, 12));
					panelDetails.add(lblDetails, BorderLayout.NORTH);
				}
				{
					treeDetails = new JTree();
					treeDetails.setRootVisible(false);
					treeDetails.setEditable(true);
					JScrollPane scrollPaneDetails = new JScrollPane(treeDetails);
					panelDetails.add(scrollPaneDetails, BorderLayout.CENTER);
				}
			}
			{
				JPanel panelProgress = new JPanel();
				panelProgress.setFocusCycleRoot(true);
				panelProgress.setEnabled(false);
				GridBagConstraints gbc_panelProgress = new GridBagConstraints();
				gbc_panelProgress.fill = GridBagConstraints.BOTH;
				gbc_panelProgress.gridx = 0;
				gbc_panelProgress.gridy = 3;
				panelInformation.add(panelProgress, gbc_panelProgress);
				panelProgress.setLayout(new BorderLayout(0, 0));
				{
					lblProgress = new JLabel("Progress...");
					lblProgress.setEnabled(false);
					lblProgress.setFont(new Font("Dialog", Font.PLAIN, 12));
					panelProgress.add(lblProgress, BorderLayout.NORTH);
				}
				{
					progressBar = new JProgressBar();
					progressBar.setEnabled(false);
					panelProgress.add(progressBar, BorderLayout.CENTER);
				}
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("Send");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						doSend();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						doCancel();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

}
