package org.reldb.dbrowser.ui.content.cmd;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;

/** Provide undo/redo support for a StyledText. */
public class UndoRedo {

	private static class Capture {
		private String content;
		private int caretOffset;
		private int topIndex;
		
		public Capture(StyledText text) {
			this.content = text.getText();
			this.caretOffset = text.getCaretOffset();
			this.topIndex = text.getTopIndex();
		}
		
		public void restore(StyledText text) {
			text.setText(content);
			text.setCaretOffset(caretOffset);
			text.setTopIndex(topIndex);
		}
	}
	
	private StyledText text;
	private Vector<Capture> buffer = new Vector<Capture>();
	private int currentUndoPoint = -1;

	private Timer quietTimer = new Timer();
	
	private boolean captured = false;
	private boolean ignore = false;
	
	private void capture() {
		if (!(buffer.size() > 0 && text.getText().equals(buffer.get(buffer.size() - 1)))) {
			buffer.add(new Capture(text));
			if (buffer.size() > 100)
				buffer.remove(0);
		}
		currentUndoPoint = -1;
    	captured = true;	
	}
	
	private void restore() {
		ignore = true;
		buffer.get(currentUndoPoint).restore(text);
		ignore = false;		
	}
	
	private ExtendedModifyListener extendedModifyListener = new ExtendedModifyListener() {
		@Override
		public void modifyText(ExtendedModifyEvent event) {
			if (ignore)
				return;
			captured = false;
			currentUndoPoint = -1;
			quietTimer.cancel();
			quietTimer = new Timer();
			quietTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					quietTimer.cancel();
					Display.getDefault().syncExec(new Runnable() {
					    public void run() {
					    	capture();
						}
					});
				}
			}, 250);
		}
	};
	
	public UndoRedo(StyledText text) {
		this.text = text;
		text.addExtendedModifyListener(extendedModifyListener);
	}
	
	public void undo() {
		quietTimer.cancel();
		if (!captured)
			capture();
		if (currentUndoPoint == 0)
			return;
		if (currentUndoPoint < 0)
			currentUndoPoint = buffer.size() - 1;
		currentUndoPoint--;
		if (currentUndoPoint < 0)
			return;
		restore();
	}
	
	public void redo() {
		quietTimer.cancel();
		if (currentUndoPoint == -1 || currentUndoPoint >= buffer.size() - 1)
			return;
		currentUndoPoint++;
		restore();
	}
	
	public void dispose() {
		text.removeExtendedModifyListener(extendedModifyListener);
	}
}
