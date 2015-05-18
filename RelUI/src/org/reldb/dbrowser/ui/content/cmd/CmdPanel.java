package org.reldb.dbrowser.ui.content.cmd;

import java.io.IOException;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.reldb.dbrowser.ui.DbTab;
import org.reldb.rel.exceptions.DatabaseFormatVersionException;

public class CmdPanel extends Composite {

	private CmdPanelOutput cmdPanelOutput;
	private CmdPanelInput cmdPanelInput;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws NumberFormatException 
	 * @throws DatabaseFormatVersionException 
	 */
	public CmdPanel(DbTab dbTab, Composite parent, int style) throws NumberFormatException, ClassNotFoundException, IOException, DatabaseFormatVersionException {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		SashForm sashForm = new SashForm(this, SWT.VERTICAL);

		cmdPanelOutput = new CmdPanelOutput(sashForm, dbTab, SWT.NONE);
		cmdPanelInput = new CmdPanelInput(sashForm, cmdPanelOutput, SWT.NONE);
		
		sashForm.setWeights(new int[] {2, 1});
	}
	
	public void dispose() {
		cmdPanelInput.dispose();
		cmdPanelOutput.dispose();
		super.dispose();
	}

	public void redisplayed() {
		cmdPanelInput.getInputTextWidget().setFocus();
	}

	public void load(String fname) {
		cmdPanelInput.loadFile(fname);
	}

	public boolean getHeadingTypesVisible() {
		return cmdPanelOutput.getHeadingTypesVisible();
	}

	public void setHeadingTypesVisible(boolean selection) {
		cmdPanelOutput.setHeadingTypesVisible(selection);
	}

	public boolean getHeadingVisible() {
		return cmdPanelOutput.getHeadingVisible();
	}

	public void setHeadingVisible(boolean selection) {
		cmdPanelOutput.setHeadingVisible(selection);
	}

	public boolean getAutoclear() {
		return cmdPanelOutput.getAutoclear();
	}

	public void setAutoclear(boolean selection) {
		cmdPanelOutput.setAutoclear(selection);
	}

	public boolean getShowOk() {
		return cmdPanelOutput.getShowOk();
	}

	public void setShowOk(boolean selection) {
		cmdPanelOutput.setShowOk(selection);
	}

	public boolean getEnhancedOutput() {
		return cmdPanelOutput.getEnhancedOutput();
	}

	public void setEnhancedOutput(boolean selection) {
		cmdPanelOutput.setEnhancedOutput(selection);
	}

	public void copyOutputToInput() {
		cmdPanelInput.copyOutputToInput();
	}

	public void saveOutputAsText() {
		cmdPanelOutput.saveOutputAsText();
	}

	public void saveOutputAsHtml() {
		cmdPanelOutput.saveOutputAsHtml();
	}

	public void clearOutput() {
		cmdPanelOutput.clearOutput();
	}

}
