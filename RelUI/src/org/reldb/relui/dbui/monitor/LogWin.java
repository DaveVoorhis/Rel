package org.reldb.relui.dbui.monitor;

import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.widgets.Composite;
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

	private Composite parent;
		
	private StyledText textLog;
	
	private Color red;
	private Color black;
	
	private boolean disposable = false;
	
	protected LogWin(Composite parent) {
		this.parent = parent;
		createContents();
	}
	
	/**
	 * Open the window.
	 * @param parent 
	 */
	public static void open() {
		if (shell.isVisible())
			return;
		shell.open();
		shell.layout();
	}

	/**
	 * Close the window.
	 */
	private void close() {
		shell.close();
	}
	
	/**
	 * Create contents of the window.
	 */
	protected void createContents() {
		shell = new Shell(parent.getDisplay());
		shell.setSize(450, 300);
		shell.setText("Rel System Log");
		shell.setLayout(new FormLayout());
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				if (!disposable) {
					e.doit = false;
					shell.setVisible(false);
				}
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
		close();
		red.dispose();
		black.dispose();
	}
	
	private void cull() {
		if (textLog.getText().length() > 1000000)
	    	textLog.setText("[...]\n" + textLog.getText().substring(10000));		
	}
	
	private Timer updateTimer = null;
	
	private void output(String s, Color color) {
		if (!shell.isDisposed() && !shell.getDisplay().isDisposed() && !shell.isDisposed())
			shell.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					cull();
					StyleRange styleRange = new StyleRange();
					styleRange.start = textLog.getCharCount();
					styleRange.length = s.length();
					styleRange.fontStyle = SWT.NORMAL;
					styleRange.foreground = color;		
					textLog.append(s);
					textLog.setStyleRange(styleRange);
					if (updateTimer != null)
						updateTimer.cancel();
					updateTimer = new Timer();
					updateTimer.schedule(new TimerTask() {
						@Override
						public void run() {
							shell.getDisplay().asyncExec(new Runnable() {
								@Override
								public void run() {
									textLog.setCaretOffset(textLog.getCharCount());
									textLog.setSelection(textLog.getCaretOffset(), textLog.getCaretOffset());		
								}
							});
						}
					}, 250);
				}
			});
	}
	
	private static Interceptor outInterceptor;
	private static Interceptor errInterceptor;
	
	public static void install(Composite parent) {
		window = new LogWin(parent);
		
    	class LogMessages implements Logger {
			public void log(String s) {
				window.output(s, window.black);
			}
    	};
    	class LogErrors implements Logger {
			public void log(String s) {
				window.output(s, window.red);
			}
    	};
		outInterceptor = new Interceptor(System.out, new LogMessages());
		outInterceptor.attachOut();
		errInterceptor = new Interceptor(System.err, new LogErrors());
		errInterceptor.attachErr();
	}

	public static void remove() {
		if (!shell.isVisible())
			window.dispose();
		else
			window.disposable = true;
	}
	
}
