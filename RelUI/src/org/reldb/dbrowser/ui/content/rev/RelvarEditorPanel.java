package org.reldb.dbrowser.ui.content.rev;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wb.swt.SWTResourceManager;
import org.reldb.dbrowser.ui.DbConnection;
import org.reldb.dbrowser.ui.content.filtersorter.FilterSorter;
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
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.verticalSpacing = 0;
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		editorComposite.setLayout(gridLayout);
		
		FilterSorter filterSorter = new FilterSorter(editorComposite, SWT.BORDER, title);
		filterSorter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		filterSorter.addUpdateListener(source -> editor.refresh());
		
		editor = new RelvarEditor(editorComposite, connection, filterSorter);		
		editor.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
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
