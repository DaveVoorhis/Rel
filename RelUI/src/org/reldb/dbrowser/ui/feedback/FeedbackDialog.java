package org.reldb.dbrowser.ui.feedback;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.TreeItem;

public abstract class FeedbackDialog extends Dialog {

	protected Shell shlFeedback;
	
	protected Label lblProgress;
	protected ProgressBar progressBar;
	
	protected Button btnSend;
	protected Button btnCancel;
	
	protected Tree treeDetails;
	
	private TreeItem report;
	
	private Feedback phoneHome;
		
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public FeedbackDialog(Shell parent, int style, String title) {
		super(parent, style);
		setText(title);
		shlFeedback = createContents();
		phoneHome = new Feedback(btnSend, lblProgress, progressBar) {
		    public void completed(SendStatus sendStatus) {	
		    	FeedbackDialog.this.completed(sendStatus);
		    }
			public void quit() {
				FeedbackDialog.this.quit();
			}
		};
		report = newTreeItem(treeDetails, "Details");
	}
	
	protected void open() {
		shlFeedback.open();
		shlFeedback.layout();
		Display display = getParent().getDisplay();
		while (!shlFeedback.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}		
	}
	
	protected static String getCurrentTimeStamp() {
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
	
	protected void checkPath(TreeItem item, boolean checked, boolean grayed) {
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

	protected void checkItems(TreeItem item, boolean checked) {
	    item.setGrayed(false);
	    item.setChecked(checked);
	    TreeItem[] items = item.getItems();
	    for (int i = 0; i < items.length; i++) {
	        checkItems(items[i], checked);
	    }
	}
	
	protected void putServerInfoInTree(String serverInitialResponse) {
		TreeItem serverInfo = newTreeItem(report, "RelServerInfo");
		String[] lines = serverInitialResponse.split("\\r?\\n");
		for (String line: lines)
			for (RelServerInfo info: relServerInfo)
				if (line.startsWith(info.lookFor))
					newTreeItem(serverInfo, info.prompt + ": " + line.substring(info.lookFor.length() + 1));
	}
	
	private void putExceptionInTree(TreeItem root, Throwable t) {
		if (t != null) {
			newTreeItem(root, "ErrorClass: " + t.getClass().toString());
			putStacktraceInTree(root, t.getStackTrace());
			newTreeItem(root, "Message: " + t.toString());
			if (t.getCause() != null) {
				TreeItem cause = newTreeItem(root, "Cause");
				putExceptionInTree(cause, t.getCause());
			}
		} else {
			newTreeItem(root, "Error details unavailable.");
		}		
	}
	
	protected void putExceptionInTree(Throwable t) {
		TreeItem root = newTreeItem(report, "JavaException");
		putExceptionInTree(root, t);
	}
	
	protected void putQueryInfoInTree(String lastQuery) {
		newTreeItem(report, "Query: " + lastQuery.toString());		
	}
	
	protected void putClientInfoInTree(String clientVersion) {
		newTreeItem(report, "Timestamp: " + getCurrentTimeStamp().toString());
		newTreeItem(report, "Client version: " + clientVersion);
		newTreeItem(report, "Java version: " + System.getProperty("java.version"));
		newTreeItem(report, "Java vendor: " + System.getProperty("java.vendor"));		
	}
	
	protected void completed(Feedback.SendStatus sendStatus) {
		try {
			if (sendStatus.getResponse() != null && sendStatus.getResponse().startsWith("Success")) {
				Shell parent = getParent();
				quit();
	    		MessageDialog.openInformation(parent, "Report Sent", sendStatus.getResponse());
	    		return;
	        } else
	        	if (sendStatus.getException() != null) {
        			sendStatus.getException().printStackTrace();
	        		MessageDialog.openError(getParent(), "Report Failed", "Unable to send report: " + sendStatus.getException().toString());
	        	} else
	        		MessageDialog.openError(getParent(), "Report Failed", "Unable to send report: " + sendStatus.getResponse());
		} catch (Exception e1) {
    		String exceptionName = e1.getClass().getName().toString(); 
    		if (exceptionName.equals("java.lang.InterruptedException"))
    			MessageDialog.openError(getParent(), "Report Failed", "Send Report Cancelled");
    		else {
    			e1.printStackTrace();
    			MessageDialog.openError(getParent(), "Report Failed", "Unable to send report: " + e1.toString());
    		}
		}
	}

	protected abstract FeedbackInfo getFeedbackInfo();

	protected abstract Shell createContents();
	
	protected void doSend() {
		phoneHome.doSend(getFeedbackInfo().toString());
	}
	
	protected void doCancel() {
		phoneHome.doCancel();
	}
	
	protected void quit() {
		shlFeedback.dispose();
	}
}
