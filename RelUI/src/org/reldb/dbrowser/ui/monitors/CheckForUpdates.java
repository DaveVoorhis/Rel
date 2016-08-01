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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;

public class CheckForUpdates extends Composite {

	private UpdatesCheck updateChecker;
	
	private StyledText txtStatus;
	
	private MouseAdapter mouseHandler = new MouseAdapter() {
		@Override
		public void mouseUp(MouseEvent event) {
			UpdatesCheckDialog.launch(getShell());
		}
	};

	private void makeRelItalic() {
		StyleRange italic = new StyleRange();
		italic.start = 0;
		italic.length = 3;
		italic.fontStyle = SWT.ITALIC;
		txtStatus.setStyleRange(italic);		
	}
	
	private void centreText() {
		GC gc = new GC(txtStatus);
		Point textExtent = gc.textExtent(txtStatus.getText());
		txtStatus.setMargins(2, (CheckForUpdates.this.getSize().y - textExtent.y) / 2, 2, 0);		
	}
	
	private void setText(String text) {
		txtStatus.setText(text);
		makeRelItalic();
		centreText();
		getParent().layout();
	}
	
	protected void completed(SendStatus sendStatus) {
		try {
			if (sendStatus.getResponse() != null && sendStatus.getResponse().startsWith("Success")) {
				String updateURL = UpdatesCheck.getUpdateURL(sendStatus);
				if (updateURL != null) {
					System.out.println("CheckForUpdates: Rel update is available at " + updateURL);
					setText("Rel update is available.");
					txtStatus.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
					txtStatus.setBackground(SWTResourceManager.getColor(255, 200, 200));
				} else {
					System.out.println("CheckForUpdates: Rel is up to date.");
					setText("Rel is up to date.");
					txtStatus.setForeground(SWTResourceManager.getColor(SWT.COLOR_DARK_GREEN));
				}
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
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.verticalSpacing = 0;
		gridLayout.horizontalSpacing = 0;
		setLayout(gridLayout);
		setVisible(false);
		
		txtStatus = new StyledText(this, SWT.WRAP);
		txtStatus.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		txtStatus.setEditable(false);
		txtStatus.setForeground(SWTResourceManager.getColor(SWT.COLOR_BLACK));
		txtStatus.setBackground(getBackground());
		txtStatus.setFont(FontSize.getThisFontInNewSize(txtStatus.getFont(), 10, SWT.NORMAL));
		txtStatus.addMouseListener(mouseHandler);
		txtStatus.setCaret(new Caret(txtStatus, SWT.NONE));
		setText("Rel updates?");
		
		updateChecker = new UpdatesCheck(parent.getDisplay()) {
			@Override
		    public void completed(SendStatus sendStatus) {
				CheckForUpdates.this.completed(sendStatus);
			}
		};
		
		TimerTask checkForUpdates = new TimerTask() {
			@Override
			public void run() {
	    		getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {						
						setVisible(true);
						setText("Rel updates?");
						System.out.println("CheckForUpdates: check for updates.");
						updateChecker.doCancel();
						updateChecker.doSend();
					}
	    		});
			}
		};
		
		// Check for updates after 10 seconds, then every 12 hours
		Timer checkTimer = new Timer();
		checkTimer.schedule(checkForUpdates, 1000 * 5, 1000 * 60 * 60 * 12);
	}
}
