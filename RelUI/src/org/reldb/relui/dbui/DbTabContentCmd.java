package org.reldb.relui.dbui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.ResourceManager;

public class DbTabContentCmd extends DbTabContent {

	private ToolItem headingTypesToggle = null;
	private CmdPanel cmdPanel = null;

	public DbTabContentCmd(DbTab parentTab) {
		super(parentTab);
	}
	
	@Override
	public Control getContent(Composite contentParent) {
		if (cmdPanel == null)
			cmdPanel = new CmdPanel(getDbTab(), contentParent, SWT.None);
		return cmdPanel;
	}

	@Override
	public void getToolBarItems(ToolBar toolBar) {
		
		ToolItem tlitmBackup = new ToolItem(toolBar, SWT.NONE);
		tlitmBackup.setToolTipText("Make backup");
		tlitmBackup.setImage(ResourceManager.getPluginImage("RelUI", "icons/safeIcon.png"));
		tlitmBackup.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getDbTab().makeBackup();
			}
		});
		
		new ToolItem(toolBar, SWT.SEPARATOR);
				
		ToolItem clearOutputBtn = new ToolItem(toolBar, SWT.PUSH);
		clearOutputBtn.setImage(ResourceManager.getPluginImage("RelUI", "icons/clearIcon.png"));
		clearOutputBtn.setToolTipText("Clear");
		clearOutputBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cmdPanel.clearOutput();
			}			
		});		

		ToolItem saveOutputAsHTMLBtn = new ToolItem(toolBar, SWT.PUSH);
		saveOutputAsHTMLBtn.setImage(ResourceManager.getPluginImage("RelUI", "icons/saveHTMLIcon.png"));
		saveOutputAsHTMLBtn.setToolTipText("Save as HTML");
		saveOutputAsHTMLBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cmdPanel.saveOutputAsHtml();
			}
		});

		ToolItem saveOutputAsTextBtn = new ToolItem(toolBar, SWT.PUSH);
		saveOutputAsTextBtn.setImage(ResourceManager.getPluginImage("RelUI", "icons/saveTextIcon.png"));
		saveOutputAsTextBtn.setToolTipText("Save as text");
		saveOutputAsTextBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cmdPanel.saveOutputAsText();
			}
		});
		
		ToolItem copyOutputToInputBtn = new ToolItem(toolBar, SWT.PUSH);
		copyOutputToInputBtn.setImage(ResourceManager.getPluginImage("RelUI", "icons/copyToInputIcon.png"));
		copyOutputToInputBtn.setToolTipText("Copy output to input");
		copyOutputToInputBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cmdPanel.copyOutputToInput();
			}
		});

		new ToolItem(toolBar, SWT.SEPARATOR);
		
		ToolItem enhancedOutputToggle = new ToolItem(toolBar, SWT.CHECK);
		enhancedOutputToggle.setImage(ResourceManager.getPluginImage("RelUI", "icons/enhancedIcon.png"));
		enhancedOutputToggle.setToolTipText("Display enhanced output");
		enhancedOutputToggle.setSelection(true);
		enhancedOutputToggle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cmdPanel.setEnhancedOutput(enhancedOutputToggle.getSelection());
			}
		});
		
		ToolItem showOkToggle = new ToolItem(toolBar, SWT.CHECK);
		showOkToggle.setImage(ResourceManager.getPluginImage("RelUI", "icons/showOkIcon.png"));
		showOkToggle.setToolTipText("Write 'Ok.' after execution");
		showOkToggle.setSelection(true);
		showOkToggle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cmdPanel.setShowOk(showOkToggle.getSelection());
			}
		});

		ToolItem autoclearToggle = new ToolItem(toolBar, SWT.CHECK);
		autoclearToggle.setImage(ResourceManager.getPluginImage("RelUI", "icons/autoclearIcon.png"));
		autoclearToggle.setToolTipText("Automatically clear output");
		autoclearToggle.setSelection(true);
		autoclearToggle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cmdPanel.setAutoclear(autoclearToggle.getSelection());
			}
		});
		
		ToolItem headingToggle = new ToolItem(toolBar, SWT.CHECK);
		headingToggle.setImage(ResourceManager.getPluginImage("RelUI", "icons/headingIcon.png"));
		headingToggle.setToolTipText("Show relation headings");
		headingToggle.setSelection(true);
		headingToggle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				headingTypesToggle.setEnabled(headingToggle.getSelection());
				cmdPanel.setHeadingVisible(headingToggle.getSelection());
			}
		});
		
		headingTypesToggle = new ToolItem(toolBar, SWT.CHECK);
		headingTypesToggle.setImage(ResourceManager.getPluginImage("RelUI", "icons/typeSuppressIcon.png"));
		headingTypesToggle.setToolTipText("Suppress attribute types in relation headings");
		headingTypesToggle.setSelection(false);	
		headingTypesToggle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cmdPanel.setHeadingTypesVisible(headingTypesToggle.getSelection());
			}
		});
	}

}
