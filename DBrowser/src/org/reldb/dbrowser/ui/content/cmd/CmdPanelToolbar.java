package org.reldb.dbrowser.ui.content.cmd;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.reldb.dbrowser.commands.CommandActivator;
import org.reldb.dbrowser.commands.Commands;
import org.reldb.dbrowser.commands.ManagedToolbar;

public class CmdPanelToolbar extends ManagedToolbar {

	private CommandActivator enhancedOutputToggle;
	private CommandActivator showOkToggle;
	private CommandActivator autoclearToggle;
	private CommandActivator headingToggle;
	private CommandActivator headingTypesToggle;
	
	public CmdPanelToolbar(Composite parent, CmdPanelOutput cmdPanel) {
		super(parent, "Output");
		
		addAdditionalItemsBefore(this);

		new CommandActivator(Commands.Do.ClearOutput, this, "clearIcon", SWT.PUSH, "Clear", e -> cmdPanel.clearOutput());
		new CommandActivator(Commands.Do.SaveAsHTML, this, "saveHTMLIcon", SWT.PUSH, "Save as HTML", e -> cmdPanel.saveOutputAsHtml());
		new CommandActivator(Commands.Do.SaveAsText, this, "saveTextIcon", SWT.PUSH, "Save as text", e -> cmdPanel.saveOutputAsText());

		addSeparator();

		enhancedOutputToggle = new CommandActivator(Commands.Do.DisplayEnhancedOutput, this, "enhancedIcon", SWT.CHECK, "Display enhanced output", e -> {
			cmdPanel.setEnhancedOutput(enhancedOutputToggle.getSelection());
			headingToggle.setEnabled(enhancedOutputToggle.getSelection());
			headingToggle.setSelection(headingToggle.getEnabled() && cmdPanel.getHeadingVisible());
			headingTypesToggle.setEnabled(enhancedOutputToggle.getSelection());
			headingTypesToggle.setSelection(headingTypesToggle.getEnabled() && cmdPanel.getHeadingTypesVisible());
		});
		enhancedOutputToggle.setSelection(cmdPanel.getEnhancedOutput());

		if (!cmdPanel.isForEvaluationOnly()) {
			showOkToggle = new CommandActivator(Commands.Do.DisplayOk, this, "showOkIcon", SWT.CHECK, "Write 'Ok.' after execution", e -> cmdPanel.setShowOk(showOkToggle.getSelection()));
			showOkToggle.setSelection(cmdPanel.getShowOk());
			
			autoclearToggle = new CommandActivator(Commands.Do.DisplayAutoClear, this, "autoclearIcon", SWT.CHECK, "Automatically clear output",  e -> cmdPanel.setAutoclear(autoclearToggle.getSelection()));
			autoclearToggle.setSelection(cmdPanel.getAutoclear());
		}

		headingToggle = new CommandActivator(Commands.Do.ShowRelationHeadings, this, "headingIcon", SWT.CHECK, "Show relation headings", e -> {
			headingTypesToggle.setEnabled(headingToggle.getSelection());
			headingTypesToggle.setSelection(headingTypesToggle.getEnabled() && cmdPanel.getHeadingTypesVisible());
			cmdPanel.setHeadingVisible(headingToggle.getSelection());
		});
		headingToggle.setEnabled(enhancedOutputToggle.getSelection());
		headingToggle.setSelection(cmdPanel.getHeadingVisible() && headingToggle.getEnabled());
		
		headingTypesToggle = new CommandActivator(Commands.Do.ShowRelationHeadingAttributeTypes, this, "typeSuppressIcon", SWT.CHECK, "Display attribute types in relation headings", e -> cmdPanel.setHeadingTypesVisible(headingTypesToggle.getSelection()));
		headingTypesToggle.setEnabled(headingToggle.getSelection() && enhancedOutputToggle.getSelection());
		headingTypesToggle.setSelection(cmdPanel.getHeadingTypesVisible() && headingTypesToggle.getEnabled());
		
		addAdditionalItemsAfter(this);
	}
	
	/** Override to add additional toolbar items before the default items. */
	protected void addAdditionalItemsBefore(CmdPanelToolbar toolbar) {}

	/** Override to add additional toolbar items after the default items. */
	protected void addAdditionalItemsAfter(CmdPanelToolbar toolbar) {}
}
