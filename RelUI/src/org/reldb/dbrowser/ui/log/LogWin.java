package org.reldb.dbrowser.ui.log;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.reldb.dbrowser.ui.IconLoader;
import org.reldb.dbrowser.ui.preferences.Preferences;

public class LogWin {

	private static final int threadLoadMax = 10;

	private static LogWin window;
	protected static Shell shell;
		
	private StyledText textLog;
	
	private Color red;
	private Color black;
	private Color blue;
	
	private static class Message {
		String msg;
		Color color;
		public Message(String msg, Color color) {
			this.msg = msg;
			this.color = color;
		}
		public Message() {
			this.msg = null;
			this.color = null;
		}
		public boolean isNull() {
			return this.msg == null && this.color == null;
		}
	}
	
	private BlockingQueue<Message> messageQueue;
	private boolean running = true;
	
	private FileDialog saveTextDialog;

	private static final String rectPrefName = "logwin.rect";
	
	protected LogWin(Composite parent) {
		messageQueue = new LinkedBlockingQueue<Message>();

		shell = new Shell(parent.getDisplay());
		shell.setText("Rel System Log");
		shell.setLayout(new FormLayout());
		shell.addShellListener(new ShellAdapter() {
			@Override
			public void shellClosed(ShellEvent e) {
				e.doit = false;
				shell.setVisible(false);
			}
		});

		shell.addListener(SWT.Move, new Listener() {
			@Override
			public void handleEvent(Event event) {
				Preferences.setPreference(rectPrefName, shell.getBounds());
			}
		});
		
		shell.addListener(SWT.Resize, new Listener() {
			@Override
			public void handleEvent(Event event) {
				Preferences.setPreference(rectPrefName, shell.getBounds());
			}
		});

		red = new Color(shell.getDisplay(), 200, 0, 0);
		black = new Color(shell.getDisplay(), 0, 0, 0);
		blue = new Color(shell.getDisplay(), 0, 0, 128);
		
		ToolBar toolBar = new ToolBar(shell, SWT.FLAT | SWT.RIGHT);
		FormData fd_toolBar = new FormData();
		fd_toolBar.top = new FormAttachment(0);
		fd_toolBar.left = new FormAttachment(0);
		toolBar.setLayoutData(fd_toolBar);
		
		ToolItem tltmClear = new ToolItem(toolBar, SWT.NONE);
		tltmClear.setToolTipText("Clear");
		tltmClear.setImage(IconLoader.loadIcon("clearIcon"));
		tltmClear.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				textLog.setText("");
			}
		});
		
		ToolItem tltmSave = new ToolItem(toolBar, SWT.NONE);
		tltmSave.setToolTipText("Save");
		tltmSave.setImage(IconLoader.loadIcon("saveIcon"));
		tltmSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (saveTextDialog == null) {
					saveTextDialog = new FileDialog(shell, SWT.SAVE);
					saveTextDialog.setFilterPath(System.getProperty("user.home"));
					saveTextDialog.setFilterExtensions(new String[] {"*.txt", "*.*"});
					saveTextDialog.setFilterNames(new String[] {"Text", "All Files"});
					saveTextDialog.setText("Save Output");
					saveTextDialog.setOverwrite(true);
				}
				String fname = saveTextDialog.open();
				if (fname == null)
					return;
				try {
					BufferedWriter f = new BufferedWriter(new FileWriter(fname));
					f.write(textLog.getText());
					f.close();
					output("Saved " + fname, blue);
				} catch (IOException ioe) {
					output(ioe.toString(), red);
				}
			}
		});	
		
		textLog = new StyledText(shell, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI | SWT.H_SCROLL);
		textLog.setEditable(false);
		FormData fd_textLog = new FormData();
		fd_textLog.bottom = new FormAttachment(100);
		fd_textLog.right = new FormAttachment(100);
		fd_textLog.top = new FormAttachment(toolBar);
		fd_textLog.left = new FormAttachment(0);
		textLog.setLayoutData(fd_textLog);

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (running) {
					// wait for data to show up
					Message awaitedEntry;
					try {
						awaitedEntry = messageQueue.take();
					} catch (InterruptedException e1) {
						continue;
					}
					if (parent.isDisposed() || parent.getDisplay().isDisposed()) {
						running = false;
						return;
					}
					parent.getDisplay().syncExec(new Runnable() {
						@Override
						public void run() {
							if (!textLog.isDisposed()) {
								try {
									Message message = awaitedEntry;
									int threadLoadCount = 0;
									do {
										if (message.isNull()) {
											running = false;
											return;
										} else {
											cull();
											StyleRange styleRange = new StyleRange();
											styleRange.start = textLog.getCharCount();
											styleRange.length = message.msg.length();
											styleRange.fontStyle = SWT.NORMAL;
											styleRange.foreground = message.color;
											textLog.append(message.msg);
											textLog.setStyleRange(styleRange);
										}
										if (++threadLoadCount > threadLoadMax) {
											// exit every so often, because staying in syncExec too long causes UI lag
											return;
										}
									} while ((message = messageQueue.poll(100, TimeUnit.MILLISECONDS)) != null);
									textLog.setCaretOffset(textLog.getCharCount());
									textLog.setSelection(textLog.getCaretOffset(), textLog.getCaretOffset());		
								} catch (InterruptedException e) {
									return;
								}
							}
						}
					});
				}
			}
		}).start();
	}
	
	/**
	 * Open the window.
	 * @param parent 
	 */
	public static void open() {
		if (shell.isVisible())
			return;

		Rectangle rect = Preferences.getPreferenceRectangle(rectPrefName);
		if (rect.height > 0 && rect.width > 0)
			shell.setBounds(rect);
		
		shell.open();
		shell.layout();
	}

	/**
	 * Close the window.
	 */
	private void close() {
		shell.close();
	}
	
	public void dispose() {
		close();
		red.dispose();
		black.dispose();
		blue.dispose();
	}
	
	private void cull() {
		if (textLog.getText().length() > 1000000)
	    	textLog.setText("[...]\n" + textLog.getText().substring(10000));		
	}
	
	private void output(String s, Color color) {
		messageQueue.add(new Message(s, color));
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
		outInterceptor.detachOut();
		errInterceptor.detachErr();
		window.messageQueue.add(new Message());
		window.messageQueue.clear();
		window.messageQueue.add(new Message());
		window.dispose();
	}
	
}
