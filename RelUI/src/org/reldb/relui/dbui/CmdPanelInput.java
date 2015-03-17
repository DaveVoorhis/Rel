package org.reldb.relui.dbui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FormLayout;
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
		
		StyledText inputText = new StyledText(this, SWT.BORDER);
		FormData fd_inputText = new FormData();
		fd_inputText.right = new FormAttachment(100);
		fd_inputText.top = new FormAttachment(toolBar);
		
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
			}
		});
		tlitmCopyToOutput.setSelection(true);
		tlitmCopyToOutput.setImage(ResourceManager.getPluginImage("RelUI", "icons/copyToOutputIcon.png"));
		
		ToolItem tlitmWrap = new ToolItem(toolBar, SWT.CHECK);
		tlitmWrap.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		tlitmWrap.setToolTipText("Wrap text");
		tlitmWrap.setSelection(true);
		tlitmWrap.setImage(ResourceManager.getPluginImage("RelUI", "icons/wrapIcon.png"));
		
		new ToolItem(toolBar, SWT.SEPARATOR);
		
		ToolItem tlitmBackup = new ToolItem(toolBar, SWT.NONE | SWT.RIGHT);
		tlitmBackup.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		tlitmBackup.setToolTipText("Make backup");
		tlitmBackup.setImage(ResourceManager.getPluginImage("RelUI", "icons/safeIcon.png"));
		fd_inputText.left = new FormAttachment(0);
		inputText.setLayoutData(fd_inputText);
		
		CmdPanelBottom cmdPanelBottom = new CmdPanelBottom(this, SWT.NONE);
		FormData fd_cmdPanelBottom = new FormData();
		fd_cmdPanelBottom.left = new FormAttachment(0);
		fd_cmdPanelBottom.right = new FormAttachment(100);
		fd_cmdPanelBottom.bottom = new FormAttachment(100);
		fd_inputText.bottom = new FormAttachment(cmdPanelBottom);
		cmdPanelBottom.setLayoutData(fd_cmdPanelBottom);
	}
}
