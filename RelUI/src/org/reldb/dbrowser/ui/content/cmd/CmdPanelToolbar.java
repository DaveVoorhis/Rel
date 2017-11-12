package org.reldb.dbrowser.ui.content.cmd;

import org.eclipse.swt.SWT;
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
		
		addAdditionalItemsBefore(this);

		clearOutputBtn = addItem("Clear", "clearIcon", SWT.PUSH);
		clearOutputBtn.addListener(SWT.Selection, e -> cmdPanel.clearOutput());

		saveOutputAsHTMLBtn = addItem("Save as HTML", "saveHTMLIcon", SWT.PUSH);
		saveOutputAsHTMLBtn.addListener(SWT.Selection, e -> cmdPanel.saveOutputAsHtml());

		saveOutputAsTextBtn = addItem("Save as text", "saveTextIcon", SWT.PUSH);
		saveOutputAsTextBtn.addListener(SWT.Selection, e -> cmdPanel.saveOutputAsText());

		addSeparator();

		enhancedOutputToggle = addItem("Display enhanced output", "enhancedIcon", SWT.CHECK);
		enhancedOutputToggle.setSelection(cmdPanel.getEnhancedOutput());
		enhancedOutputToggle.addListener(SWT.Selection, e -> {
			cmdPanel.setEnhancedOutput(enhancedOutputToggle.getSelection());
			headingToggle.setEnabled(enhancedOutputToggle.getSelection());
			headingToggle.setSelection(headingToggle.getEnabled()
					&& cmdPanel.getHeadingVisible());
			headingTypesToggle.setEnabled(enhancedOutputToggle
					.getSelection());
			headingTypesToggle.setSelection(headingTypesToggle.getEnabled()
					&& cmdPanel.getHeadingTypesVisible());
		});

		if (!cmdPanel.isForEvaluationOnly()) {
			showOkToggle = addItem("Write 'Ok.' after execution", "showOkIcon", SWT.CHECK);
			showOkToggle.setSelection(cmdPanel.getShowOk());
			showOkToggle.addListener(SWT.Selection, e -> cmdPanel.setShowOk(showOkToggle.getSelection()));
			
			autoclearToggle = addItem("Automatically clear output", "autoclearIcon", SWT.CHECK);
			autoclearToggle.setSelection(cmdPanel.getAutoclear());
			autoclearToggle.addListener(SWT.Selection, e -> cmdPanel.setAutoclear(autoclearToggle.getSelection()));
		}

		headingToggle = addItem("Show relation headings", "headingIcon", SWT.CHECK);
		headingToggle.setEnabled(enhancedOutputToggle.getSelection());
		headingToggle.setSelection(cmdPanel.getHeadingVisible()
				&& headingToggle.getEnabled());
		headingToggle.addListener(SWT.Selection, e -> {
			headingTypesToggle.setEnabled(headingToggle.getSelection());
			headingTypesToggle.setSelection(headingTypesToggle.getEnabled()
					&& cmdPanel.getHeadingTypesVisible());
			cmdPanel.setHeadingVisible(headingToggle.getSelection());
		});
		
		headingTypesToggle = addItem("Suppress attribute types in relation headings", "typeSuppressIcon", SWT.CHECK);
		headingTypesToggle.setEnabled(headingToggle.getSelection()
				&& enhancedOutputToggle.getSelection());
		headingTypesToggle.setSelection(cmdPanel.getHeadingTypesVisible()
				&& headingTypesToggle.getEnabled());
		headingTypesToggle.addListener(SWT.Selection, e -> cmdPanel.setHeadingTypesVisible(headingTypesToggle.getSelection()));
		
		addAdditionalItemsAfter(this);
	}
	
	/** Override to add additional toolbar items before the default items. */
	protected void addAdditionalItemsBefore(CmdPanelToolbar toolbar) {}

	/** Override to add additional toolbar items after the default items. */
	protected void addAdditionalItemsAfter(CmdPanelToolbar toolbar) {}	
}
