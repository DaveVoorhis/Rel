package org.reldb.dbrowser.ui.content.cmd;

import org.eclipse.dbrowser.commands.CommandActivator;
import org.eclipse.dbrowser.commands.Commands;
import org.eclipse.dbrowser.commands.ManagedToolbar;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class CmdPanelToolbar extends ManagedToolbar {

	private CommandActivator clearOutputBtn;
	private CommandActivator saveOutputAsHTMLBtn;
	private CommandActivator saveOutputAsTextBtn;
	private CommandActivator enhancedOutputToggle;
	private CommandActivator showOkToggle;
	private CommandActivator autoclearToggle;
	private CommandActivator headingToggle;
	private CommandActivator headingTypesToggle;
	
	public CmdPanelToolbar(Composite parent, CmdPanelOutput cmdPanel) {
		super(parent);
		
		addAdditionalItemsBefore(this);

		clearOutputBtn = addItem(Commands.Do.ClearOutput, "Clear", "clearIcon", SWT.PUSH);
		clearOutputBtn.addListener(SWT.Selection, e -> cmdPanel.clearOutput());

		saveOutputAsHTMLBtn = addItem(Commands.Do.SaveAsHTML, "Save as HTML", "saveHTMLIcon", SWT.PUSH);
		saveOutputAsHTMLBtn.addListener(SWT.Selection, e -> cmdPanel.saveOutputAsHtml());

		saveOutputAsTextBtn = addItem(Commands.Do.SaveAsText, "Save as text", "saveTextIcon", SWT.PUSH);
		saveOutputAsTextBtn.addListener(SWT.Selection, e -> cmdPanel.saveOutputAsText());

		addSeparator();

		enhancedOutputToggle = addItem(Commands.Do.DisplayEnhancedOutput, "Display enhanced output", "enhancedIcon", SWT.CHECK);
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
			showOkToggle = addItem(Commands.Do.DisplayOk, "Write 'Ok.' after execution", "showOkIcon", SWT.CHECK);
			showOkToggle.setSelection(cmdPanel.getShowOk());
			showOkToggle.addListener(SWT.Selection, e -> cmdPanel.setShowOk(showOkToggle.getSelection()));
			
			autoclearToggle = addItem(Commands.Do.DisplayAutoClear, "Automatically clear output", "autoclearIcon", SWT.CHECK);
			autoclearToggle.setSelection(cmdPanel.getAutoclear());
			autoclearToggle.addListener(SWT.Selection, e -> cmdPanel.setAutoclear(autoclearToggle.getSelection()));
		}

		headingToggle = addItem(Commands.Do.ShowRelationHeadings, "Show relation headings", "headingIcon", SWT.CHECK);
		headingToggle.setEnabled(enhancedOutputToggle.getSelection());
		headingToggle.setSelection(cmdPanel.getHeadingVisible()
				&& headingToggle.getEnabled());
		headingToggle.addListener(SWT.Selection, e -> {
			headingTypesToggle.setEnabled(headingToggle.getSelection());
			headingTypesToggle.setSelection(headingTypesToggle.getEnabled()
					&& cmdPanel.getHeadingTypesVisible());
			cmdPanel.setHeadingVisible(headingToggle.getSelection());
		});
		
		headingTypesToggle = addItem(Commands.Do.ShowRelationHeadingAttributeTypes, "Display attribute types in relation headings", "typeSuppressIcon", SWT.CHECK);
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
