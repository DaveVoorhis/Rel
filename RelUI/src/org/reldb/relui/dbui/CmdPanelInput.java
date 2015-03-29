package org.reldb.relui.dbui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;

public class CmdPanelInput extends Composite {
	
	private CmdPanelBottom cmdPanelBottom;
	private StyledText inputText;

	private ToolItem tlitmPrevHistory;	
	private ToolItem tlitmNextHistory;
	
	private Vector<String> entryHistory = new Vector<String>();
	private int currentHistoryItem = 0;
	
	private FileDialog loadDialog = null;
	private FileDialog saveDialog = null;
	
	private void showRunningStart() {
		cmdPanelBottom.setEnabledRunButton(false);		
	}
	
	private void showRunningStop() {
		cmdPanelBottom.setEnabledRunButton(true);
	}

	/** Override to receive notification that the cancel button has been pressed. */
	public void notifyStop() {}
	
	/** Override to receive notification that the run button has been pressed.  Must invoke done() when 
	 * ready to receive another notification. */
	public void notifyGo(String text) {}
	
	/** Must invoke this after processing invoked by run button has finished, and we're ready to receive another 'run' notification. */
	public void done() {
		showRunningStop();
	}
	
	public static boolean isLastNonWhitespaceCharacter(String s, char c) {
		int endPosn = s.length() - 1;
		if (endPosn < 0)
			return false;
		while (endPosn >= 0 && Character.isWhitespace(s.charAt(endPosn)))
			endPosn--;
		if (endPosn < 0)
			return false;
		return (s.charAt(endPosn) == c);
	}

	public void insertInputText(String newText) {
		int insertionStart;
		int insertionEnd;
		Point selectionRange = inputText.getSelectionRange();
		if (selectionRange == null) {
			insertionStart = insertionEnd = inputText.getCaretOffset();
		} else {
			insertionStart = selectionRange.x;
			insertionEnd = selectionRange.y + insertionStart;
		}
		String before = inputText.getText().substring(0, insertionStart);
		String after = inputText.getText().substring(insertionEnd, inputText.getText().length());
		inputText.setText(before + newText + after);
		inputText.setCaretOffset(before.length() + newText.length());
	}
	
	public String getInputText() {
		return inputText.getText();
	}

	public StyledText getInputTextWidget() {
		return inputText;
	}
	
	/** Override to be notified that copyInputToOutput setting has changed. */
	protected void setCopyInputToOutput(boolean selection) {
	}

	/** Override to be notified that an announcement has been raised. */
	protected void announcement(String msg) {
	}
	
	/** Override to be notified about an error. */
	protected void announceError(String msg, Throwable t) {
	}
	
	/** Get number of items in History. */
	private int getHistorySize() {
		return entryHistory.size();
	}

	/** Get history item. */
	private String getHistoryItemAt(int index) {
		if (index < 0 || index >= entryHistory.size())
			return null;
		return entryHistory.get(index);
	}

	/** Get previous history item. */
	private String getPreviousHistoryItem() {
		if (currentHistoryItem > 0)
			currentHistoryItem--;
		setButtons();
		return getHistoryItemAt(currentHistoryItem);
	}

	/** Get next history item. */
	private String getNextHistoryItem() {
		currentHistoryItem++;
		if (currentHistoryItem >= entryHistory.size())
			currentHistoryItem = entryHistory.size() - 1;
		setButtons();
		return getHistoryItemAt(currentHistoryItem);
	}

	/** Add a history item. */
	private void addHistoryItem(String s) {
		entryHistory.add(s);
		currentHistoryItem = entryHistory.size() - 1;
		setButtons();
	}

	/** Set up history button status. */
	private void setButtons() {
		tlitmPrevHistory.setEnabled(currentHistoryItem > 0 && getHistorySize() > 1);
		tlitmNextHistory.setEnabled(currentHistoryItem < getHistorySize() - 1 && getHistorySize() > 1);
	}

	private void run() {
		showRunningStart();
		String text = getInputText();
		addHistoryItem(text);
		notifyGo(text);	
	}

	private void stop() {
		notifyStop();
	}
	
	private void ensureSaveDialogExists() {
		if (saveDialog == null) {
			saveDialog = new FileDialog(getShell(), SWT.SAVE);
			saveDialog.setFilterPath(System.getProperty("user.home"));
			saveDialog.setText("Save");
			saveDialog.setOverwrite(true);
		}		
	}

	private void ensureLoadDialogExists() {
		if (loadDialog == null) {
			loadDialog = new FileDialog(getShell(), SWT.OPEN);
			loadDialog.setFilterPath(System.getProperty("user.home"));
			loadDialog.setText("Load");
		}
	}
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CmdPanelInput(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());
		
		ToolBar toolBar = new ToolBar(this, SWT.FLAT);
		FormData fd_toolBar = new FormData();
		fd_toolBar.left = new FormAttachment(0);
		fd_toolBar.top = new FormAttachment(0);
		fd_toolBar.right = new FormAttachment(100);
		toolBar.setLayoutData(fd_toolBar);
		
		inputText = new StyledText(this, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI | SWT.WRAP);
		FormData fd_inputText = new FormData();
		fd_inputText.right = new FormAttachment(toolBar, 0, SWT.RIGHT);
		fd_inputText.top = new FormAttachment(toolBar);
		fd_inputText.left = new FormAttachment(toolBar, 0, SWT.LEFT);
		inputText.setLayoutData(fd_inputText);
		inputText.addCaretListener(new CaretListener() {
			@Override
			public void caretMoved(CaretEvent event) {
				int offset = event.caretOffset;
				try {
					int line = inputText.getLineAtOffset(offset);
					int column = offset - inputText.getOffsetAtLine(line);
					cmdPanelBottom.setRowColDisplay("" + (line + 1) + ":" + (column + 1));
				} catch (Exception ble) {
					cmdPanelBottom.setRowColDisplay("?:?");
				}
				if (isLastNonWhitespaceCharacter(inputText.getText(), ';')) {
					cmdPanelBottom.setRunButtonPrompt("Execute (F5)");
				} else {
					cmdPanelBottom.setRunButtonPrompt("Evaluate (F5)");
				}
			}
		});
		inputText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == 0x100000e)
					run();
			}
		});
		
		cmdPanelBottom = new CmdPanelBottom(this, SWT.NONE) {
			@Override
			public void go() {
				run();
			}
			public void cancel() {
				stop();
			}
		};
		fd_inputText.bottom = new FormAttachment(cmdPanelBottom, 191);
		FormData fd_cmdPanelBottom = new FormData();
		fd_cmdPanelBottom.left = new FormAttachment(0);
		fd_cmdPanelBottom.right = new FormAttachment(100);
		fd_cmdPanelBottom.bottom = new FormAttachment(100);
		fd_inputText.bottom = new FormAttachment(cmdPanelBottom);
		cmdPanelBottom.setLayoutData(fd_cmdPanelBottom);

		tlitmPrevHistory = new ToolItem(toolBar, SWT.NONE);
		tlitmPrevHistory.setToolTipText("Load previous historical entry");
		tlitmPrevHistory.setText("<");
		tlitmPrevHistory.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				inputText.setText(getPreviousHistoryItem());
				inputText.setSelection(0, inputText.getText().length());
				inputText.setFocus();
			}
		});
		
		tlitmNextHistory = new ToolItem(toolBar, SWT.NONE);
		tlitmNextHistory.setToolTipText("Load next historical entry");
		tlitmNextHistory.setText(">");
		tlitmNextHistory.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				inputText.setText(getNextHistoryItem());
				inputText.setSelection(0, inputText.getText().length());
				inputText.setFocus();
			}
		});
		
		ToolItem tlitmClear = new ToolItem(toolBar, SWT.NONE);
		tlitmClear.setToolTipText("Clear");
		tlitmClear.setImage(ResourceManager.getPluginImage("RelUI", "icons/clearIcon.png"));
		tlitmClear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				inputText.setText("");
				inputText.setFocus();
			}
		});
		
		ToolItem tlitmLoad = new ToolItem(toolBar, SWT.NONE);
		tlitmLoad.setToolTipText("Load file");
		tlitmLoad.setImage(ResourceManager.getPluginImage("RelUI", "icons/loadIcon.png"));
		tlitmLoad.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ensureLoadDialogExists();
				loadDialog.setFileName("");
				loadDialog.setText("Load File");
				loadDialog.setFilterExtensions(new String[] {"*.rel", "*.*"});
				loadDialog.setFilterNames(new String[] {"Rel script", "All Files"});
				String fname = loadDialog.open();
				if (fname == null)
					return;
				try {
					BufferedReader f = new BufferedReader(new FileReader(fname));
					StringBuffer fileImage = new StringBuffer();
					String line;
					while ((line = f.readLine()) != null) {
						if (fileImage.length() > 0)
							fileImage.append('\n');
						fileImage.append(line);
					}
					f.close();
					insertInputText(fileImage.toString());
					announcement("Loaded " + fname);
				} catch (Exception ioe) {
					announceError(ioe.toString(), ioe);
				}
			}
		});
		
		ToolItem tlitmGetPath = new ToolItem(toolBar, SWT.NONE);
		tlitmGetPath.setToolTipText("Get file path");
		tlitmGetPath.setImage(ResourceManager.getPluginImage("RelUI", "icons/pathIcon.png"));
		tlitmGetPath.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ensureLoadDialogExists();
				loadDialog.setFileName("");
				loadDialog.setText("Get File Path");
				loadDialog.setFilterExtensions(new String[] {"*.*"});
				loadDialog.setFilterNames(new String[] {"All Files"});
				String fname = loadDialog.open();
				if (fname == null)
					return;
				insertInputText('"' + fname + '"');
			}
		});
		
		ToolItem tlitmSave = new ToolItem(toolBar, SWT.NONE);
		tlitmSave.setToolTipText("Save");
		tlitmSave.setImage(ResourceManager.getPluginImage("RelUI", "icons/saveIcon.png"));
		tlitmSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ensureSaveDialogExists();
				saveDialog.setText("Save Input");
				saveDialog.setFilterExtensions(new String[] {"*.rel", "*.*"});
				saveDialog.setFilterNames(new String[] {"Rel script", "All Files"});
				String fname = saveDialog.open();
				if (fname == null)
					return;
				try {
					BufferedWriter f = new BufferedWriter(new FileWriter(fname));
					f.write(inputText.getText());
					f.close();
					announcement("Saved " + fname);
				} catch (IOException ioe) {
					announceError(ioe.toString(), ioe);
				}
			}
		});
		
		ToolItem tlitmSaveHistory = new ToolItem(toolBar, SWT.NONE);
		tlitmSaveHistory.setToolTipText("Save history");
		tlitmSaveHistory.setImage(ResourceManager.getPluginImage("RelUI", "icons/saveHistoryIcon.png"));
		tlitmSaveHistory.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ensureSaveDialogExists();
				saveDialog.setText("Save History");
				saveDialog.setFilterExtensions(new String[] {"*.rel", "*.*"});
				saveDialog.setFilterNames(new String[] {"Rel script", "All Files"});
				String fname = saveDialog.open();
				if (fname == null)
					return;
				try {
					BufferedWriter f = new BufferedWriter(new FileWriter(fname));
					for (int i = 0; i < getHistorySize(); i++) {
						f.write("// History item #" + (i + 1) + "\n");
						f.write(getHistoryItemAt(i));
						f.write("\n\n");
					}
					f.write("// Current entry" + "\n");
					f.write(inputText.getText());
					f.close();
					announcement("Saved " + fname);
				} catch (IOException ioe) {
					announceError(ioe.toString(), ioe);
				}
			}
		});
		
		ToolItem tlitmCopyToOutput = new ToolItem(toolBar, SWT.CHECK);
		tlitmCopyToOutput.setToolTipText("Copy input to output");
		tlitmCopyToOutput.setSelection(true);
		tlitmCopyToOutput.setImage(ResourceManager.getPluginImage("RelUI", "icons/copyToOutputIcon.png"));
		tlitmCopyToOutput.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setCopyInputToOutput(tlitmCopyToOutput.getSelection());
			}
		});
		
		ToolItem tlitmWrap = new ToolItem(toolBar, SWT.CHECK);
		tlitmWrap.setToolTipText("Wrap text");
		tlitmWrap.setSelection(true);
		tlitmWrap.setImage(ResourceManager.getPluginImage("RelUI", "icons/wrapIcon.png"));
		tlitmWrap.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				inputText.setWordWrap(tlitmWrap.getSelection());
			}
		});
		
		setButtons();
	}
	
}
