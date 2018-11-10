package org.reldb.dbrowser.ui.feedback;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.reldb.dbrowser.ui.IconLoader;
import org.reldb.dbrowser.ui.version.Version;

public class BugReportDialog extends FeedbackDialog {
	private Text textWhatHappened;
	private Text textEmailAddress;
	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public BugReportDialog(Shell parent, int style) {
		super(parent, SWT.NONE, "Bug Report");
		putClientInfoInTree(Version.getVersion());
	}
	
	/**
	 * Launch the dialog.
	 * @param shell
	 */
	public static void launch(Shell shell) {
		try {
			shell.getDisplay().syncExec(() -> (new BugReportDialog(shell, SWT.None)).open());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected FeedbackInfo getFeedbackInfo() {
		FeedbackInfo report = new FeedbackInfo("BugReport");
		report.addString("WhatHappened", textWhatHappened.getText());
		report.addString("Email", textEmailAddress.getText());
		report.addTree(treeDetails.getItems()[0]);
		return report;
	}

	/** Create contents of the dialog. */
	protected Shell createContents() {
		Shell shlBugReport = new Shell(getParent(), SWT.DIALOG_TRIM | SWT.RESIZE | SWT.APPLICATION_MODAL);
		shlBugReport.setSize(700, 500);
		shlBugReport.setText(getText());
		shlBugReport.setLayout(new FormLayout());
		
		Composite panelIntro = new Composite(shlBugReport, SWT.NONE);
		panelIntro.setLayout(new GridLayout(2, false));
		FormData fd_panelIntro = new FormData();
		fd_panelIntro.top = new FormAttachment(0);
		fd_panelIntro.right = new FormAttachment(100);
		fd_panelIntro.left = new FormAttachment(0);
		panelIntro.setLayoutData(fd_panelIntro);
		
		Label lblIcon = new Label(panelIntro, SWT.NONE);
		lblIcon.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		lblIcon.setImage(IconLoader.loadIcon("Bug"));
		
		Label lblInstructions = new Label(panelIntro, SWT.WRAP);
		lblInstructions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		lblInstructions.setText("Please complete the following and remove any information that you don't want to send.  Then press the Send button to transmit it to the developers.");
		
		Label lblStep1 = new Label(shlBugReport, SWT.NONE);
		FormData fd_lblStep1 = new FormData();
		fd_lblStep1.top = new FormAttachment(panelIntro, 10);
		fd_lblStep1.left = new FormAttachment(0, 10);
		lblStep1.setLayoutData(fd_lblStep1);
		lblStep1.setText("1. Please describe the problem.");
		
		textWhatHappened = new Text(shlBugReport, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		FormData fd_textWhatHappened = new FormData();
		fd_textWhatHappened.top = new FormAttachment(lblStep1, 6);
		fd_textWhatHappened.left = new FormAttachment(0, 10);
		fd_textWhatHappened.right = new FormAttachment(100, -10);
		textWhatHappened.setLayoutData(fd_textWhatHappened);
		
		Label lblStep2 = new Label(shlBugReport, SWT.NONE);
		FormData fd_lblStep2 = new FormData();
		lblStep2.setLayoutData(fd_lblStep2);
		lblStep2.setText("2. What is your email address?  (optional - we'll only use it if we need to ask you further questions)");
		
		textEmailAddress = new Text(shlBugReport, SWT.BORDER);
		FormData fd_textEmailAddress = new FormData();
		textEmailAddress.setLayoutData(fd_textEmailAddress);
		
		Label lblStep3 = new Label(shlBugReport, SWT.NONE);
		FormData fd_lblStep3 = new FormData();
		lblStep3.setLayoutData(fd_lblStep3);
		lblStep3.setText("3. Examine these further details and un-check anything you don't want to send.");
		
		treeDetails = new Tree(shlBugReport, SWT.BORDER | SWT.CHECK);
		FormData fd_treeDetails = new FormData();
		treeDetails.setLayoutData(fd_treeDetails);
		fd_treeDetails.height = 75;
	    treeDetails.addListener(SWT.Selection, event -> {
	            if (event.detail == SWT.CHECK) {
	                TreeItem item = (TreeItem) event.item;
	                boolean checked = item.getChecked();
	                checkItems(item, checked);
	                checkPath(item.getParentItem(), checked, false);
	            }
	    });
		
		lblProgress = new Label(shlBugReport, SWT.NONE);
		fd_treeDetails.bottom = new FormAttachment(lblProgress, -10);
		FormData fd_lblProgress = new FormData();
		fd_lblProgress.right = new FormAttachment(textWhatHappened, 0, SWT.RIGHT);
		lblProgress.setLayoutData(fd_lblProgress);
		lblProgress.setText("Progress...");
		
		progressBar = new ProgressBar(shlBugReport, SWT.NONE);
		FormData fd_progressBar = new FormData();
		progressBar.setLayoutData(fd_progressBar);
		
		btnCancel = new Button(shlBugReport, SWT.NONE);
		FormData fd_btnCancel = new FormData();
		btnCancel.setLayoutData(fd_btnCancel);
		btnCancel.setText("Cancel");
		btnCancel.addListener(SWT.Selection, e -> doCancel());
		
		btnSend = new Button(shlBugReport, SWT.NONE);
		fd_btnCancel.right = new FormAttachment(btnSend, -6);
		FormData fd_btnSend = new FormData();
		fd_btnSend.bottom = new FormAttachment(100, -10);
		fd_btnSend.right = new FormAttachment(100, -10);
		btnSend.setLayoutData(fd_btnSend);
		btnSend.setText("Send");
		btnSend.addListener(SWT.Selection, e -> doSend());
		
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
		
		return shlBugReport;
	}
}
