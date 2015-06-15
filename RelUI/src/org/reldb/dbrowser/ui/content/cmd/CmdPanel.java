package org.reldb.dbrowser.ui.content.cmd;

import java.io.IOException;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.reldb.dbrowser.ui.DbConnection;
import org.reldb.rel.exceptions.DatabaseFormatVersionException;

public class CmdPanel extends Composite {

	public static final int NONE = 0;
	public static final int NO_INPUT_TOOLBAR = 1;
	
	private CmdPanelOutput cmdPanelOutput;
	private CmdPanelInput cmdPanelInput;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param cmdstyle
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws NumberFormatException 
	 * @throws DatabaseFormatVersionException 
	 */
	public CmdPanel(DbConnection connection, Composite parent, int cmdstyle) throws NumberFormatException, ClassNotFoundException, IOException, DatabaseFormatVersionException {
		super(parent, SWT.NONE);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		SashForm sashForm = new SashForm(this, SWT.VERTICAL);

		cmdPanelOutput = new CmdPanelOutput(sashForm, connection, SWT.NONE) {
			@Override
			protected void notifyInputDone() {
				cmdPanelInput.done();
			}
			@Override
			protected void notifyInputOfSuccess() {
				cmdPanelInput.selectAll();
				notifyExecuteSuccess();
			}
			@Override
			protected void notifyInputOfError(StringBuffer errorBuffer) {
				cmdPanelInput.handleError(errorBuffer);
			}
		};
		cmdPanelInput = new CmdPanelInput(sashForm, cmdPanelOutput, cmdstyle);
		
		sashForm.setWeights(new int[] {2, 1});
	}

	/** Override to be notified of execution/evaluation success. */
	public void notifyExecuteSuccess() {
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

	public void setInputText(String text) {
		cmdPanelInput.setText(text);
	}

}
