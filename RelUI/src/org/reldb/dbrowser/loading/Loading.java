package org.reldb.dbrowser.loading;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;
import org.reldb.dbrowser.ui.IconLoader;
import org.reldb.dbrowser.utilities.FontSize;

public class Loading {

	private static final int expectedMessageCount = 6;

	private static final int backgroundWidth = 600;
	private static final int backgroundHeight = 183;
	
	private static Loading loading = null;
	
	private Shell loadingShell;
	private Label lblAction;
	private ProgressBar progressBar;
	private int count = 0;

	private static Point getMonitorCenter(Shell shell) {
		Monitor primary = shell.getDisplay().getPrimaryMonitor();
		Rectangle bounds = primary.getBounds();
		Rectangle rect = shell.getBounds();
		int x = bounds.x + (bounds.width - rect.width) / 2;
		int y = bounds.y + (bounds.height - rect.height) / 2;
		return new Point(x, y);
	}
	
	public static void open() {
		loading = new Loading();
		loading.openInstance();
	}

	public static void close() {
		if (loading != null)
			loading.closeInstance();
		loading = null;
	}

	public static void action(String message) {
		if (loading != null)
			loading.setMessageInstance(message);
	}
	
	public static boolean isDisplayed() {
		return loading != null;
	}
	
	private void openInstance() {
		loadingShell = createLoadingShell();
		loadingShell.open();
		count = 0;
		refresh();
	}

	private void refresh() {
		while (loadingShell.getDisplay().readAndDispatch());		
	}
	
	private Shell createLoadingShell() {
		final Shell shell = new Shell(SWT.TOOL | SWT.NO_TRIM);

		shell.setLayout(new FormLayout());
		shell.setMinimumSize(backgroundWidth, backgroundHeight);
		shell.setSize(backgroundWidth, backgroundHeight);
		
		Image background = IconLoader.loadIconNormal("loading");
		shell.setBackgroundImage(background);
		
		Label lblTitle = new Label(shell, SWT.NONE);
		lblTitle.setFont(FontSize.getThisFontInNewSize(lblTitle.getFont(), 24, SWT.BOLD));
		lblTitle.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_lblTitle = new FormData();
		fd_lblTitle.top = new FormAttachment(0, 0);
		fd_lblTitle.left = new FormAttachment(0, 10);
		fd_lblTitle.right = new FormAttachment(100, -10);
		lblTitle.setLayoutData(fd_lblTitle);
		lblTitle.setText("Loading");
		
		progressBar = new ProgressBar(shell, SWT.NONE);
		progressBar.setMaximum(expectedMessageCount);
		FormData fd_progressBar = new FormData();
		fd_progressBar.bottom = new FormAttachment(100, -10);
		fd_progressBar.left = new FormAttachment(0, 10);
		fd_progressBar.right = new FormAttachment(100, -10);
		progressBar.setLayoutData(fd_progressBar);
		
		lblAction = new Label(shell, SWT.WRAP);
		lblAction.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		FormData fd_lblAction = new FormData();
		fd_lblAction.top = new FormAttachment(progressBar, -60);
		fd_lblAction.bottom = new FormAttachment(progressBar, -10);
		fd_lblAction.left = new FormAttachment(0, 10);
		fd_lblAction.right = new FormAttachment(100, -200);
		lblAction.setLayoutData(fd_lblAction);
		lblAction.setText("Starting...");
		
		shell.setSize(background.getBounds().x, background.getBounds().y);
		shell.setLocation(getMonitorCenter(shell));
		return shell;
	}

	private void closeInstance() {
		loadingShell.close();
		loadingShell = null;
	}
	
	private void setMessageInstance(final String message) {
		if (lblAction != null && !lblAction.isDisposed()) {
			loadingShell.getDisplay().syncExec(new Runnable() {
				@Override
				public void run() {
					String msg = message.trim().replaceAll("\n", "");
					if (msg.length() == 0)
						return;
					lblAction.setText(msg);
					progressBar.setSelection(count++);
					loadingShell.update();
					refresh();
				}
			});
		}
	}

}
