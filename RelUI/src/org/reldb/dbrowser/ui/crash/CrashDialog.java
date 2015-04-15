package org.reldb.dbrowser.ui.crash;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.wb.swt.ResourceManager;

public class CrashDialog extends Dialog {
	
    private static String errorLoggerURL = "http://rel.armchair.mb.ca/errorlog/";

	protected Object result;
	protected Shell shlCrashNotification;
	private Text textWhatHappened;
	private Text textEmailAddress;
	private Tree treeDetails;
	
	private Label lblProgress;
	private ProgressBar progressBar;
	
	private Button btnSend;
	private Button btnCancel;
	
	private SendWorker sendWorker = null;
	private Object sendWorkerMutex = new Integer(0);
	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public CrashDialog(Shell parent, int style, Throwable t, String lastQuery, String serverInitialResponse, String clientVersion) {
		super(parent, style);
		setText("Crash Notification");
		createContents();
		setInformation(t, lastQuery, serverInitialResponse, clientVersion);
		shlCrashNotification.open();
		shlCrashNotification.layout();
		Display display = getParent().getDisplay();
		while (!shlCrashNotification.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/* Not used, but needed to allow WindowBuilder to work. */
	public void open() {
		createContents();
		shlCrashNotification.open();
		shlCrashNotification.layout();
		Display display = getParent().getDisplay();
		while (!shlCrashNotification.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}
	
	/**
	 * Launch the dialog.
	 * @param shell2 
	 */
	public static void launch(Throwable t, String lastQuery, String serverInitialResponse, Shell shell, String clientVersion) {
		try {
			shell.getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					new CrashDialog(shell, SWT.None, t, lastQuery, serverInitialResponse, clientVersion);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String getCurrentTimeStamp() {
	    return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z")).format(new Date());
	}	

	private void setupTreeItem(TreeItem item, String text) {
		item.setText(text);
		item.setChecked(true);
		item.setExpanded(true);
	}
	
	private TreeItem newTreeItem(TreeItem parent, String text) {
		TreeItem item = new TreeItem(parent, SWT.None);
		setupTreeItem(item, text);
		return item;
	}
	
	private TreeItem newTreeItem(Tree parent, String text) {
		TreeItem item = new TreeItem(parent, SWT.None);
		setupTreeItem(item, text);
		return item;
	}
	
	private void putStacktraceInTree(TreeItem root, StackTraceElement[] trace) {
		TreeItem stackTraceTree = newTreeItem(root, "StackTrace");
		for (StackTraceElement element: trace)
			newTreeItem(stackTraceTree, element.toString());
	}

	private static class RelServerInfo {
		String prompt;
		String lookFor;
		public RelServerInfo(String prompt, String lookFor) {
			this.prompt = prompt;
			this.lookFor = lookFor;
		}
	};
	
	private static final RelServerInfo[] relServerInfo = {
		new RelServerInfo("Version", "Rel version"),
		new RelServerInfo("Host", "Rel is running on"),
		new RelServerInfo("Storage", "Persistence provided by")
	};
	
	private void checkPath(TreeItem item, boolean checked, boolean grayed) {
	    if (item == null) 
	    	return;
	    if (grayed) {
	        checked = true;
	    } else {
	        int index = 0;
	        TreeItem[] items = item.getItems();
	        while (index < items.length) {
	            TreeItem child = items[index];
	            if (child.getGrayed() || checked != child.getChecked()) {
	                checked = grayed = true;
	                break;
	            }
	            index++;
	        }
	    }
	    item.setChecked(checked);
	    item.setGrayed(grayed);
	    checkPath(item.getParentItem(), checked, grayed);
	}

	private void checkItems(TreeItem item, boolean checked) {
	    item.setGrayed(false);
	    item.setChecked(checked);
	    TreeItem[] items = item.getItems();
	    for (int i = 0; i < items.length; i++) {
	        checkItems(items[i], checked);
	    }
	}
	
	private void putServerInfoInTree(TreeItem root, String serverInitialResponse) {
		TreeItem serverInfo = newTreeItem(root, "RelServerInfo");
		String[] lines = serverInitialResponse.split("\\r?\\n");
		for (String line: lines)
			for (RelServerInfo info: relServerInfo)
				if (line.startsWith(info.lookFor))
					newTreeItem(serverInfo, info.prompt + ": " + line.substring(info.lookFor.length() + 1));
	}
	
	private void putExceptionInTree(TreeItem root, Throwable t) {
		newTreeItem(root, "ErrorClass: " + t.getClass().toString());
		putStacktraceInTree(root, t.getStackTrace());
		newTreeItem(root, "Message: " + t.toString());
		if (t.getCause() != null) {
			TreeItem cause = newTreeItem(root, "Cause");
			putExceptionInTree(cause, t.getCause());
		}
	}
	
	private void setInformation(Throwable t, String lastQuery, String serverInitialResponse, String clientVersion) {
		TreeItem report = newTreeItem(treeDetails, "Rel Error Report");
		newTreeItem(report, "Timestamp: " + getCurrentTimeStamp().toString());
		newTreeItem(report, "Query: " + lastQuery.toString());
		newTreeItem(report, "Client version: " + clientVersion);
		newTreeItem(report, "Java version: " + System.getProperty("java.version"));
		newTreeItem(report, "Java vendor: " + System.getProperty("java.vendor"));
		putServerInfoInTree(report, serverInitialResponse);
		TreeItem except = newTreeItem(report, "JavaException");
		putExceptionInTree(except, t);
	}
	
	private void initialiseProgress(String msg, int steps) {
		btnSend.setEnabled(false);
		lblProgress.setEnabled(true);
		progressBar.setEnabled(true);
		progressBar.setMaximum(steps);
		updateProgress(msg, 0);
	}
	
	private void updateProgress(String msg, int step) {
		lblProgress.setText(msg);
		progressBar.setSelection(step);
	}
	
	private void resetProgress() {
		updateProgress("Progress...", 0);
		lblProgress.setEnabled(false);
		progressBar.setEnabled(false);
		btnSend.setEnabled(true);
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
    
    private class SendWorker extends Thread {

    	private String report;
    	
    	public SendWorker(String report) {
    		this.report = report;
    	}
    	
    	public void publish(SendProgress progressMessage) {
    		shlCrashNotification.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
			    	updateProgress(progressMessage.msg, progressMessage.progress);  		
				}
    		});
    	}
    	
    	private SendStatus status = null;
    	
    	public void run() {
    		try {
    			status = doInBackground();
    		} catch (Exception e) {
    			status = new SendStatus(e);
    		}
    		shlCrashNotification.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					done(status);
				}
    		});
    	}

		protected SendStatus doInBackground() throws Exception {			
			publish(new SendProgress("Generating message...", 10));

	        HttpClient client = new DefaultHttpClient();
	        try {
	            HttpPost httppost = new HttpPost(errorLoggerURL);

	            List<NameValuePair> formparams = new ArrayList<NameValuePair>();
	            formparams.add(new BasicNameValuePair("RelErrorReport", report));
	            
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
		
		protected void done(SendStatus sendStatus) {
			synchronized (sendWorkerMutex) {
				sendWorker = null;
			}
			try {
				if (sendStatus.response != null && sendStatus.response.startsWith("Success")) {
					Shell parent = getParent();
					quit();
		    		MessageDialog.openInformation(parent, "Error Report Sent", sendStatus.response);
		    		return;
		        } else
		        	if (sendStatus.exception != null) {
	        			sendStatus.exception.printStackTrace();
		        		MessageDialog.openError(getParent(), "Error Report Failed", "Unable to send error report: " + sendStatus.exception.toString());
		        	} else
		        		MessageDialog.openError(getParent(), "Error Report Failed", "Unable to send error report: " + sendStatus.response);
			} catch (Exception e1) {
        		String exceptionName = e1.getClass().getName().toString(); 
        		if (exceptionName.equals("java.lang.InterruptedException"))
        			MessageDialog.openError(getParent(), "Error Report Failed", "Send Error Report Cancelled");
        		else {
        			e1.printStackTrace();
        			MessageDialog.openError(getParent(), "Error Report Failed", "Unable to send error report: " + e1.toString());
        		}
			}
			resetProgress();
		}
    }

	private void doSend() {
		initialiseProgress("Sending...", 100);
		synchronized (sendWorkerMutex) {
			CrashInfo crashInfo = new CrashInfo(textWhatHappened.getText(), textEmailAddress.getText(), treeDetails.getItems()[0]);
			sendWorker = new SendWorker(crashInfo.toString());
		}
		sendWorker.start();
	}
	
	private void doCancel() {
		synchronized (sendWorkerMutex) {
			if (sendWorker != null)
				sendWorker.interrupt();
			else
				quit();
		}
	}
	
	private void quit() {
		shlCrashNotification.dispose();
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlCrashNotification = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shlCrashNotification.setSize(600, 500);
		shlCrashNotification.setText("Crash Notification");
		shlCrashNotification.setLayout(new FormLayout());
		
		Composite panelIntro = new Composite(shlCrashNotification, SWT.NONE);
		panelIntro.setLayout(new GridLayout(2, false));
		FormData fd_panelIntro = new FormData();
		fd_panelIntro.top = new FormAttachment(0);
		fd_panelIntro.right = new FormAttachment(100);
		fd_panelIntro.left = new FormAttachment(0);
		panelIntro.setLayoutData(fd_panelIntro);
		
		Label lblIconBoom = new Label(panelIntro, SWT.NONE);
		lblIconBoom.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblIconBoom.setImage(ResourceManager.getPluginImage("RelUI", "icons/nuclear-explosion.png"));
		
		Label lblInstructions = new Label(panelIntro, SWT.WRAP);
		lblInstructions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		lblInstructions.setText("Unfortunately, something went wrong.  We'd like to send the developers a message about it, so they can fix it in a future update.\n\nIf you'd rather not send anything, that's ok.  Press the Cancel button and nothing will be sent.\n\nOtherwise, please answer the following questions as best you can and remove any information that you don't want to send.  Then press the Send button to transmit it to the developers.");
		
		Label lblStep1 = new Label(shlCrashNotification, SWT.NONE);
		FormData fd_lblStep1 = new FormData();
		fd_lblStep1.top = new FormAttachment(panelIntro, 10);
		fd_lblStep1.left = new FormAttachment(0, 10);
		lblStep1.setLayoutData(fd_lblStep1);
		lblStep1.setText("1. What were you doing when this happened?");
		
		textWhatHappened = new Text(shlCrashNotification, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		FormData fd_textWhatHappened = new FormData();
		fd_textWhatHappened.top = new FormAttachment(lblStep1, 6);
		fd_textWhatHappened.left = new FormAttachment(0, 10);
		fd_textWhatHappened.right = new FormAttachment(100, -10);
		textWhatHappened.setLayoutData(fd_textWhatHappened);
		
		Label lblStep2 = new Label(shlCrashNotification, SWT.NONE);
		FormData fd_lblStep2 = new FormData();
		lblStep2.setLayoutData(fd_lblStep2);
		lblStep2.setText("2. What is your email address?  (optional - we'll only use it if we need to ask you further questions)");
		
		textEmailAddress = new Text(shlCrashNotification, SWT.BORDER);
		FormData fd_textEmailAddress = new FormData();
		textEmailAddress.setLayoutData(fd_textEmailAddress);
		
		Label lblStep3 = new Label(shlCrashNotification, SWT.NONE);
		FormData fd_lblStep3 = new FormData();
		lblStep3.setLayoutData(fd_lblStep3);
		lblStep3.setText("3. Examine these further details and un-check anything you don't want to send.");
		
		treeDetails = new Tree(shlCrashNotification, SWT.BORDER | SWT.CHECK);
		FormData fd_treeDetails = new FormData();
		treeDetails.setLayoutData(fd_treeDetails);
		fd_treeDetails.height = 75;
	    treeDetails.addListener(SWT.Selection, new Listener() {
	        @Override
			public void handleEvent(Event event) {
	            if (event.detail == SWT.CHECK) {
	                TreeItem item = (TreeItem) event.item;
	                boolean checked = item.getChecked();
	                checkItems(item, checked);
	                checkPath(item.getParentItem(), checked, false);
	            }
	        }
	    });
		
		lblProgress = new Label(shlCrashNotification, SWT.NONE);
		fd_treeDetails.bottom = new FormAttachment(lblProgress, -10);
		FormData fd_lblProgress = new FormData();
		fd_lblProgress.right = new FormAttachment(textWhatHappened, 0, SWT.RIGHT);
		lblProgress.setLayoutData(fd_lblProgress);
		lblProgress.setText("Progress...");
		
		progressBar = new ProgressBar(shlCrashNotification, SWT.NONE);
		FormData fd_progressBar = new FormData();
		progressBar.setLayoutData(fd_progressBar);
		
		btnCancel = new Button(shlCrashNotification, SWT.NONE);
		FormData fd_btnCancel = new FormData();
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.setText("Cancel");
		btnCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doCancel();
			}
		});
		
		btnSend = new Button(shlCrashNotification, SWT.NONE);
		fd_btnCancel.right = new FormAttachment(btnSend, -6);
		FormData fd_btnSend = new FormData();
		fd_btnSend.bottom = new FormAttachment(100, -10);
		fd_btnSend.right = new FormAttachment(100, -10);
		btnSend.setLayoutData(fd_btnSend);
		btnSend.setText("Send");
		btnSend.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doSend();
			}
		});
		
		fd_btnCancel.bottom = new FormAttachment(100, -10);
		fd_btnCancel.right = new FormAttachment(btnSend, -10);
		
		fd_progressBar.bottom = new FormAttachment(btnCancel, -10);
		fd_progressBar.left = new FormAttachment(0, 10);
		fd_progressBar.right = new FormAttachment(100, -10);
		
		fd_lblProgress.bottom = new FormAttachment(progressBar, -6);
		fd_lblProgress.left = new FormAttachment(0, 10);
		fd_treeDetails.left = new FormAttachment(0, 10);
		fd_treeDetails.right = new FormAttachment(100, -10);
		
		fd_lblStep3.bottom = new FormAttachment(treeDetails, -6);
		fd_lblStep3.left = new FormAttachment(0, 10);
		
		fd_textEmailAddress.bottom = new FormAttachment(lblStep3, -10);
		fd_textEmailAddress.left = new FormAttachment(0, 10);
		fd_textEmailAddress.right = new FormAttachment(100, -10);

		fd_lblStep2.bottom = new FormAttachment(textEmailAddress, -6);
		fd_lblStep2.left = new FormAttachment(0, 10);

		fd_textWhatHappened.bottom = new FormAttachment(lblStep2, -10);
		
		lblProgress.setEnabled(false);
		progressBar.setEnabled(false);
	}
}
