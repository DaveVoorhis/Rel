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

		cmdPanelOutput = new CmdPanelOutput(sashForm, dbTab.getConnection(), SWT.NONE) {
			@Override
			protected void notifyInputDone() {
				cmdPanelInput.done();
			}
			@Override
			protected void notifyInputOfSuccess() {
				cmdPanelInput.selectAll();
			}
			@Override
			protected void notifyInputOfError(StringBuffer errorBuffer) {
				cmdPanelInput.handleError(errorBuffer);
			}
		};
		cmdPanelInput = new CmdPanelInput(sashForm, cmdPanelOutput, SWT.NONE);
		
		sashForm.setWeights(new int[] {2, 1});
	}

	public CmdPanelOutput getCmdPanelOutput() {
		return cmdPanelOutput;
	}
	
	public void dispose() {
		cmdPanelInput.dispose();
		cmdPanelOutput.dispose();
		super.dispose();
	}

	public void redisplayed() {
		cmdPanelInput.setFocused();
	}

	public void load(String fname) {
		cmdPanelInput.loadFile(fname);
	}

	public void copyOutputToInput() {
		cmdPanelInput.copyOutputToInput();
	}

	public boolean getEnhancedOutput() {
		return cmdPanelOutput.getEnhancedOutput();
	}

}
