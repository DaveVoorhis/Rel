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
import org.reldb.relui.dbui.preferences.PreferenceChangeAdapter;
import org.reldb.relui.dbui.preferences.PreferenceChangeEvent;
import org.reldb.relui.dbui.preferences.PreferenceChangeListener;
import org.reldb.relui.dbui.preferences.PreferencePageGeneral;

public class DbTabContentCmd extends Composite {

	private ToolItem tlitmBackup = null;
	private ToolItem clearOutputBtn = null;
	private ToolItem saveOutputAsHTMLBtn = null;
	private ToolItem saveOutputAsTextBtn = null;
	private ToolItem copyOutputToInputBtn = null;
	private ToolItem enhancedOutputToggle = null;
	private ToolItem showOkToggle = null;
	private ToolItem autoclearToggle = null;
	private ToolItem headingToggle = null;
	private ToolItem headingTypesToggle = null;
	
	private CmdPanel cmdPanel = null;
    
    private PreferenceChangeListener preferenceChangeListener;

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
				
		tlitmBackup = new ToolItem(toolBar, SWT.NONE);
		tlitmBackup.setToolTipText("Make backup");
		tlitmBackup.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				parentTab.makeBackup();
			}
		});
				
		clearOutputBtn = new ToolItem(toolBar, SWT.PUSH);
		clearOutputBtn.setToolTipText("Clear");
		clearOutputBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cmdPanel.clearOutput();
			}			
		});		
				
		saveOutputAsHTMLBtn = new ToolItem(toolBar, SWT.PUSH);
		saveOutputAsHTMLBtn.setToolTipText("Save as HTML");
		saveOutputAsHTMLBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cmdPanel.saveOutputAsHtml();
			}
		});

		saveOutputAsTextBtn = new ToolItem(toolBar, SWT.PUSH);
		saveOutputAsTextBtn.setToolTipText("Save as text");
		saveOutputAsTextBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cmdPanel.saveOutputAsText();
			}
		});
		
		copyOutputToInputBtn = new ToolItem(toolBar, SWT.PUSH);
		copyOutputToInputBtn.setToolTipText("Copy output to input");
		copyOutputToInputBtn.setEnabled(!cmdPanel.getEnhancedOutput());
		copyOutputToInputBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cmdPanel.copyOutputToInput();
			}
		});

		new ToolItem(toolBar, SWT.SEPARATOR);
		
		enhancedOutputToggle = new ToolItem(toolBar, SWT.CHECK);
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
		
		showOkToggle = new ToolItem(toolBar, SWT.CHECK);
		showOkToggle.setToolTipText("Write 'Ok.' after execution");
		showOkToggle.setSelection(cmdPanel.getShowOk());
		showOkToggle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cmdPanel.setShowOk(showOkToggle.getSelection());
			}
		});

		autoclearToggle = new ToolItem(toolBar, SWT.CHECK);
		autoclearToggle.setToolTipText("Automatically clear output");
		autoclearToggle.setSelection(cmdPanel.getAutoclear());
		autoclearToggle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cmdPanel.setAutoclear(autoclearToggle.getSelection());
			}
		});
		
		headingToggle = new ToolItem(toolBar, SWT.CHECK);
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
		headingTypesToggle.setToolTipText("Suppress attribute types in relation headings");
		headingTypesToggle.setEnabled(headingToggle.getSelection() && enhancedOutputToggle.getSelection());
		headingTypesToggle.setSelection(cmdPanel.getHeadingTypesVisible() && headingTypesToggle.getEnabled());	
		headingTypesToggle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cmdPanel.setHeadingTypesVisible(headingTypesToggle.getSelection());
			}
		});
		
		setupIcons();
		
		preferenceChangeListener = new PreferenceChangeAdapter("DbTabContentCmd") {
			@Override
			public void preferenceChange(PreferenceChangeEvent evt) {
				setupIcons();
			}
		};		
		Preferences.addPreferenceChangeListener(PreferencePageGeneral.DBL_ICONS, preferenceChangeListener);
	}

	public void dispose() {
		Preferences.removePreferenceChangeListener(PreferencePageGeneral.DBL_ICONS, preferenceChangeListener);
		cmdPanel.dispose();
		super.dispose();
	}

	private void setupIcons() {
		tlitmBackup.setImage(IconLoader.loadIcon("safeIcon"));		
		clearOutputBtn.setImage(IconLoader.loadIcon("clearIcon"));
		saveOutputAsHTMLBtn.setImage(IconLoader.loadIcon("saveHTMLIcon"));
		saveOutputAsTextBtn.setImage(IconLoader.loadIcon("saveTextIcon"));
		copyOutputToInputBtn.setImage(IconLoader.loadIcon("copyToInputIcon"));
		enhancedOutputToggle.setImage(IconLoader.loadIcon("enhancedIcon"));
		showOkToggle.setImage(IconLoader.loadIcon("showOkIcon"));
		autoclearToggle.setImage(IconLoader.loadIcon("autoclearIcon"));
		headingToggle.setImage(IconLoader.loadIcon("headingIcon"));
		headingTypesToggle.setImage(IconLoader.loadIcon("typeSuppressIcon"));
	}

	public void notifyIconSizeChange() {
		setupIcons();
	}
	
}
