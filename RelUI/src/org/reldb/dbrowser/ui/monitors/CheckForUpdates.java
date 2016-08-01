package org.reldb.dbrowser.ui.monitors;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.widgets.Caret;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.wb.swt.SWTResourceManager;
import org.reldb.dbrowser.ui.updates.UpdatesCheck;
import org.reldb.dbrowser.ui.updates.UpdatesCheckDialog;
import org.reldb.dbrowser.ui.updates.UpdatesCheck.SendStatus;
import org.reldb.dbrowser.utilities.FontSize;
import org.eclipse.swt.layout.FillLayout;

public class CheckForUpdates extends Composite {

	private UpdatesCheck updateChecker;
	
	private StyledText txtStatus;
	
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
					System.out.println("CheckForUpdates: Rel update is available at " + updateURL);
					txtStatus.setText("Rel update is available.");
					txtStatus.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
					txtStatus.setBackground(SWTResourceManager.getColor(255, 220, 220));
				} else {
					System.out.println("CheckForUpdates: Rel is up to date.");
					txtStatus.setText("Rel is up to date.");
					txtStatus.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GREEN));
				}
				makeRelItalic();
				getParent().layout();
	        }
		} catch (Exception e) {
			System.out.println("CheckForUpdates: exception: " + e);
		}
	}

	private void makeRelItalic() {
		StyleRange italic = new StyleRange();
		italic.start = 0;
		italic.length = 3;
		italic.fontStyle = SWT.ITALIC;
		txtStatus.setStyleRange(italic);		
	}
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CheckForUpdates(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout());
		
		txtStatus = new StyledText(this, SWT.WRAP);
		txtStatus.setEditable(false);
		txtStatus.setMargins(2, 2, 2, 2);
		txtStatus.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		txtStatus.setBackground(getBackground());
		txtStatus.setText("Rel updates?");
		txtStatus.setFont(FontSize.getThisFontInNewSize(txtStatus.getFont(), 10, SWT.NORMAL));
		makeRelItalic();
		txtStatus.addMouseListener(mouseHandler);
		txtStatus.setCaret(new Caret(txtStatus, SWT.NONE));
		
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
