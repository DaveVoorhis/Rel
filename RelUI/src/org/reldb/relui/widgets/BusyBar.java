package org.reldb.relui.widgets;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ProgressBar;

public class BusyBar extends ProgressBar {

	public BusyBar(Composite parent, int style) {
		super(parent, style);
		setVisible(false);
	}

	private boolean busyIndicatorRun = false;
	private boolean busyIndicatorRunning = false;
	private int direction = 1;
	
	public void startBusyIndicator() {
		stopBusyIndicator();
		setMinimum(0);
		setMaximum(100);
		setSelection(0);
		busyIndicatorRun = true;
		getDisplay().asyncExec(new Runnable() {
			public void run() {
				setVisible(true);
			}
		});
		(new Thread() {
			public void run() {
				busyIndicatorRunning = true;
				while (busyIndicatorRun) {
					final Runnable update = new Runnable() {
						public void run() {
							try {
								if (getSelection() == getMinimum())
									direction = 1;
								else if (getSelection() == getMaximum())
									direction = -1;
								setSelection(getSelection() + direction);
							} catch (Exception e) {
								if (isDisposed())
									busyIndicatorRun = false;
							}
						}
					};
					if (!isDisposed()) {
						getDisplay().asyncExec(update);
						try {sleep(15);} catch (InterruptedException e) {}
					}
				}
				busyIndicatorRunning = false;
				getDisplay().asyncExec(new Runnable() {
					public void run() {
						setVisible(false);						
					}
				});
			}
		}).start();
	}
	
	public void stopBusyIndicator() {
		busyIndicatorRun = false;
		while (busyIndicatorRunning)
			try {Thread.sleep(250);} catch (InterruptedException e) {}
	}

	// allow subclassing
	public void checkSubclass() {}
}
