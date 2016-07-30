package org.reldb.dbrowser.ui.content.cmd;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolItem;
import org.reldb.dbrowser.ui.ManagedToolbar;

public class CmdPanelToolbar extends ManagedToolbar {

	private ToolItem clearOutputBtn;
	private ToolItem saveOutputAsHTMLBtn;
	private ToolItem saveOutputAsTextBtn;
	private ToolItem enhancedOutputToggle;
	private ToolItem showOkToggle;
	private ToolItem autoclearToggle;
	private ToolItem headingToggle;
	private ToolItem headingTypesToggle;
	
	public CmdPanelToolbar(Composite parent, CmdPanelOutput cmdPanel) {
		super(parent);
		
		addAdditionalItemsBefore();

		clearOutputBtn = addItem("Clear", "clearIcon", SWT.PUSH);
		clearOutputBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cmdPanel.clearOutput();
			}
		});

		saveOutputAsHTMLBtn = addItem("Save as HTML", "saveHTMLIcon", SWT.PUSH);
		saveOutputAsHTMLBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cmdPanel.saveOutputAsHtml();
			}
		});

		saveOutputAsTextBtn = addItem("Save as text", "saveTextIcon", SWT.PUSH);
		saveOutputAsTextBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cmdPanel.saveOutputAsText();
			}
		});

		addSeparator();

		enhancedOutputToggle = addItem("Display enhanced output", "enhancedIcon", SWT.CHECK);
		enhancedOutputToggle.setSelection(cmdPanel.getEnhancedOutput());
		enhancedOutputToggle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cmdPanel.setEnhancedOutput(enhancedOutputToggle.getSelection());
				headingToggle.setEnabled(enhancedOutputToggle.getSelection());
				headingToggle.setSelection(headingToggle.getEnabled()
						&& cmdPanel.getHeadingVisible());
				headingTypesToggle.setEnabled(enhancedOutputToggle
						.getSelection());
				headingTypesToggle.setSelection(headingTypesToggle.getEnabled()
						&& cmdPanel.getHeadingTypesVisible());
			}
		});

		showOkToggle = addItem("Write 'Ok.' after execution", "showOkIcon", SWT.CHECK);
		showOkToggle.setSelection(cmdPanel.getShowOk());
		showOkToggle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cmdPanel.setShowOk(showOkToggle.getSelection());
			}
		});

		autoclearToggle = addItem("Automatically clear output", "autoclearIcon", SWT.CHECK);
		autoclearToggle.setSelection(cmdPanel.getAutoclear());
		autoclearToggle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cmdPanel.setAutoclear(autoclearToggle.getSelection());
			}
		});

		headingToggle = addItem("Show relation headings", "headingIcon", SWT.CHECK);
		headingToggle.setEnabled(enhancedOutputToggle.getSelection());
		headingToggle.setSelection(cmdPanel.getHeadingVisible()
				&& headingToggle.getEnabled());
		headingToggle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				headingTypesToggle.setEnabled(headingToggle.getSelection());
				headingTypesToggle.setSelection(headingTypesToggle.getEnabled()
						&& cmdPanel.getHeadingTypesVisible());
				cmdPanel.setHeadingVisible(headingToggle.getSelection());
			}
		});
		
		headingTypesToggle = addItem("Suppress attribute types in relation headings", "typeSuppressIcon", SWT.CHECK);
		headingTypesToggle.setEnabled(headingToggle.getSelection()
				&& enhancedOutputToggle.getSelection());
		headingTypesToggle.setSelection(cmdPanel.getHeadingTypesVisible()
				&& headingTypesToggle.getEnabled());
		headingTypesToggle.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				cmdPanel.setHeadingTypesVisible(headingTypesToggle
						.getSelection());
			}
		});
		
		addAdditionalItemsAfter();
	}
	
	/** Override to add additional toolbar items before the default items. */
	protected void addAdditionalItemsBefore() {}

	/** Override to add additional toolbar items after the default items. */
	protected void addAdditionalItemsAfter() {}	
}
