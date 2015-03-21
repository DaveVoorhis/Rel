package org.reldb.relui.dbui;

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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

public class CmdPanelInput extends Composite {
	
	private CmdPanelBottom cmdPanelBottom;
	private StyledText inputText;
	
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
		
		ToolItem tlitmPrevHistory = new ToolItem(toolBar, SWT.NONE);
		tlitmPrevHistory.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		tlitmPrevHistory.setToolTipText("Load previous entry");
		tlitmPrevHistory.setText("<");
		
		ToolItem tlitmNextHistory = new ToolItem(toolBar, SWT.NONE);
		tlitmNextHistory.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		tlitmNextHistory.setToolTipText("Reload next entry");
		tlitmNextHistory.setText(">");
		
		ToolItem tlitmClear = new ToolItem(toolBar, SWT.NONE);
		tlitmClear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				inputText.setText("");
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
				showRunningStart();
				notifyGo(getInputText());
			}
		};
		fd_inputText.bottom = new FormAttachment(cmdPanelBottom, 191);
		FormData fd_cmdPanelBottom = new FormData();
		fd_cmdPanelBottom.left = new FormAttachment(0);
		fd_cmdPanelBottom.right = new FormAttachment(100);
		fd_cmdPanelBottom.bottom = new FormAttachment(100);
		fd_inputText.bottom = new FormAttachment(cmdPanelBottom);
		cmdPanelBottom.setLayoutData(fd_cmdPanelBottom);
	}
	
}
