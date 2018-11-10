package org.reldb.dbrowser.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.reldb.dbrowser.loading.Loading;
import org.reldb.dbrowser.ui.StatusPanel;
import org.reldb.dbrowser.ui.crash.CrashTrap;
import org.reldb.dbrowser.ui.log.LogWin;
import org.reldb.dbrowser.ui.preferences.Preferences;
import org.reldb.dbrowser.ui.version.Version;

public class MainPanel extends Composite {

	private CTabFolder tabFolder;
	private StatusPanel statusPanel;

	private final String rectPrefName = "mainpanel.rect";

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public MainPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());

		getShell().addListener(SWT.Close, e -> LogWin.remove());
		getShell().addListener(SWT.Move, e -> Preferences.setPreference(rectPrefName, getShell().getBounds()));
		getShell().addListener(SWT.Resize, e -> Preferences.setPreference(rectPrefName, getShell().getBounds()));

		// Install logging
		LogWin.install(parent);

		// Install platform logging and UI error trapping
		new CrashTrap(getShell(), Version.getVersion());
		
		Rectangle rect = Preferences.getPreferenceRectangle(rectPrefName);
		if (rect.height > 0 && rect.width > 0)
			getShell().setBounds(rect);

		tabFolder = new CTabFolder(this, SWT.None);
		FormData fd_tabFolder = new FormData();
		fd_tabFolder.top = new FormAttachment(0);
		fd_tabFolder.left = new FormAttachment(0);
		fd_tabFolder.right = new FormAttachment(100);
		tabFolder.setLayoutData(fd_tabFolder);
		tabFolder.setSelectionBackground(
				Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		tabFolder.addListener(SWT.Selection, e -> setStatus(((DbTab) tabFolder.getSelection()).getStatus()));

		statusPanel = new StatusPanel(this, SWT.None);
		FormData fd_statusPanel = new FormData();
		fd_statusPanel.left = new FormAttachment(0);
		fd_statusPanel.right = new FormAttachment(100);
		fd_statusPanel.bottom = new FormAttachment(100);
		statusPanel.setLayoutData(fd_statusPanel);

		fd_tabFolder.bottom = new FormAttachment(statusPanel);
		
		layout();
	}

	public void setStatus(String s) {
		statusPanel.setStatus(s);
	}

	public CTabFolder getTabFolder() {
		return tabFolder;
	}
}
