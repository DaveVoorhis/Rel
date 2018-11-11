package org.reldb.dbrowser.ui.content.cmd;

import org.eclipse.swt.custom.StyledText;

public class CmdStyledText extends StyledText {
	private UndoRedo undoredo;
	private CmdPanelInput parent;

	public CmdStyledText(CmdPanelInput parent, int style) {
		super(parent, style);
		this.parent = parent;
		undoredo = new UndoRedo(this);
	}

	public void undo() {
		undoredo.undo();
	}
	
	public void redo() {
		undoredo.redo();
	}
	
	public void selectAll() {
		int topIndex = getTopIndex();
		setSelection(0, getCharCount());
		setTopIndex(topIndex);
	}

	public void findReplace() {
		new FindReplaceDialog(getShell(), this).open();
	}

	public void specialCharacters() {
		parent.specialCharacters();
	}

	public void previousHistory() {
		parent.previousHistory();
	}
	
	public void nextHistory() {
		parent.nextHistory();
	}

	public void loadFile() {
		parent.loadFile();
	}
	
	public void insertFile() {
		parent.insertFile();
	}
	
	public void insertFileName() {
		parent.insertFileName();
	}
	
	public void saveFile() {
		parent.saveFile();
	}
	
	public void saveHistory() {
		parent.saveHistory();
	}
}
