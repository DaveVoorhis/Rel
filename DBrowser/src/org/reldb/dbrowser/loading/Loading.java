package org.reldb.dbrowser.loading;

import org.eclipse.jface.util.Util;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;
import org.reldb.dbrowser.ui.IconLoader;
import org.reldb.dbrowser.utilities.FontSize;

public class Loading {

	private static final int expectedMessageCount = 4;

	private static final int backgroundWidth = 600;
	private static final int backgroundHeight = 183;
	
	private static Loading loading = null;
	
	private Shell loadingShell;
	private Text lblAction;
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
		System.err.println("Loading: open");
		if (loading != null)
			return;
		loading = new Loading();
		loading.openInstance();
		System.err.println("Loading: opened");
	}

	public static void close() {
		System.err.println("Loading close");
		if (loading != null)
			loading.closeInstance();
		loading = null;
		System.err.println("Loading: closed");
	}

	public static void action(String message) {
		if (loading != null)
			loading.setMessage(message);
	}
	
	public static boolean isDisplayed() {
		return loading != null;
	}
	
	private void openInstance() {
		loadingShell = createLoadingShell();
		loadingShell.layout();
		loadingShell.open();
		count = 0;
	}
	
	private Shell createLoadingShell() {
		final Shell shell = new Shell(SWT.NO_TRIM);

		shell.setLayout(new FormLayout());
		shell.setMinimumSize(backgroundWidth, backgroundHeight);
		shell.setSize(backgroundWidth, backgroundHeight);
		
		Image background = IconLoader.loadIconNormal("loading");
		
		Label lblTitle = new Label(shell, SWT.TRANSPARENT);
		lblTitle.setFont(FontSize.getThisFontInNewSize(lblTitle.getFont(), 24, SWT.BOLD));
		FormData fd_lblTitle = new FormData();
		fd_lblTitle.top = new FormAttachment(0, 0);
		fd_lblTitle.left = new FormAttachment(0, 10);
		fd_lblTitle.right = new FormAttachment(100, -10);
		lblTitle.setLayoutData(fd_lblTitle);
		lblTitle.setText("Loading");
		
		progressBar = new ProgressBar(shell, SWT.NONE);
		progressBar.setMaximum(expectedMessageCount);
		progressBar.setMinimum(0);
		progressBar.setSelection(0);
		FormData fd_progressBar = new FormData();
		fd_progressBar.bottom = new FormAttachment(100, -10);
		fd_progressBar.left = new FormAttachment(0, 10);
		fd_progressBar.right = new FormAttachment(100, -10);
		progressBar.setLayoutData(fd_progressBar);
		
		lblAction = new Text(shell, SWT.WRAP | SWT.TRANSPARENT);
		lblAction.setEditable(false);
		FormData fd_lblAction = new FormData();
		fd_lblAction.top = new FormAttachment(progressBar, -60);
		fd_lblAction.bottom = new FormAttachment(progressBar, -10);
		fd_lblAction.left = new FormAttachment(0, 10);
		fd_lblAction.right = new FormAttachment(100, -200);
		lblAction.setLayoutData(fd_lblAction);
		lblAction.setText("Starting...");
		
		if (!Util.isGtk()) {
			shell.setBackgroundImage(background);
			shell.setBackgroundMode(SWT.INHERIT_FORCE);
			lblTitle.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			lblAction.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
		}
		
		shell.setSize(background.getBounds().x, background.getBounds().y);
		shell.setLocation(getMonitorCenter(shell));
		
		return shell;
	}

	private void closeInstance() {
		loadingShell.close();
		loadingShell = null;
	}
	
	private void setMessage(final String message) {
		if (lblAction != null && !lblAction.isDisposed()) {
			System.err.println("Loading: message: " + message);
			String msg = message.trim().replace("\n\n", "\n");
			if (msg.length() == 0)
				return;
			lblAction.setText(msg);
			lblAction.redraw();
			lblAction.update();
			progressBar.setSelection(++count);
			progressBar.redraw();
			progressBar.update();
			loadingShell.layout();
			loadingShell.getDisplay().readAndDispatch();	// needed on OS X
		}
	}

}
