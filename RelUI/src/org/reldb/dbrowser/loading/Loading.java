package org.reldb.dbrowser.loading;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
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

public class Loading {

	private static final int expectedMessageCount = 6;
	
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
		shell.setBackgroundMode(SWT.INHERIT_DEFAULT);

		shell.setLayout(new FormLayout());
		
		StyledText lblTitle = new StyledText(shell, SWT.NONE);
		lblTitle.setFont(SWTResourceManager.getFont(".SF NS Text", 16, SWT.NORMAL));
		FormData fd_lblNewLabel = new FormData();
		fd_lblNewLabel.top = new FormAttachment(0, 10);
		fd_lblNewLabel.left = new FormAttachment(0, 10);
		lblTitle.setLayoutData(fd_lblNewLabel);
		lblTitle.setText("Rel is loading.");
		StyleRange italic = new StyleRange();
		italic.start = 0;
		italic.length = 3;
		italic.fontStyle = SWT.ITALIC;
		lblTitle.setStyleRange(italic);
		lblTitle.setBackground(shell.getBackground());
		
		lblAction = new Label(shell, SWT.NONE);
		FormData fd_lblAction = new FormData();
		fd_lblAction.top = new FormAttachment(lblTitle, 6);
		fd_lblAction.left = new FormAttachment(0, 10);
		lblAction.setLayoutData(fd_lblAction);
		lblAction.setText("Starting.");
		
		progressBar = new ProgressBar(shell, SWT.HORIZONTAL);
		fd_lblAction.right = new FormAttachment(progressBar, 0, SWT.RIGHT);
		FormData fd_progressBar = new FormData();
		fd_progressBar.top = new FormAttachment(lblAction, 6);
		fd_progressBar.left = new FormAttachment(0, 10);
		fd_progressBar.right = new FormAttachment(100, -10);
		progressBar.setLayoutData(fd_progressBar);
		progressBar.setMaximum(expectedMessageCount);
		
		shell.pack();
		shell.setMinimumSize(600, shell.getSize().y + 20);
		shell.pack();
		
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
