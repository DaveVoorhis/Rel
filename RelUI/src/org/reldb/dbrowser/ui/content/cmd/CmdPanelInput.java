package org.reldb.dbrowser.ui.content.cmd;

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
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.reldb.dbrowser.ui.IconLoader;
import org.reldb.dbrowser.ui.Tabs;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeAdapter;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeEvent;
import org.reldb.dbrowser.ui.preferences.PreferenceChangeListener;
import org.reldb.dbrowser.ui.preferences.PreferencePageCmd;
import org.reldb.dbrowser.ui.preferences.PreferencePageGeneral;
import org.reldb.dbrowser.ui.preferences.Preferences;

public class CmdPanelInput extends Composite {
	
	private CmdPanelBottom cmdPanelBottom;
	private StyledText inputText;

	private ToolItem tlitmPrevHistory;	
	private ToolItem tlitmNextHistory;
	private ToolItem tlitmClear;
	private ToolItem tlitmLoad;
	private ToolItem tlitmGetPath;
	private ToolItem tlitmSave;
	private ToolItem tlitmSaveHistory;
	private ToolItem tlitmCopyToOutput;
	private ToolItem tlitmWrap;
	
	private Vector<String> entryHistory = new Vector<String>();
	private int currentHistoryItem = 0;
	
	private FileDialog loadDialog = null;
	private FileDialog loadPathDialog = null;
	private FileDialog saveDialog = null;
    
    private PreferenceChangeListener iconPreferenceChangeListener;
    private PreferenceChangeListener fontPreferenceChangeListener;
	
    private CmdPanelOutput cmdPanelOutput;
    
	private boolean copyInputToOutput = true;
    
	/**
	 * Create the composite.
	 * @param parent
	 * @param cmdPanelOutput 
	 * @param style
	 */
	public CmdPanelInput(Composite parent, CmdPanelOutput cmdPanelOutput, int style) {
		super(parent, style);
		setLayout(new FormLayout());
		
		this.cmdPanelOutput = cmdPanelOutput;
		
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
					int row = inputText.getLineAtOffset(offset);
					int characterIndex = offset - inputText.getOffsetAtLine(row);
					String line = inputText.getLine(row);
					int tabSize = inputText.getTabs();
					int displayColumn = Tabs.characterIndexToDisplayColumn(tabSize, line, characterIndex);
					cmdPanelBottom.setRowColDisplay("" + (row + 1) + ":" + (displayColumn + 1));
				} catch (Exception ble) {
					cmdPanelBottom.setRowColDisplay("?:?");
				}
				if (CmdPanelOutput.isLastNonWhitespaceCharacter(inputText.getText(), ';')) {
					cmdPanelBottom.setRunButtonPrompt("Execute (F5)");
				} else {
					cmdPanelBottom.setRunButtonPrompt("Evaluate (F5)");
				}
			}
		});
		inputText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == 0x100000e && inputText.isEnabled())
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
		tlitmNextHistory.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				inputText.setText(getNextHistoryItem());
				inputText.setSelection(0, inputText.getText().length());
				inputText.setFocus();
			}
		});
		
		tlitmClear = new ToolItem(toolBar, SWT.NONE);
		tlitmClear.setToolTipText("Clear");
		tlitmClear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				inputText.setText("");
				inputText.setFocus();
			}
		});
		
		tlitmLoad = new ToolItem(toolBar, SWT.NONE);
		tlitmLoad.setToolTipText("Load file");
		tlitmLoad.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ensureLoadDialogExists();
				loadDialog.setFileName("");
				loadDialog.setText("Load File");
				String fname = loadDialog.open();
				if (fname == null)
					return;
				loadFile(fname);
			}
		});
		
		tlitmGetPath = new ToolItem(toolBar, SWT.NONE);
		tlitmGetPath.setToolTipText("Get file path");
		tlitmGetPath.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ensureLoadPathDialogExists();
				loadPathDialog.setFileName("");
				loadPathDialog.setText("Get File Path");
				String fname = loadPathDialog.open();
				if (fname == null)
					return;
				insertInputText('"' + fname + '"');
			}
		});
		
		tlitmSave = new ToolItem(toolBar, SWT.NONE);
		tlitmSave.setToolTipText("Save");
		tlitmSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ensureSaveDialogExists();
				saveDialog.setText("Save Input");
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
		
		tlitmSaveHistory = new ToolItem(toolBar, SWT.NONE);
		tlitmSaveHistory.setToolTipText("Save history");
		tlitmSaveHistory.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ensureSaveDialogExists();
				saveDialog.setText("Save History");
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
		
		tlitmCopyToOutput = new ToolItem(toolBar, SWT.CHECK);
		tlitmCopyToOutput.setToolTipText("Copy input to output");
		tlitmCopyToOutput.setSelection(true);
		tlitmCopyToOutput.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setCopyInputToOutput(tlitmCopyToOutput.getSelection());
			}
		});
		
		tlitmWrap = new ToolItem(toolBar, SWT.CHECK);
		tlitmWrap.setToolTipText("Wrap text");
		tlitmWrap.setSelection(true);
		tlitmWrap.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				inputText.setWordWrap(tlitmWrap.getSelection());
			}
		});
		
		setupButtons();
		
		setupIcons();		
		iconPreferenceChangeListener = new PreferenceChangeAdapter("CmdPanelInput_icon") {
			@Override
			public void preferenceChange(PreferenceChangeEvent evt) {
				setupIcons();
			}
		};		
		Preferences.addPreferenceChangeListener(PreferencePageGeneral.LARGE_ICONS, iconPreferenceChangeListener);

		setupFont();
		fontPreferenceChangeListener = new PreferenceChangeAdapter("CmdPanelInput_font") {
			@Override
			public void preferenceChange(PreferenceChangeEvent preferenceChangeEvent) {
				setupFont();
			}
		};
		Preferences.addPreferenceChangeListener(PreferencePageCmd.CMD_FONT, fontPreferenceChangeListener);
	}
	
	private void setupIcons() {
		tlitmPrevHistory.setImage(IconLoader.loadIcon("previousIcon"));
		tlitmNextHistory.setImage(IconLoader.loadIcon("nextIcon"));
		tlitmClear.setImage(IconLoader.loadIcon("clearIcon"));
		tlitmLoad.setImage(IconLoader.loadIcon("loadIcon"));
		tlitmGetPath.setImage(IconLoader.loadIcon("pathIcon"));
		tlitmSave.setImage(IconLoader.loadIcon("saveIcon"));
		tlitmSaveHistory.setImage(IconLoader.loadIcon("saveHistoryIcon"));
		tlitmCopyToOutput.setImage(IconLoader.loadIcon("copyToOutputIcon"));
		tlitmWrap.setImage(IconLoader.loadIcon("wrapIcon"));		
	}

	private void setupFont() {
		inputText.setFont(Preferences.getPreferenceFont(getDisplay(), PreferencePageCmd.CMD_FONT));
	}

	public void copyOutputToInput() {
		String selection = cmdPanelOutput.getSelectionText();
		if (selection.length() == 0)
			insertInputText(getInputText() + cmdPanelOutput.getText());
		else
			insertInputText(getInputText() + selection);
	}
	
	public void dispose() {
		Preferences.removePreferenceChangeListener(PreferencePageGeneral.LARGE_ICONS, iconPreferenceChangeListener);
		Preferences.removePreferenceChangeListener(PreferencePageCmd.CMD_FONT, fontPreferenceChangeListener);
		super.dispose();
	}
	
	/** Override to receive notification that the cancel button has been pressed. */
	public void notifyStop() {
		cmdPanelOutput.notifyStop();
	}
	
	/** Override to receive notification that the run button has been pressed.  Must invoke done() when 
	 * ready to receive another notification. */
	public void notifyGo(String text) {
		cmdPanelOutput.go(text, copyInputToOutput);
	}
	
	/** Must invoke this after processing invoked by run button has finished, and we're ready to receive another 'run' notification. */
	public void done() {
		showRunningStop();
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
	
	public void loadFile(String fname) {
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
		
	private void showRunningStart() {
		cmdPanelBottom.setEnabledRunButton(false);		
	}
	
	private void showRunningStop() {
		cmdPanelBottom.setEnabledRunButton(true);
	}
	
	/** Override to be notified that copyInputToOutput setting has changed. */
	protected void setCopyInputToOutput(boolean selection) {
		copyInputToOutput = selection;
	}

	/** Override to be notified that an announcement has been raised. */
	protected void announcement(String msg) {
		cmdPanelOutput.systemResponse(msg);
	}
	
	/** Override to be notified about an error. */
	protected void announceError(String msg, Throwable t) {
		cmdPanelOutput.badResponse(msg);
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
		setupButtons();
		return getHistoryItemAt(currentHistoryItem);
	}

	/** Get next history item. */
	private String getNextHistoryItem() {
		currentHistoryItem++;
		if (currentHistoryItem >= entryHistory.size())
			currentHistoryItem = entryHistory.size() - 1;
		setupButtons();
		return getHistoryItemAt(currentHistoryItem);
	}

	/** Add a history item. */
	private void addHistoryItem(String s) {
		if (entryHistory.size() > 0 && s.equals(entryHistory.get(entryHistory.size() - 1)))
			return;
		entryHistory.add(s);
		currentHistoryItem = entryHistory.size() - 1;
		setupButtons();
	}

	/** Set up history button status. */
	private void setupButtons() {
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
			saveDialog.setFilterExtensions(new String[] {"*.rel", "*.*"});
			saveDialog.setFilterNames(new String[] {"Rel script", "All Files"});
			saveDialog.setText("Save");
			saveDialog.setOverwrite(true);
		}		
	}

	private void ensureLoadDialogExists() {
		if (loadDialog == null) {
			loadDialog = new FileDialog(getShell(), SWT.OPEN);
			loadDialog.setFilterPath(System.getProperty("user.home"));
			loadDialog.setFilterExtensions(new String[] {"*.rel", "*.*"});
			loadDialog.setFilterNames(new String[] {"Rel script", "All Files"});
			loadDialog.setText("Load File");
		}
	}

	private void ensureLoadPathDialogExists() {
		if (loadPathDialog == null) {
			loadPathDialog = new FileDialog(getShell(), SWT.OPEN);
			loadPathDialog.setFilterPath(System.getProperty("user.home"));
			loadDialog.setFilterExtensions(new String[] {"*.*"});
			loadDialog.setFilterNames(new String[] {"All Files"});
			loadPathDialog.setText("Load Path");
		}
	}
	
}
