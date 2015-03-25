package org.reldb.relui.dbui;

import java.util.Vector;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class CmdPanelInput extends Composite {
	
	private CmdPanelBottom cmdPanelBottom;
	private StyledText inputText;

	private ToolItem tlitmPrevHistory;	
	private ToolItem tlitmNextHistory;
	
	private Vector<String> entryHistory = new Vector<String>();
	private int currentHistoryItem = 0;
	
	private void showRunningStart() {
		cmdPanelBottom.startBusyIndicator();
		cmdPanelBottom.setEnabledRunButton(false);		
	}
	
	private void showRunningStop() {
		cmdPanelBottom.setEnabledRunButton(true);
		cmdPanelBottom.stopBusyIndicator();
	}

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

	public void setInputText(String string) {
		inputText.setText(string);
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

		tlitmPrevHistory = new ToolItem(toolBar, SWT.NONE);
		tlitmPrevHistory.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				inputText.setText(getPreviousHistoryItem());
				inputText.setSelection(0, inputText.getText().length());
				inputText.setFocus();
			}
		});
		tlitmPrevHistory.setToolTipText("Load previous entry");
		tlitmPrevHistory.setText("<");
		
		tlitmNextHistory = new ToolItem(toolBar, SWT.NONE);
		tlitmNextHistory.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				inputText.setText(getNextHistoryItem());
				inputText.setSelection(0, inputText.getText().length());
				inputText.setFocus();
			}
		});
		tlitmNextHistory.setToolTipText("Reload next entry");
		tlitmNextHistory.setText(">");
		
		ToolItem tlitmClear = new ToolItem(toolBar, SWT.NONE);
		tlitmClear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				inputText.setText("");
				inputText.setFocus();
			}
		});
		tlitmClear.setToolTipText("Clear");
		tlitmClear.setImage(ResourceManager.getPluginImage("RelUI", "icons/clearIcon.png"));
		
		ToolItem tlitmLoad = new ToolItem(toolBar, SWT.NONE);
		tlitmLoad.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		tlitmLoad.setToolTipText("Load file");
		tlitmLoad.setImage(ResourceManager.getPluginImage("RelUI", "icons/loadIcon.png"));
		
		ToolItem tlitmGetPath = new ToolItem(toolBar, SWT.NONE);
		tlitmGetPath.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		tlitmGetPath.setToolTipText("Get file path");
		tlitmGetPath.setImage(ResourceManager.getPluginImage("RelUI", "icons/pathIcon.png"));
		
		ToolItem tlitmSave = new ToolItem(toolBar, SWT.NONE);
		tlitmSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		tlitmSave.setToolTipText("Save");
		tlitmSave.setImage(ResourceManager.getPluginImage("RelUI", "icons/saveIcon.png"));
		
		ToolItem tlitmSaveHistory = new ToolItem(toolBar, SWT.NONE);
		tlitmSaveHistory.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		tlitmSaveHistory.setToolTipText("Save history");
		tlitmSaveHistory.setImage(ResourceManager.getPluginImage("RelUI", "icons/saveHistoryIcon.png"));
		
		ToolItem tlitmCopyToOutput = new ToolItem(toolBar, SWT.CHECK);
		tlitmCopyToOutput.setToolTipText("Copy input to output");
		tlitmCopyToOutput.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setCopyInputToOutput(tlitmCopyToOutput.getSelection());
			}
		});
		tlitmCopyToOutput.setSelection(true);
		tlitmCopyToOutput.setImage(ResourceManager.getPluginImage("RelUI", "icons/copyToOutputIcon.png"));
		
		ToolItem tlitmWrap = new ToolItem(toolBar, SWT.CHECK);
		tlitmWrap.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				inputText.setWordWrap(tlitmWrap.getSelection());
			}
		});
		tlitmWrap.setToolTipText("Wrap text");
		tlitmWrap.setSelection(true);
		tlitmWrap.setImage(ResourceManager.getPluginImage("RelUI", "icons/wrapIcon.png"));
		
		cmdPanelBottom = new CmdPanelBottom(this, SWT.NONE) {
			@Override
			public void go() {
				run();
			}
		};
		fd_inputText.bottom = new FormAttachment(cmdPanelBottom, 191);
		FormData fd_cmdPanelBottom = new FormData();
		fd_cmdPanelBottom.left = new FormAttachment(0);
		fd_cmdPanelBottom.right = new FormAttachment(100);
		fd_cmdPanelBottom.bottom = new FormAttachment(100);
		fd_inputText.bottom = new FormAttachment(cmdPanelBottom);
		cmdPanelBottom.setLayoutData(fd_cmdPanelBottom);
		
		setButtons();
	}
	
}
