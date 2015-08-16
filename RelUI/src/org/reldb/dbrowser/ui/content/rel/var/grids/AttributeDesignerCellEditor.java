package org.reldb.dbrowser.ui.content.rel.var.grids;

import org.eclipse.jface.window.Window;
import org.eclipse.nebula.widgets.nattable.edit.gui.AbstractDialogCellEditor;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer.MoveDirectionEnum;

public class AttributeDesignerCellEditor extends AbstractDialogCellEditor {	
    private boolean dialogIsClosed = false;

    private Designer parentDesigner;
    
    public AttributeDesignerCellEditor(Designer designer) {
    	this.parentDesigner = designer;
    }
    
    @Override
    public int open() {
    	if (Window.OK == getDialogInstance().open()) {
            this.dialogIsClosed = true;
            return Window.CANCEL;
        } else {
            commit(MoveDirectionEnum.NONE);
            this.dialogIsClosed = true;
            return Window.OK;
        }
    }

    @Override
    public AttributeDesignerDialog createDialogInstance() {
        dialogIsClosed = false;
        return new AttributeDesignerDialog(parent.getShell(), parentDesigner.connection);
    }

    @Override
    public AttributeDesignerDialog getDialogInstance() {
        return (AttributeDesignerDialog)dialog;
    }

    @Override
    public Object getEditorValue() {
    	return getDialogInstance().getAttributeDefinition();
    }

    @Override
    public void setEditorValue(Object value) {
    	getDialogInstance().setAttributeDefinition(value.toString());
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
