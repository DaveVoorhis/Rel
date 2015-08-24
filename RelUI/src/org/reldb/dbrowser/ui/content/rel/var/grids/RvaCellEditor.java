package org.reldb.dbrowser.ui.content.rel.var.grids;

import org.eclipse.jface.window.Window;
import org.eclipse.nebula.widgets.nattable.edit.gui.AbstractDialogCellEditor;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer.MoveDirectionEnum;

public class RvaCellEditor extends AbstractDialogCellEditor {	
    private boolean dialogIsClosed = false;

    private Editor parentEditor;
    private String defaultValue;
    
    public RvaCellEditor(Editor editor, String defaultValue) {
    	this.parentEditor = editor;
    	this.defaultValue = defaultValue;
    }
    
    @Override
    public int open() {
    	if (Window.OK == getDialogInstance().open()) {
            commit(MoveDirectionEnum.NONE);
            this.dialogIsClosed = true;
            return Window.OK;
        } else {
            this.dialogIsClosed = true;
            return Window.CANCEL;
        }
    }

    @Override
    public RvaEditorDialog createDialogInstance() {
        dialogIsClosed = false;
        return new RvaEditorDialog(parent.getShell(), parentEditor.connection);
    }

    @Override
    public RvaEditorDialog getDialogInstance() {
        return (RvaEditorDialog) dialog;
    }

    @Override
    public Object getEditorValue() {
    	return getDialogInstance().getRVAValue();
    }

    @Override
    public void setEditorValue(Object value) {
    	String editorValue;
    	if (value.toString().trim().length() == 0)
    		editorValue = defaultValue;
    	else
    		editorValue = value.toString();
    	getDialogInstance().setRVAValue(editorValue);
    }

    @Override
    public void close() {
    	getDialogInstance().close();
    }

    @Override
    public boolean isClosed() {
        return this.dialogIsClosed;
    }

}
