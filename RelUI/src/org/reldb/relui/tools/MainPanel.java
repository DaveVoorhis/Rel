package org.reldb.relui.tools;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

public class MainPanel extends Composite {
	
	private CTabFolder tabFolder;
	private StatusPanel statusPanel;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public MainPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());
		
		tabFolder = new CTabFolder(this, SWT.NONE);
		FormData fd_tabFolder = new FormData();
		fd_tabFolder.top = new FormAttachment(0);
		fd_tabFolder.left = new FormAttachment(0);
		fd_tabFolder.right = new FormAttachment(100);
		tabFolder.setLayoutData(fd_tabFolder);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		statusPanel = new StatusPanel(this, SWT.NONE);
		fd_tabFolder.bottom = new FormAttachment(statusPanel);
		FormData fd_statusPanel = new FormData();
		fd_statusPanel.left = new FormAttachment(0);
		fd_statusPanel.right = new FormAttachment(100);
		fd_statusPanel.bottom = new FormAttachment(100);
		statusPanel.setLayoutData(fd_statusPanel);		
	}
		
	public CTabFolder getTabFolder() {
		return tabFolder;
	}

	public Label getStatusLabel() {
		return statusPanel.getStatusLabel();
	}
	
}
