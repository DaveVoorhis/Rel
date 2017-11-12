package org.reldb.dbrowser.ui.content.cmd;

import java.io.IOException;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.reldb.dbrowser.ui.DbConnection;
import org.reldb.dbrowser.ui.RevDatabase.Script;
import org.reldb.dbrowser.ui.content.cmd.CmdPanelInput.ErrorInformation;
import org.reldb.rel.exceptions.DatabaseFormatVersionException;

public class CmdPanel extends Composite {

	public static final int NONE = 0;
	public static final int NO_INPUT_TOOLBAR = 1;
	
	private CmdPanelOutput cmdPanelOutput;
	private CmdPanelInput cmdPanelInput;
	
	private SashForm sashForm;
	
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
		
		sashForm = new SashForm(this, SWT.VERTICAL);

		cmdPanelOutput = new CmdPanelOutput(sashForm, connection, CmdPanelOutput.SHOW_SERVER_RESPONSE) {
			@Override
			protected void notifyInputDone() {
				cmdPanelInput.done();
				cmdPanelInput.setFocused();
			}
			@Override
			protected void notifyInputOfSuccess() {
				cmdPanelInput.selectAll();
				notifyExecuteSuccess();
				cmdPanelInput.setFocused();
			}
			@Override
			protected void notifyInputOfError(StringBuffer errorBuffer) {
				ErrorInformation eInfo = cmdPanelInput.handleError(errorBuffer);
				notifyError(eInfo);
			}
			@Override
			protected void zoom() {
				if (sashForm.getMaximizedControl() == null) {
					sashForm.setMaximizedControl(cmdPanelInput);
					zoomInParent();
				} else if (sashForm.getMaximizedControl() == cmdPanelInput) {
					sashForm.setMaximizedControl(cmdPanelOutput);
					zoomInParent();
				} else {
					sashForm.setMaximizedControl(null);
					unzoomInParent();
				}
			}
			@Override
			protected void notifyEnhancedOutputChange() {
				CmdPanel.this.notifyEnhancedOutputChange();
			}
		};
		cmdPanelInput = new CmdPanelInput(sashForm, cmdPanelOutput, cmdstyle, connection.getKeywords()) {
			@Override
			protected void notifyHistoryAdded(String historyItem) {
				CmdPanel.this.notifyHistoryAdded(historyItem);
			}
			@Override
			protected String getDefaultSaveFileName() {
				return CmdPanel.this.getDefaultSaveFileName();
			}
		};
		
		sashForm.setWeights(new int[] {2, 1});
	}

	/** Override to receive notifications of errors. */
	protected void notifyError(ErrorInformation eInfo) {}

	public void run() {
		cmdPanelInput.run();
	}
	
	protected String getDefaultSaveFileName() {
		return "Untitled";
	}

	private void setZoomedParent(Composite parent, Composite setTo) {
		Composite parentparent = parent.getParent();
		if (parentparent instanceof SashForm) {
			SashForm parentSash = (SashForm)parentparent;
			parentSash.setMaximizedControl(setTo);
		}
	}
	
	private void zoomInParent() {
		Composite parent = getParent();
		setZoomedParent(parent, parent);
	}
	
	private void unzoomInParent() {
		Composite parent = getParent();
		setZoomedParent(parent, null);
	}
	
	public void zoom() {
		if (sashForm.getMaximizedControl() == null) {
			sashForm.setMaximizedControl(cmdPanelInput);
			zoomInParent();
		} else if (sashForm.getMaximizedControl() == cmdPanelInput) {
			sashForm.setMaximizedControl(cmdPanelOutput);
			zoomInParent();
		} else {
			sashForm.setMaximizedControl(null);
			unzoomInParent();
		}
	}
	
	protected void notifyHistoryAdded(String historyItem) {
	}

	protected void notifyEnhancedOutputChange() {
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

	public void focusOnInput() {
		cmdPanelInput.setFocused();		
	}
	
	public void redisplayed() {
		focusOnInput();
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

	public String getInputText() {
		return cmdPanelInput.getText();
	}
	
	public void setContent(Script script) {
		setInputText(script.getContent());
		cmdPanelInput.setHistory(script.getHistory());
	}

	public void setContent(String text) {
		cmdPanelInput.saveToHistory();
		cmdPanelInput.setText(text);
	}
	
}
