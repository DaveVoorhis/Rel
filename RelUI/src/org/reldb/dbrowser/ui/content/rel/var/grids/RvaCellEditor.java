package org.reldb.dbrowser.ui.content.rel.var.grids;

import org.eclipse.jface.window.Window;
import org.eclipse.nebula.widgets.nattable.edit.gui.AbstractDialogCellEditor;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer.MoveDirectionEnum;

public class RvaCellEditor extends AbstractDialogCellEditor {	
    private boolean dialogIsClosed = false;

    private RelvarEditor parentEditor;
    
    public RvaCellEditor(RelvarEditor editor) {
    	this.parentEditor = editor;
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
    	getDialogInstance().setRVAValue(value.toString());
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
