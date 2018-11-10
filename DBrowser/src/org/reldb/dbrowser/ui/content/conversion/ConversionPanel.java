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
import org.reldb.dbrowser.DBrowser;
import org.reldb.dbrowser.ui.DbConnection;
import org.reldb.dbrowser.ui.DbTab;

public class ConversionPanel extends Composite {

	private StyledText textOutput;
	private Label lblConvert;
	private Button btnConvert;

	private boolean converted = false;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public ConversionPanel(Composite parent, DbTab dbTab, String message, String dbDir, int style) {
		super(parent, style);
		FormLayout formLayout = new FormLayout();
		formLayout.marginWidth = 5;
		formLayout.marginHeight = 5;
		setLayout(formLayout);

		lblConvert = new Label(this, SWT.NONE);
		FormData fd_lblConvert = new FormData();
		fd_lblConvert.top = new FormAttachment(0);
		fd_lblConvert.left = new FormAttachment(0);
		fd_lblConvert.right = new FormAttachment(100);
		lblConvert.setLayoutData(fd_lblConvert);
		lblConvert.setText(message);

		btnConvert = new Button(this, SWT.NONE);
		FormData fd_btnConvert = new FormData();
		fd_btnConvert.top = new FormAttachment(lblConvert);
		fd_btnConvert.left = new FormAttachment(0);
		fd_btnConvert.right = new FormAttachment(100);
		btnConvert.setLayoutData(fd_btnConvert);
		btnConvert.setText("Convert to the Current Format");
		btnConvert.addListener(SWT.Selection, e -> {
			if (converted) {
				dbTab.openLocalDatabase(dbDir);
			} else {
				if (MessageDialog.openConfirm(DBrowser.getShell(), "Convert Database to the Current Format?",
						"Are you sure you wish to convert database " + dbDir + " to the current format?")) {
					performConversion(dbDir);
				}
			}
		});

		textOutput = new StyledText(this, SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL);
		textOutput.setEditable(false);
		FormData fd_textOutput = new FormData();
		fd_textOutput.top = new FormAttachment(btnConvert);
		fd_textOutput.bottom = new FormAttachment(100);
		fd_textOutput.left = new FormAttachment(0);
		fd_textOutput.right = new FormAttachment(100);
		textOutput.setLayoutData(fd_textOutput);
	}

	private void output(String s) {
		getDisplay().syncExec(() -> {
			textOutput.append(s);
			textOutput.append("\n");
			textOutput.setCaretOffset(textOutput.getCharCount());
			textOutput.setSelection(textOutput.getCaretOffset(), textOutput.getCaretOffset());
		});
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
						if (in == null)
							break;
						output(in);
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
			output(e1.toString());
			return;
		}
		PrintStream conversionOutput = new PrintStream(pipeOutput, true);
		Thread converter = new Thread() {
			public void run() {
				try {
					DbConnection.convertToLatestFormat(dbDir, conversionOutput);
					converted = true;
					getDisplay().asyncExec(() -> {
						lblConvert.setText("Conversion complete.");
						btnConvert.setText("Open database " + dbDir);
						DBrowser.setStatus("Conversion complete.");
					});
				} catch (Throwable e) {
					outputRunning = false;
					output(e.toString());
				}
			}
		};
		converter.start();
	}
}
