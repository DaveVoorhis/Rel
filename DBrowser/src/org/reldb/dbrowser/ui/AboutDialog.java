package org.reldb.dbrowser.ui;

import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.SWT;
import org.reldb.dbrowser.IconLoader;
import org.reldb.dbrowser.ui.version.Version;
import org.eclipse.wb.swt.SWTResourceManager;

public class AboutDialog extends Dialog {

	protected Shell shell;

	private static final int backgroundWidth = 500;
	private static final int backgroundHeight = 330;

	private static final int urlTop = 180;
	private static final int rightPos = backgroundWidth - 10;

	/**
	 * Create the dialog.
	 * 
	 * @param parent
	 * @param style
	 */
	public AboutDialog(Shell parent) {
		super(parent, SWT.None);
		setText("About Rel");
	}

	/**
	 * Open the dialog.
	 */
	public void open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	private boolean isPointInText(Control control, String s, int rightPosn, int topPosn, Point p) {
		GC gc = new GC(control);
		gc.setFont(shell.getFont());
		Point dimensions = gc.textExtent(s);
		Rectangle controlRect = new Rectangle(rightPosn - dimensions.x, topPosn, dimensions.x, dimensions.y);
		return controlRect.contains(p);
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), SWT.NO_TRIM);
		shell.setText("About Rel");
		shell.setLayout(null);
		shell.setMinimumSize(backgroundWidth, backgroundHeight);
		shell.setSize(backgroundWidth, backgroundHeight);
		shell.setLocation(getParent().getLocation().x + (getParent().getSize().x - backgroundWidth) / 2,
				getParent().getLocation().y + (getParent().getSize().y - backgroundHeight) / 2);

		Button btnClose = new Button(shell, SWT.PUSH);
		btnClose.setText("Close");
		btnClose.setFocus();
		btnClose.setSize(btnClose.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		btnClose.setLocation(rightPos - btnClose.getSize().x, backgroundHeight - btnClose.getSize().y - 10);
		btnClose.addListener(SWT.Selection, e -> shell.dispose());
		shell.setDefaultButton(btnClose);

		Image background = IconLoader.loadIconNormal("RelAboutAndSplash");

		shell.addPaintListener(e -> {
			if (background != null)
				e.gc.drawImage(background, 0, 0, background.getImageData().width, background.getImageData().height, 0,
						0, backgroundWidth, backgroundHeight);
			e.gc.setForeground(SWTResourceManager.getColor(SWT.COLOR_WHITE));
			e.gc.setFont(SWTResourceManager.getFont("Arial", 18, SWT.BOLD));
			int width = e.gc.textExtent(Version.getVersion()).x;
			e.gc.drawText(Version.getVersion(), rightPos - width, urlTop - 62, true);
			e.gc.setFont(SWTResourceManager.getFont("Arial", 12, SWT.BOLD));
			width = e.gc.textExtent(Version.getCopyright()).x;
			e.gc.drawText(Version.getCopyright(), rightPos - width, urlTop - 28, true);
			e.gc.setForeground(SWTResourceManager.getColor(150, 200, 255));
			width = e.gc.textExtent(Version.getURL()).x;
			e.gc.drawText(Version.getURL(), rightPos - width, urlTop, true);
		});

		shell.addListener(SWT.MouseUp, e -> {
			if (isPointInText(shell, Version.getURL(), rightPos, urlTop, new Point(e.x, e.y)))
				org.eclipse.swt.program.Program.launch(Version.getURL());
		});

		shell.addListener(SWT.MouseMove, e -> {
			Cursor cursor = shell.getCursor();
			if (cursor != null)
				cursor.dispose();
			boolean isPointerInURL = isPointInText(shell, Version.getURL(), rightPos, urlTop, new Point(e.x, e.y));
			cursor = new Cursor(shell.getDisplay(), isPointerInURL ? SWT.CURSOR_HAND : SWT.CURSOR_ARROW);
			shell.setCursor(cursor);
		});

		shell.pack();
	}

}
