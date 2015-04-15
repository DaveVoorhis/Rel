package org.reldb.dbrowser.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.reldb.dbrowser.ui.StatusPanel;
import org.reldb.dbrowser.ui.monitor.LogWin;
import org.reldb.dbrowser.ui.preferences.Preferences;

public class MainPanel extends Composite {
	
	private CTabFolder tabFolder;
	private StatusPanel statusPanel;
	
	private final String rectPrefName = "mainpanel.rect";

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public MainPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());

		getShell().addListener(SWT.Close, new Listener() {
			@Override
			public void handleEvent(Event event) {
				LogWin.remove();
			}
		});

		getShell().addListener(SWT.Move, new Listener() {
			@Override
			public void handleEvent(Event event) {
				Preferences.setPreference(rectPrefName, getShell().getBounds());
			}
		});
		
		getShell().addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event event) {
				Preferences.setPreference(rectPrefName, getShell().getBounds());
			}
		});
		
		LogWin.install(parent);

		Rectangle rect = Preferences.getPreferenceRectangle(rectPrefName);
		if (rect.height > 0 && rect.width > 0)
			getShell().setBounds(rect);
		
		tabFolder = new CTabFolder(this, SWT.None);
		FormData fd_tabFolder = new FormData();
		fd_tabFolder.top = new FormAttachment(0);
		fd_tabFolder.left = new FormAttachment(0);
		fd_tabFolder.right = new FormAttachment(100);
		tabFolder.setLayoutData(fd_tabFolder);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		tabFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setStatus(((DbTab)tabFolder.getSelection()).getStatus());
			}
		});
		
		statusPanel = new StatusPanel(this, SWT.None);
		FormData fd_statusPanel = new FormData();
		fd_statusPanel.left = new FormAttachment(0);
		fd_statusPanel.right = new FormAttachment(100);
		fd_statusPanel.bottom = new FormAttachment(100);
		statusPanel.setLayoutData(fd_statusPanel);
		
		fd_tabFolder.bottom = new FormAttachment(statusPanel);
	}

	public void setStatus(String s) {
		statusPanel.setStatus(s);
	}

	public CTabFolder getTabFolder() {
		return tabFolder;
	}
}
