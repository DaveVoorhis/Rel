package org.reldb.dbrowser.ui.content.rel.var.grids;

import org.eclipse.jface.window.Window;
import org.eclipse.nebula.widgets.nattable.edit.gui.AbstractDialogCellEditor;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer.MoveDirectionEnum;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

public class AttributeDesignerDialog extends AbstractDialogCellEditor {

    /**
     * The selection result of the {@link FileDialog}. Needed to update the data
     * model after closing the dialog.
     */
    private String selectedFile;
    /**
     * Flag to determine whether the dialog was closed or if it is still open.
     */
    private boolean closed = false;

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.nebula.widgets.nattable.edit.editor.AbstractDialogCellEditor
     * #open()
     */
    @Override
    public int open() {
        this.selectedFile = getDialogInstance().open();
        if (this.selectedFile == null) {
            this.closed = true;
            return Window.CANCEL;
        } else {
            commit(MoveDirectionEnum.NONE);
            this.closed = true;
            return Window.OK;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.nebula.widgets.nattable.edit.editor.AbstractDialogCellEditor
     * #createDialogInstance()
     */
    @Override
    public FileDialog createDialogInstance() {
        this.closed = false;
        return new FileDialog(this.parent.getShell(), SWT.OPEN);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.nebula.widgets.nattable.edit.editor.AbstractDialogCellEditor
     * #getDialogInstance()
     */
    @Override
    public FileDialog getDialogInstance() {
        return (FileDialog) this.dialog;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.nebula.widgets.nattable.edit.editor.AbstractDialogCellEditor
     * #getEditorValue()
     */
    @Override
    public Object getEditorValue() {
        return this.selectedFile;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.nebula.widgets.nattable.edit.editor.AbstractDialogCellEditor
     * #setEditorValue(java.lang.Object)
     */
    @Override
    public void setEditorValue(Object value) {
        getDialogInstance().setFileName(value != null ? value.toString() : null);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.nebula.widgets.nattable.edit.editor.AbstractDialogCellEditor
     * #close()
     */
    @Override
    public void close() {
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.eclipse.nebula.widgets.nattable.edit.editor.AbstractDialogCellEditor
     * #isClosed()
     */
    @Override
    public boolean isClosed() {
        return this.closed;
    }

}
