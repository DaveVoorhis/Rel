package org.reldb.dbrowser.ui.content.conversion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.reldb.dbrowser.DBrowser;
import org.reldb.dbrowser.ui.DbConnection;

public class ConversionPanel extends Composite {
	
	private StyledText textOutput;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ConversionPanel(Composite parent, String message, String dbDir, int style) {
		super(parent, style);
		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = 5;
		formLayout.marginHeight = 5;
		setLayout(formLayout);
		
		Label lblConvert = new Label(this, SWT.NONE);
		FormData fd_lblConvert = new FormData();
		fd_lblConvert.top = new FormAttachment(0);
		fd_lblConvert.left = new FormAttachment(0);
		fd_lblConvert.right = new FormAttachment(100);
		lblConvert.setLayoutData(fd_lblConvert);
		lblConvert.setText(message);
		
		Button btnConvert = new Button(this, SWT.NONE);
		FormData fd_btnConvert = new FormData();
		fd_btnConvert.top = new FormAttachment(lblConvert);
		fd_btnConvert.left = new FormAttachment(0);
		fd_btnConvert.right = new FormAttachment(100);
		btnConvert.setLayoutData(fd_btnConvert);
		btnConvert.setText("Convert to the Current Format");
		btnConvert.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
	    		if (MessageDialog.openConfirm(DBrowser.getShell(), "Convert Database to the Current Format?", 
	    				"Are you sure you wish to convert database " + dbDir + " to the current format?")) {
	    			performConversion(dbDir);
	    		}
			}
		});
		
		textOutput = new StyledText(this, SWT.BORDER | SWT.READ_ONLY);
		textOutput.setEditable(false);
		FormData fd_textOutput = new FormData();
		fd_textOutput.top = new FormAttachment(btnConvert);
		fd_textOutput.bottom = new FormAttachment(100);
		fd_textOutput.left = new FormAttachment(0);
		fd_textOutput.right = new FormAttachment(100);
		textOutput.setLayoutData(fd_textOutput);
	}
	
	private boolean outputRunning = true;

	private void performConversion(String dbDir) {
		PipedInputStream input = new PipedInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		outputRunning = true;
		Thread outputter = new Thread() {
			public void run() {
				while (outputRunning) {
					try {
						String in = reader.readLine();
						ConversionPanel.this.getDisplay().syncExec(new Runnable() {
							public void run() {
								textOutput.append(in);
								textOutput.append("\n");
							}
						});
					} catch (IOException e) {
						break;
					}
				}		
			}
		};
		outputter.start();
		PipedOutputStream pipeOutput;
		try {
			pipeOutput = new PipedOutputStream(input);
		} catch (IOException e1) {
			textOutput.append(e1.toString());
			return;
		}
		PrintStream conversionOutput = new PrintStream(pipeOutput, true);
		Thread converter = new Thread() {
			public void run() {
				try {
					DbConnection.convertToLatestFormat(dbDir, conversionOutput);
				} catch (Throwable e) {
					outputRunning = false;
					textOutput.append(e.toString() + "\n");
				}					
			}
		};
		converter.start();
	}
}
