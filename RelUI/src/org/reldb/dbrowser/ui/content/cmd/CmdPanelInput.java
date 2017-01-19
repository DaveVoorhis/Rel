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
import org.eclipse.swt.custom.LineBackgroundEvent;
import org.eclipse.swt.custom.LineBackgroundListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.SWTResourceManager;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
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
	private ToolItem tlitmUndo;
	private ToolItem tlitmRedo;
	private ToolItem tlitmCut;
	private ToolItem tlitmCopy;
	private ToolItem tlitmPaste;
	private ToolItem tlitmFindReplace;
	private ToolItem tlitmLoad;
	private ToolItem tlitmLoadInsert;
	private ToolItem tlitmGetPath;
	private ToolItem tlitmSave;
	private ToolItem tlitmSaveHistory;
	private ToolItem tlitmCopyToOutput;
	private ToolItem tlitmWrap;
	private ToolItem tlitmCharacters;
	
	private Vector<String> entryHistory = new Vector<String>();
	private int currentHistoryItem = 0;
	
	private FileDialog loadDialog = null;
	private FileDialog loadPathDialog = null;
	private FileDialog saveDialog = null;
	private FileDialog saveHistoryDialog = null;
    
    private PreferenceChangeListener iconPreferenceChangeListener;
    private PreferenceChangeListener fontPreferenceChangeListener;
	
    private CmdPanelOutput cmdPanelOutput;
    
	private boolean copyInputToOutput = true;
	
	private UndoRedo undoredo;
    
	private static final Color backgroundHighlight = SWTResourceManager.getColor(250, 255, 252);
	
	private SpecialCharacters specialCharacterDisplay;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param cmdPanelOutput 
	 * @param style
	 */
	public CmdPanelInput(Composite parent, CmdPanelOutput cmdPanelOutput, int cmdStyle) {
		super(parent, SWT.NONE);
		setLayout(new FormLayout());
		
		this.cmdPanelOutput = cmdPanelOutput;
	
		ToolBar toolBar = new ToolBar(this, SWT.FLAT);
		FormData fd_toolBar = new FormData();
		fd_toolBar.left = new FormAttachment(0);
		fd_toolBar.top = new FormAttachment(0);
		fd_toolBar.right = new FormAttachment(100);
		toolBar.setLayoutData(fd_toolBar);
		
		inputText = new StyledText(this, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		FormData fd_inputText = new FormData();
		fd_inputText.right = new FormAttachment(toolBar, 0, SWT.RIGHT);
		fd_inputText.top = new FormAttachment(toolBar);
		fd_inputText.left = new FormAttachment(toolBar, 0, SWT.LEFT);
		inputText.setLayoutData(fd_inputText);
		
		undoredo = new UndoRedo(inputText);
		RelLineStyler lineStyler = new RelLineStyler();

		inputText.addLineStyleListener(lineStyler);
		inputText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				lineStyler.parseBlockComments(inputText.getText());
				inputText.redraw();
			}
		});
		inputText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (((e.stateMask & SWT.CTRL) != 0 || (e.stateMask & SWT.COMMAND) != 0) && inputText.isEnabled()) {
					switch (e.keyCode) {
					case 'a': doSelectAll(); return;
					case 'f': doFindReplace(); return;
					case 'y': doRedo(); return;
					case 'z': 
						if ((e.stateMask & SWT.SHIFT) != 0)
							doRedo();
						else
							doUndo();
						return;
					}
				}
			}
		});
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
		inputText.addLineBackgroundListener(new LineBackgroundListener() {
			@Override
			public void lineGetBackground(LineBackgroundEvent event) {
				int line = inputText.getLineAtOffset(event.lineOffset);
				if (!((line & 1) == 0))
					event.lineBackground = backgroundHighlight;
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

		if ((cmdStyle & CmdPanel.NO_INPUT_TOOLBAR) == 0) {
			tlitmCharacters = new ToolItem(toolBar, SWT.NONE);
			tlitmCharacters.setToolTipText("Special characters");
			tlitmCharacters.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					specialCharacterDisplay.open();
				}
			});
			
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

			tlitmUndo = new ToolItem(toolBar, SWT.NONE);
			tlitmUndo.setToolTipText("Undo");
			tlitmUndo.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					doUndo();
				}
			});
			
			tlitmRedo = new ToolItem(toolBar, SWT.NONE);
			tlitmRedo.setToolTipText("Redo");
			tlitmRedo.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					doRedo();
				}
			});

			tlitmCut = new ToolItem(toolBar, SWT.NONE);
			tlitmCut.setToolTipText("Cut");
			tlitmCut.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					doCut();
				}
			});
			
			tlitmCopy = new ToolItem(toolBar, SWT.NONE);
			tlitmCopy.setToolTipText("Copy");
			tlitmCopy.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					doCopy();
				}
			});
			
			tlitmPaste = new ToolItem(toolBar, SWT.NONE);
			tlitmPaste.setToolTipText("Paste");
			tlitmPaste.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					doPaste();
				}
			});
			
			tlitmFindReplace = new ToolItem(toolBar, SWT.NONE);
			tlitmFindReplace.setToolTipText("Find/Replace");
			tlitmFindReplace.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					doFindReplace();
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
					ensureSaveDialogExists();
					saveDialog.setFileName(loadDialog.getFileName());
					saveDialog.setFilterPath(loadDialog.getFilterPath());
				}
			});
			
			tlitmLoadInsert = new ToolItem(toolBar, SWT.NONE);
			tlitmLoadInsert.setToolTipText("Load and insert file");
			tlitmLoadInsert.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					ensureLoadDialogExists();
					loadDialog.setFileName("");
					loadDialog.setText("Load and Insert File");
					String fname = loadDialog.open();
					if (fname == null)
						return;
					loadInsertFile(fname);
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
					insertInputText('"' + fname.replace("\\", "\\\\") + '"');
				}
			});
			
			tlitmSave = new ToolItem(toolBar, SWT.NONE);
			tlitmSave.setToolTipText("Save");
			tlitmSave.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					ensureSaveDialogExists();
					saveDialog.setText("Save Input");
					if (saveDialog.getFileName().length() == 0)
						saveDialog.setFileName(getDefaultSaveFileName());
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
					ensureSaveHistoryDialogExists();
					saveHistoryDialog.setText("Save History");
					if (saveHistoryDialog.getFileName().length() == 0)
						saveHistoryDialog.setFileName("InputHistory");
					String fname = saveHistoryDialog.open();
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
		}
		
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
				
		specialCharacterDisplay = new SpecialCharacters(parent.getShell(), inputText);
	}

	protected String getDefaultSaveFileName() {
		return "Untitled";
	}

	private void doUndo() {
		undoredo.undo();
	}

	private void doRedo() {
		undoredo.redo();
	}

	protected void doPaste() {
		inputText.paste();
	}

	protected void doCopy() {
		inputText.copy();
	}

	protected void doCut() {
		inputText.cut();
	}

	private void doSelectAll() {
		int topIndex = inputText.getTopIndex();
		inputText.setSelection(0, inputText.getCharCount());
		inputText.setTopIndex(topIndex);
	}

	private void doFindReplace() {
		new FindReplace(getShell(), inputText).open();
	}

	public void copyOutputToInput() {
		String selection = cmdPanelOutput.getSelectionText();
		if (selection.length() == 0)
			insertInputText(inputText.getText() + cmdPanelOutput.getText());
		else
			insertInputText(inputText.getText() + selection);
	}
	
	public void dispose() {
		Preferences.removePreferenceChangeListener(PreferencePageGeneral.LARGE_ICONS, iconPreferenceChangeListener);
		Preferences.removePreferenceChangeListener(PreferencePageCmd.CMD_FONT, fontPreferenceChangeListener);
		super.dispose();
	}
	
	/** Must invoke this after processing invoked by run button has finished, and we're ready to receive another 'run' notification. */
	public void done() {
		showRunningStop();
	}

	public void selectAll() {
		inputText.selectAll();
	}
	
	private void setupIcons() {
		if (tlitmPrevHistory == null)
			return;		
		tlitmPrevHistory.setImage(IconLoader.loadIcon("previousIcon"));
		tlitmNextHistory.setImage(IconLoader.loadIcon("nextIcon"));
		tlitmClear.setImage(IconLoader.loadIcon("clearIcon"));
		tlitmUndo.setImage(IconLoader.loadIcon("undo"));
		tlitmRedo.setImage(IconLoader.loadIcon("redo"));
		tlitmCut.setImage(IconLoader.loadIcon("cut"));
		tlitmCopy.setImage(IconLoader.loadIcon("copy"));
		tlitmPaste.setImage(IconLoader.loadIcon("paste"));		
		tlitmFindReplace.setImage(IconLoader.loadIcon("edit_find_replace"));
		tlitmLoad.setImage(IconLoader.loadIcon("loadIcon"));
		tlitmLoadInsert.setImage(IconLoader.loadIcon("loadInsertIcon"));
		tlitmGetPath.setImage(IconLoader.loadIcon("pathIcon"));
		tlitmSave.setImage(IconLoader.loadIcon("saveIcon"));
		tlitmSaveHistory.setImage(IconLoader.loadIcon("saveHistoryIcon"));
		tlitmCopyToOutput.setImage(IconLoader.loadIcon("copyToOutputIcon"));
		tlitmWrap.setImage(IconLoader.loadIcon("wrapIcon"));
		tlitmCharacters.setImage(IconLoader.loadIcon("characters"));
	}

	private void setupFont() {
		inputText.setFont(Preferences.getPreferenceFont(getDisplay(), PreferencePageCmd.CMD_FONT));
	}
	
	/** Override to receive notification that the cancel button has been pressed. */
	private void notifyStop() {
		cmdPanelOutput.notifyStop();
	}
	
	/** Override to receive notification that the run button has been pressed.  Must invoke done() when 
	 * ready to receive another notification. */
	private void notifyGo(String text) {
		cmdPanelOutput.go(text, copyInputToOutput);
	}
	
	private void replaceInputText(String newText) {
		inputText.setText(newText);
	}
	
	private void insertInputText(String newText) {
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

	public void setFocused() {
		inputText.setFocus();
	}

	private String loadFileImage(String fname) throws Exception {
		BufferedReader f = new BufferedReader(new FileReader(fname));
		StringBuffer fileImage = new StringBuffer();
		String line;
		while ((line = f.readLine()) != null) {
			if (fileImage.length() > 0)
				fileImage.append('\n');
			fileImage.append(line);
		}
		f.close();
		return fileImage.toString();
	}
	
	public void loadFile(String fname) {
		try {
			String fileImage = loadFileImage(fname);
			replaceInputText(fileImage.toString());
			announcement("Loaded " + fname);
		} catch (Exception ioe) {
			announceError(ioe.toString(), ioe);
		}		
	}
	
	public void loadInsertFile(String fname) {
		try {
			String fileImage = loadFileImage(fname);
			insertInputText(fileImage.toString());
			announcement("Loaded " + fname);
		} catch (Exception ioe) {
			announceError(ioe.toString(), ioe);
		}		
	}

	public void handleError(StringBuffer errorBuffer) {
		ErrorInformation eInfo = parseErrorInformationFrom(errorBuffer.toString());
		if (eInfo != null) {
			int offset = 0;
			try {
				if (eInfo.getLine() > 0) {
					int row = eInfo.getLine() - 1;
					offset = inputText.getOffsetAtLine(row);
					if (eInfo.getColumn() > 0) {
						int outputTabSize = 4;	// should match parserEngine.setTabSize() in org.reldb.rel.v<n>.interpreter.Interpreter
						String inputLine = inputText.getLine(row);
						int characterIndex = Tabs.displayColumnToCharacterIndex(outputTabSize, inputLine, eInfo.getColumn() - 1);
						offset = characterIndex + inputText.getOffsetAtLine(row);
					}
				}
				inputText.setCaretOffset(offset);
				if (eInfo.getBadToken() != null)
					inputText.setSelection(offset, offset + eInfo.getBadToken().length());
				inputText.setFocus();
			} catch (Exception e) {
				System.out.println("CmdPanelInput: Unable to position to line " + eInfo.getLine() + ", column " + eInfo.getColumn());
			}
		} else
			System.out.println("CmdPanelInput: Unable to locate error in " + errorBuffer.toString());
		errorBuffer = null;
	}

	public void setText(String text) {
		inputText.setText(text);
	}

	public String getText() {
		return inputText.getText();
	}

	public void setHistory(Vector<String> history) {
		entryHistory = history;
		currentHistoryItem = entryHistory.size() - 1;
		setupButtons();
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

	protected void notifyHistoryAdded(String historyItem) {}
	
	/** Add a history item. */
	private void addHistoryItem(String s) {
		if (entryHistory.size() > 0 && s.equals(entryHistory.get(entryHistory.size() - 1)))
			return;
		entryHistory.add(s);
		currentHistoryItem = entryHistory.size() - 1;
		setupButtons();
		notifyHistoryAdded(s);
	}

	/** Set up history button status. */
	private void setupButtons() {
		if (tlitmPrevHistory == null)
			return;
		tlitmPrevHistory.setEnabled(currentHistoryItem > 0 && getHistorySize() > 1);
		tlitmNextHistory.setEnabled(currentHistoryItem < getHistorySize() - 1 && getHistorySize() > 1);
	}

	private void run() {
		showRunningStart();
		String text = saveToHistory();
		notifyGo(text);	
	}

	public String saveToHistory() {
		String text = inputText.getText();
		if (text.trim().length() > 0)
			addHistoryItem(text);
		return text;
	}

	private void stop() {
		notifyStop();
	}
	
	private void ensureSaveDialogExists() {
		if (saveDialog == null) {
			saveDialog = new FileDialog(getShell(), SWT.SAVE);
			if (loadDialog != null)
				saveDialog.setFilterPath(loadDialog.getFilterPath());
			else
				saveDialog.setFilterPath(System.getProperty("user.home"));
			saveDialog.setFilterExtensions(new String[] {"*.rel", "*.*"});
			saveDialog.setFilterNames(new String[] {"Rel script", "All Files"});
			saveDialog.setText("Save");
			saveDialog.setOverwrite(true);
		}
	}

	private void ensureSaveHistoryDialogExists() {
		if (saveHistoryDialog == null) {
			saveHistoryDialog = new FileDialog(getShell(), SWT.SAVE);
			saveHistoryDialog.setFilterPath(System.getProperty("user.home"));
			saveHistoryDialog.setFilterExtensions(new String[] {"*.rel", "*.*"});
			saveHistoryDialog.setFilterNames(new String[] {"Rel script", "All Files"});
			saveHistoryDialog.setText("Save Input History");
			saveHistoryDialog.setOverwrite(true);
		}		
	}

	private void ensureLoadDialogExists() {
		if (loadDialog == null) {
			loadDialog = new FileDialog(getShell(), SWT.OPEN);
			if (saveDialog != null)
				loadDialog.setFilterPath(saveDialog.getFilterPath());
			else
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
			loadPathDialog.setFilterExtensions(new String[] {"*.*"});
			loadPathDialog.setFilterNames(new String[] {"All Files"});
			loadPathDialog.setText("Load Path");
		}
	}
	
	private static class ErrorInformation {
		private int line;
		private int column;
		private String badToken;
		ErrorInformation(int line, int column, String badToken) {
			this.line = line;
			this.column = column;
			this.badToken = badToken;
		}
		int getLine() {
			return line;
		}
		int getColumn() {
			return column;
		}
		String getBadToken() {
			return badToken;
		}
		public String toString() {
			String output = "Error in line " + line;
			if (column >= 0)
				output += ", column " + column;
			if (badToken != null)
				output += " near " + badToken;
			return output;
		}
	}
	
	private ErrorInformation parseErrorInformationFrom(String eInfo) {
		try {
			String badToken = null;
			String[] errorPrefix = {
				"ERROR: Encountered ",	
				"ERROR: Lexical error "
			};
			for (String errorEncountered: errorPrefix)
				if (eInfo.startsWith(errorEncountered)) {
					String atLineText = "at line ";
					int atLineTextPosition = eInfo.indexOf(atLineText);
					int lastBadTokenCharPosition = eInfo.lastIndexOf('"', atLineTextPosition);
					if (lastBadTokenCharPosition >= 0)
						badToken = eInfo.substring(errorEncountered.length() + 1, lastBadTokenCharPosition);
				}
			String lineText = "line ";
			int locatorStart = eInfo.toLowerCase().indexOf(lineText);
			if (locatorStart >= 0) {
				int line = 0;
				int column = 0;
				eInfo = eInfo.substring(locatorStart + lineText.length());
				int nonNumberPosition = 0;
				while (nonNumberPosition < eInfo.length() && Character.isDigit(eInfo.charAt(nonNumberPosition)))
					nonNumberPosition++;
				String lineString = eInfo.substring(0, nonNumberPosition);
				try {
					line = Integer.parseInt(lineString);			
				} catch (NumberFormatException nfe) {
					return null;
				}
				int commaPosition = eInfo.indexOf(',');
				if (commaPosition > 0) {
					eInfo = eInfo.substring(commaPosition + 2);
					String columnText = "column ";
					if (eInfo.startsWith(columnText)) {
						eInfo = eInfo.substring(columnText.length());
						int endOfNumber = 0;
						while (endOfNumber < eInfo.length() && Character.isDigit(eInfo.charAt(endOfNumber)))
							endOfNumber++;
						String columnString = "";
						if (endOfNumber > 0 && endOfNumber < eInfo.length())
							columnString = eInfo.substring(0, endOfNumber);
						else
							columnString = eInfo;
						try {
							column = Integer.parseInt(columnString);
						} catch (NumberFormatException nfe) {
							return null;
						}
						String nearText = "near ";
						int nearTextPosition = eInfo.indexOf(nearText, endOfNumber);
						if (nearTextPosition > 0) {
							int lastQuotePosition = eInfo.lastIndexOf('\'');
							badToken = eInfo.substring(nearTextPosition + nearText.length() + 1, lastQuotePosition);
						} else {
							String encounteredText = "Encountered: \"";
							int encounteredTextPosition = eInfo.indexOf(encounteredText);
							if (encounteredTextPosition > 0) {
								int afterEncounteredTextPosition = encounteredTextPosition + encounteredText.length() + 1;
								int lastQuotePosition = eInfo.indexOf('"', afterEncounteredTextPosition);
								badToken = eInfo.substring(afterEncounteredTextPosition - 1, lastQuotePosition);
							}
						}
						return new ErrorInformation(line, column, badToken);
					} else
						return new ErrorInformation(line, -1, badToken);
				} else
					return new ErrorInformation(line, -1, badToken);
			}
		} catch (Throwable t) {
			System.out.println("CmdPanelInput: unable to parse " + eInfo + " due to " + t);
			t.printStackTrace();
		}
		return null;
	}
	
}
