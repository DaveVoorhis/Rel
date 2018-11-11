package org.reldb.dbrowser.ui.content.cmd;

import org.eclipse.swt.custom.StyledText;

public class CmdStyledText extends StyledText {
	private UndoRedo undoredo;

	public CmdStyledText(CmdPanelInput parent, int style) {
		super(parent, style);
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

}
