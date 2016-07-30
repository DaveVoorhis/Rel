package org.reldb.dbrowser.ui.content.rev;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.reldb.dbrowser.ui.DbConnection;
import org.reldb.dbrowser.ui.content.rel.var.grids.RelvarEditor;

public class RelvarEditorPanel extends Composite {
	private RelvarEditor editor;
	
	public RelvarEditorPanel(Composite parent, DbConnection connection, String title, int style) {
		super(parent, style);
		setLayout(new FormLayout());
		
		Label editorTitle = new Label(this, SWT.NONE);
		editorTitle.setText(title);
		editorTitle.setAlignment(SWT.CENTER);
		editorTitle.setBackground(SWTResourceManager.getColor(SWT.COLOR_GRAY));
		
		FormData fd_editorTitle = new FormData();
		fd_editorTitle.top = new FormAttachment(0);
		fd_editorTitle.left = new FormAttachment(0);
		fd_editorTitle.right = new FormAttachment(100);
		editorTitle.setLayoutData(fd_editorTitle);
		
		Composite editorComposite = new Composite(this, SWT.NONE);
		editorComposite.setLayout(new FillLayout());
		editor = new RelvarEditor(editorComposite, connection, title);		
		editor.refresh();
		
		FormData fd_editor = new FormData();
		fd_editor.top = new FormAttachment(editorTitle);
		fd_editor.left = new FormAttachment(0);
		fd_editor.right = new FormAttachment(100);
		fd_editor.bottom = new FormAttachment(100);
		editorComposite.setLayoutData(fd_editor);
	}

	public RelvarEditor getRelvarEditor() {
		return editor;
	}

}
