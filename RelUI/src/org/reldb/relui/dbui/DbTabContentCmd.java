package org.reldb.relui.dbui;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.ResourceManager;

public class DbTabContentCmd extends Composite {

	private ToolItem headingToggle = null;
	private ToolItem headingTypesToggle = null;
	private CmdPanel cmdPanel = null;

	public DbTabContentCmd(DbTab parentTab, Composite contentParent) throws NumberFormatException, ClassNotFoundException, IOException {
		super(contentParent, SWT.None);
		setLayout(new FormLayout());
				
		ToolBar toolBar = new ToolBar(this, SWT.None);
		FormData fd_toolBar = new FormData();
		fd_toolBar.left = new FormAttachment(0);
		fd_toolBar.top = new FormAttachment(0);
		fd_toolBar.right = new FormAttachment(100);
		toolBar.setLayoutData(fd_toolBar);

		cmdPanel = new CmdPanel(parentTab, this, SWT.None);
		FormData fd_composite = new FormData();
		fd_composite.left = new FormAttachment(0);
		fd_composite.top = new FormAttachment(toolBar);
		fd_composite.right = new FormAttachment(100);
		fd_composite.bottom = new FormAttachment(100);
		cmdPanel.setLayoutData(fd_composite);
				
		ToolItem tlitmBackup = new ToolItem(toolBar, SWT.NONE);
		tlitmBackup.setToolTipText("Make backup");
		tlitmBackup.setImage(ResourceManager.getPluginImage("RelUI", "icons/safeIcon.png"));
		tlitmBackup.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				parentTab.makeBackup();
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
		copyOutputToInputBtn.setEnabled(!cmdPanel.getEnhancedOutput());
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
		enhancedOutputToggle.setSelection(cmdPanel.getEnhancedOutput());
		enhancedOutputToggle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cmdPanel.setEnhancedOutput(enhancedOutputToggle.getSelection());
				copyOutputToInputBtn.setEnabled(!enhancedOutputToggle.getSelection());
				headingToggle.setEnabled(enhancedOutputToggle.getSelection());
				headingToggle.setSelection(headingToggle.getEnabled() && cmdPanel.getHeadingVisible());
				headingTypesToggle.setEnabled(enhancedOutputToggle.getSelection());
				headingTypesToggle.setSelection(headingTypesToggle.getEnabled() && cmdPanel.getHeadingTypesVisible());
			}
		});
		
		ToolItem showOkToggle = new ToolItem(toolBar, SWT.CHECK);
		showOkToggle.setImage(ResourceManager.getPluginImage("RelUI", "icons/showOkIcon.png"));
		showOkToggle.setToolTipText("Write 'Ok.' after execution");
		showOkToggle.setSelection(cmdPanel.getShowOk());
		showOkToggle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cmdPanel.setShowOk(showOkToggle.getSelection());
			}
		});

		ToolItem autoclearToggle = new ToolItem(toolBar, SWT.CHECK);
		autoclearToggle.setImage(ResourceManager.getPluginImage("RelUI", "icons/autoclearIcon.png"));
		autoclearToggle.setToolTipText("Automatically clear output");
		autoclearToggle.setSelection(cmdPanel.getAutoclear());
		autoclearToggle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cmdPanel.setAutoclear(autoclearToggle.getSelection());
			}
		});
		
		headingToggle = new ToolItem(toolBar, SWT.CHECK);
		headingToggle.setImage(ResourceManager.getPluginImage("RelUI", "icons/headingIcon.png"));
		headingToggle.setToolTipText("Show relation headings");
		headingToggle.setEnabled(enhancedOutputToggle.getSelection());
		headingToggle.setSelection(cmdPanel.getHeadingVisible() && headingToggle.getEnabled());
		headingToggle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				headingTypesToggle.setEnabled(headingToggle.getSelection());
				headingTypesToggle.setSelection(headingTypesToggle.getEnabled() && cmdPanel.getHeadingTypesVisible());
				cmdPanel.setHeadingVisible(headingToggle.getSelection());
			}
		});
		
		headingTypesToggle = new ToolItem(toolBar, SWT.CHECK);
		headingTypesToggle.setImage(ResourceManager.getPluginImage("RelUI", "icons/typeSuppressIcon.png"));
		headingTypesToggle.setToolTipText("Suppress attribute types in relation headings");
		headingTypesToggle.setEnabled(headingToggle.getSelection() && enhancedOutputToggle.getSelection());
		headingTypesToggle.setSelection(cmdPanel.getHeadingTypesVisible() && headingTypesToggle.getEnabled());	
		headingTypesToggle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cmdPanel.setHeadingTypesVisible(headingTypesToggle.getSelection());
			}
		});
	}

	public void dispose() {
		cmdPanel.dispose();
		super.dispose();
	}
	
}
