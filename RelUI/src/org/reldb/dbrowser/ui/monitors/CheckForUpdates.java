package org.reldb.dbrowser.ui.monitors;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.wb.swt.SWTResourceManager;

import org.reldb.dbrowser.ui.updates.UpdatesCheck;
import org.reldb.dbrowser.ui.updates.UpdatesCheckDialog;
import org.reldb.dbrowser.ui.updates.UpdatesCheck.SendStatus;
import org.reldb.dbrowser.utilities.FontSize;

public class CheckForUpdates extends Composite {

	private UpdatesCheck updateChecker;
	
	private Text txtStatus;
	
	private MouseAdapter mouseHandler = new MouseAdapter() {
		@Override
		public void mouseUp(MouseEvent event) {
			UpdatesCheckDialog.launch(getShell());
		}
	};
	
	protected void completed(SendStatus sendStatus) {
		try {
			if (sendStatus.getResponse() != null && sendStatus.getResponse().startsWith("Success")) {
				String updateURL = UpdatesCheck.getUpdateURL(sendStatus);
				if (updateURL != null) {
					System.out.println("CheckForUpdates: updates available: " + updateURL);
					txtStatus.setText("Update available");
					txtStatus.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_RED));
				} else {
					System.out.println("CheckForUpdates: no new updates.");
					txtStatus.setText("Up to date");
					txtStatus.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GREEN));
				}
				getParent().layout();
	        }
		} catch (Exception e) {
			System.out.println("CheckForUpdates: exception: " + e);
		}
	}
		
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CheckForUpdates(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout());
		
		txtStatus = new Text(this, SWT.WRAP | SWT.CENTER);
		txtStatus.setEditable(false);
		txtStatus.setBackground(getBackground());
		txtStatus.setText("Check for updates");
		txtStatus.setFont(FontSize.getThisFontInNewSize(txtStatus.getFont(), 10, SWT.NORMAL));
		txtStatus.addMouseListener(mouseHandler);
		
		txtStatus.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));		
		
		updateChecker = new UpdatesCheck(parent.getDisplay()) {
			@Override
		    public void completed(SendStatus sendStatus) {
				CheckForUpdates.this.completed(sendStatus);
			}
		};
		
		TimerTask checkForUpdates = new TimerTask() {
			@Override
			public void run() {
				System.out.println("CheckForUpdates: check for updates.");
				updateChecker.doCancel();
				updateChecker.doSend();
			}
		};
		
		// Check for updates after 10 seconds, then every 12 hours
		Timer checkTimer = new Timer();
		checkTimer.schedule(checkForUpdates, 1000 * 10, 1000 * 60 * 60 * 12);
	}
}
