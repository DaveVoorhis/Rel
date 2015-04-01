package org.reldb.relui.dbui.monitor;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Color;

public class LogWin {
	
	private static LogWin window;

	protected static Shell shell;
	
	private StyledText textLog;
	
	private Color red;
	private Color black;

	private static String criticalSection = "";
	
	protected LogWin() {
		createContents();
	}
	
	/**
	 * Open the window.
	 * @param parent 
	 */
	public static void open() {
		Display display = Display.getDefault();
		shell.open();
		shell.layout();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Close the window.
	 */
	public void close() {
		shell.close();
	}
	
	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell();
		shell.setSize(450, 300);
		shell.setText("Rel System Log");
		shell.setLayout(new FormLayout());
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				e.doit = false;
				shell.setVisible(false);
			}
		});

		red = new Color(shell.getDisplay(), 128, 0, 0);
		black = new Color(shell.getDisplay(), 0, 0, 0);
		
		ToolBar toolBar = new ToolBar(shell, SWT.FLAT | SWT.RIGHT);
		FormData fd_toolBar = new FormData();
		fd_toolBar.top = new FormAttachment(0);
		fd_toolBar.left = new FormAttachment(0);
		toolBar.setLayoutData(fd_toolBar);
		
		ToolItem tltmClear = new ToolItem(toolBar, SWT.NONE);
		tltmClear.setToolTipText("Clear");
		tltmClear.setImage(ResourceManager.getPluginImage("RelUI", "icons/clearIcon.png"));
		
		ToolItem tltmSave = new ToolItem(toolBar, SWT.NONE);
		tltmSave.setToolTipText("Save");
		tltmSave.setImage(ResourceManager.getPluginImage("RelUI", "icons/saveIcon.png"));
		
		textLog = new StyledText(shell, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI | SWT.H_SCROLL);
		textLog.setEditable(false);
		FormData fd_textLog = new FormData();
		fd_textLog.bottom = new FormAttachment(100);
		fd_textLog.right = new FormAttachment(100);
		fd_textLog.top = new FormAttachment(toolBar);
		fd_textLog.left = new FormAttachment(0);
		textLog.setLayoutData(fd_textLog);
	}
	
	public void dispose() {
		red.dispose();
		black.dispose();
		shell.dispose();
	}
	
	private void output(String s, Color color) {
		StyleRange styleRange = new StyleRange();
		styleRange.start = textLog.getCharCount();
		styleRange.length = s.length();
		styleRange.fontStyle = SWT.NORMAL;
		styleRange.foreground = color;		
		textLog.append(s);
		textLog.setStyleRange(styleRange);
	}
	
	private void cull() {
		if (textLog.getText().length() > 1000000)
	    	textLog.setText("[...]\n" + textLog.getText().substring(10000));		
	}
	
	private void outputMessage(String s) {
		cull();
		output(s, black);
	}
	
	private void outputError(String s) {
		cull();
		output(s, red);
	}
	
	private static void ensureWindowExists() {
		if (window == null)
			window = new LogWin();		
	}
	
	public static void logMessage(String s) {
		synchronized (criticalSection) {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					ensureWindowExists();
					window.outputMessage(s);
				}
			});
		}
	}
	
	public static void logError(String s) {
		synchronized (criticalSection) {
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					ensureWindowExists();
					window.outputError(s);
				}
			});
		}
	}

	public static void remove() {
		if (window != null)
			window.dispose();
	}
	
}
