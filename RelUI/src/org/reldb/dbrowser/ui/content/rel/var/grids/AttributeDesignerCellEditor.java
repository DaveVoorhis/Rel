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
            commit(MoveDirectionEnum.NONE);
            this.dialogIsClosed = true;
            return Window.OK;
        } else {
            this.dialogIsClosed = true;
            return Window.CANCEL;
        }
    }

    @Override
    public AttributeDesignerDialog createDialogInstance() {
        dialogIsClosed = false;
        return new AttributeDesignerDialog(parent.getShell(), parentDesigner.connection);
    }

    @Override
    public AttributeDesignerDialog getDialogInstance() {
        return (AttributeDesignerDialog) dialog;
    }

    @Override
    public Object getEditorValue() {
    	String newHeading = getDialogInstance().getHeadingDefinition();
    	System.out.println("AttributeDesignerCellEditor: get heading " + newHeading);
    	return newHeading;
    }

    @Override
    public void setEditorValue(Object value) {
    	System.out.println("AttributeDesignerCellEditor: set heading " + value.toString());
    	getDialogInstance().setHeadingDefinition(value.toString());
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
