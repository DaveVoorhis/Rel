package org.reldb.dbrowser.ui.content.cmd;

import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.swt.SWT;

import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.StyledText;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Button;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.RowLayout;

public class FindReplace extends Dialog {

	private Shell shell;
	
	private Label lblFind;
	private Text textFind;
	
	private Label lblReplace;
	private Text textReplace;
	
	private Composite compositeDirectionScope;
	private Group grpDirection;
	private Button btnRadioForward;
	private Button btnRadioBackward;
	
	private Composite compositeButtons;
	private Group grpScope;
	private Button btnRadioAll;
	private Button btnRadioSelected;
	
	private Composite compositeOptions;
	private Group grpOptions;
	private Button btnCheckCaseSensitive;
	private Button btnCheckWholeWord;
	private Button btnCheckRegexp;
	private Button btnCheckWrapsearch;
	private Button btnCheckIncremental;
	
	private Button btnFind;
	private Button btnReplaceFind;
	private Button btnReplace;
	private Button btnReplaceAll;

	private Composite compositeStatusAndClose;
	private Label lblStatus;
	
	private StyledText text;
	
	private Vector<Match> matches = null;	
	private int lastFindIndex = -1;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public FindReplace(Shell parent, StyledText text) {
		super(parent, SWT.DIALOG_TRIM | SWT.RESIZE);
		this.text = text;
		setText("Find/Replace");
		text.addExtendedModifyListener(textModifyListener);
		text.addSelectionListener(textSelectionListener);
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return null;
	}
	
	private Timer textModifyTimer = new Timer();
	
	private ExtendedModifyListener textModifyListener = new ExtendedModifyListener() {
		@Override
		public void modifyText(ExtendedModifyEvent event) {
			clearAll();
			textModifyTimer.cancel();
			textModifyTimer = new Timer();
			textModifyTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					textModifyTimer.cancel();
					Display.getDefault().syncExec(new Runnable() {
					    public void run() {
					    	clearAll();
							doFind();
						}
					});
				}
			}, 250);
		}
	};
	
	private SelectionListener textSelectionListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			boolean selected = e.y - e.x > 0;
			btnReplace.setEnabled(selected);
			btnReplaceFind.setEnabled(selected);			
		}
	};
	
	private void setStatus(String error) {
		lblStatus.setText(error);
		compositeStatusAndClose.layout();
	}

	private Pattern compilePattern() {
		String needle = textFind.getText().trim();
		if (needle.length() == 0)
			return null;
		String regexp;
		if (btnCheckWholeWord.getSelection())
			regexp = "\\b(" + Pattern.quote(needle) + ")\\b";
		else if (!btnCheckRegexp.getSelection())
			regexp = Pattern.quote(needle);
		else
			regexp = needle;
		try {
			return Pattern.compile(regexp, (!btnCheckCaseSensitive.getSelection()) ? Pattern.CASE_INSENSITIVE : 0);
		} catch (PatternSyntaxException pse) {
			String error = "Regex error: " + pse.getMessage();
			setStatus(error);
			return null;
		}
	}
	
	private void clearAll() {
		setStatus("");
		matches = null;
		text.setSelectionRange(0, 0);
		btnReplace.setEnabled(false);
		btnReplaceFind.setEnabled(false);
	}
	
	private class Match {
		public int start;
		public int end;
		public Match(int start, int end) {this.start = start; this.end = end;}
	}
	
	private void buildSearchResults() {
		matches = new Vector<Match>();
		Pattern pattern = compilePattern();
		if (pattern == null)
			return;
		Matcher matcher = pattern.matcher(text.getText());
		while (matcher.find())
			matches.add(new Match(matcher.start(), matcher.end()));
	}
	
	private void doFindInternal() {
		if (matches == null)
			buildSearchResults();
		if (lastFindIndex < 0)
			lastFindIndex = 0;
		if (matches.size() == 0) {
			setStatus("Not found.");
			return;
		}
		if (lastFindIndex >= matches.size()) {
			if (btnCheckWrapsearch.getSelection()) {
				lastFindIndex = 0;
				setStatus("Wrapped to the beginning.");
			} else {
				lastFindIndex = matches.size() - 1;
				setStatus("Reached the end.");
				return;
			}
		}
		Match match = matches.get(lastFindIndex);
		text.setSelection(match.start, match.end);
		btnReplace.setEnabled(true);
		btnReplaceFind.setEnabled(true);
	}
	
	private void doFind() {
		setStatus("");
		doFindInternal();
	}
	
	private void doFindNext() {
		setStatus("");
		if (btnRadioForward.getSelection())
			lastFindIndex++;
		else {
			lastFindIndex--;
			if (lastFindIndex < 0) {
				if (btnCheckWrapsearch.getSelection()) {
					lastFindIndex = matches.size() - 1;
					setStatus("Wrapped to the end.");
				} else {
					lastFindIndex = 0;
					setStatus("Reached the beginning.");
					return;
				}
			}
		}
		doFindInternal();
	}

	protected void doReplaceAll() {
		setStatus("");
		Pattern pattern = compilePattern();
		if (pattern == null)
			return;
		String haystack = text.getText();
		long hitCount = 0;
		Matcher matcher = pattern.matcher(haystack);
		StringBuffer changeBuffer = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(changeBuffer, textReplace.getText());
			hitCount++;
		}
		text.setText(changeBuffer.toString());
		text.redraw();
		if (hitCount == 0)
			setStatus("Not found.");
		else if (hitCount == 1)
			setStatus(hitCount + " match replaced.");
		else
			setStatus(hitCount + " matches replaced.");
		clearAll();
	}

	protected void doReplace() {
		setStatus("");
		Pattern pattern = compilePattern();
		if (pattern == null)
			return;
		String haystack = text.getSelectionText();
		Matcher matcher = pattern.matcher(haystack);
		StringBuffer changeBuffer = new StringBuffer();
		if (matcher.find())
			matcher.appendReplacement(changeBuffer, textReplace.getText());
		else {
			setStatus("Not found.");
			return;
		}
		int start = text.getSelectionRange().x;
		int length = text.getSelectionRange().y;
		text.replaceTextRange(start, length, changeBuffer.toString());
		text.setSelectionRange(start, changeBuffer.toString().length());
	}
	
	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), getStyle());
		shell.setText("Find/Replace");
		shell.setLayout(new FormLayout());

		Composite searchTextPanel = new Composite(shell, SWT.None);
		searchTextPanel.setLayout(new GridLayout(3, false));
		FormData fd_searchTextPanel = new FormData();
		fd_searchTextPanel.right = new FormAttachment(100);
		fd_searchTextPanel.top = new FormAttachment(0);
		fd_searchTextPanel.left = new FormAttachment(0);
		searchTextPanel.setLayoutData(fd_searchTextPanel);
		
		new Label(searchTextPanel, SWT.NONE);		
		lblFind = new Label(searchTextPanel, SWT.NONE);
		lblFind.setText("Find:");
		lblFind.setAlignment(SWT.RIGHT);
		lblFind.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		textFind = new Text(searchTextPanel, SWT.BORDER);
		textFind.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				boolean findEmpty = textFind.getText().isEmpty();
				btnFind.setEnabled(!findEmpty);
				btnReplaceAll.setEnabled(!findEmpty);
				clearAll();
				if (!btnCheckIncremental.getSelection())
					return;
				clearAll();
				doFind();
			}
		});
		textFind.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == '\r')
					doFindNext();
			}
		});
		textFind.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		new Label(searchTextPanel, SWT.NONE);
		lblReplace = new Label(searchTextPanel, SWT.NONE);
		lblReplace.setText("Replace with:");
		lblReplace.setAlignment(SWT.RIGHT);
		lblReplace.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		textReplace = new Text(searchTextPanel, SWT.BORDER);
		textReplace.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
				
		Composite contentPanel = new Composite(shell, SWT.NONE);
		contentPanel.setLayout(new GridLayout(3, false));
		FormData fd_contentPanel = new FormData();
		fd_contentPanel.top = new FormAttachment(searchTextPanel);
		fd_contentPanel.left = new FormAttachment(0);
		fd_contentPanel.right = new FormAttachment(100);
		contentPanel.setLayoutData(fd_contentPanel);
		
		compositeDirectionScope = new Composite(contentPanel, SWT.NONE);
		compositeDirectionScope.setLayout(new FillLayout(SWT.HORIZONTAL));
		compositeDirectionScope.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		grpDirection = new Group(compositeDirectionScope, SWT.NONE);
		grpDirection.setText("Direction");
		grpDirection.setLayout(new RowLayout(SWT.VERTICAL));
		
		btnRadioForward = new Button(grpDirection, SWT.RADIO);
		btnRadioForward.setSelection(true);
		btnRadioForward.setText("Forward");
		
		btnRadioBackward = new Button(grpDirection, SWT.RADIO);
		btnRadioBackward.setText("Backward");
		
		grpScope = new Group(compositeDirectionScope, SWT.NONE);
		grpScope.setText("Scope");
		grpScope.setLayout(new RowLayout(SWT.VERTICAL));
		
		btnRadioAll = new Button(grpScope, SWT.RADIO);
		btnRadioAll.setText("All");
		btnRadioAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearAll();
				doFind();
			}
		});
		btnRadioAll.setSelection(true);
		
		btnRadioSelected = new Button(grpScope, SWT.RADIO);
		btnRadioSelected.setText("Selected lines");
		btnRadioSelected.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearAll();
				doFind();
			}
		});
		
		compositeOptions = new Composite(contentPanel, SWT.NONE);
		compositeOptions.setLayout(new FillLayout(SWT.HORIZONTAL));
		compositeOptions.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		grpOptions = new Group(compositeOptions, SWT.NONE);
		grpOptions.setText("Options");
		grpOptions.setLayout(new GridLayout(2, false));
		
		btnCheckCaseSensitive = new Button(grpOptions, SWT.CHECK);
		btnCheckCaseSensitive.setText("Case sensitive");
		btnCheckCaseSensitive.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearAll();
				doFind();
			}
		});
		
		btnCheckWholeWord = new Button(grpOptions, SWT.CHECK);
		btnCheckWholeWord.setText("Whole word");
		btnCheckWholeWord.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearAll();
				doFind();
			}
		});
		
		btnCheckRegexp = new Button(grpOptions, SWT.CHECK);
		btnCheckRegexp.setText("Regular expressions");
		btnCheckRegexp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearAll();
				doFind();
				btnCheckIncremental.setEnabled(!btnCheckRegexp.getSelection());
				btnCheckWholeWord.setEnabled(!btnCheckRegexp.getSelection());
			}
		});
		
		btnCheckWrapsearch = new Button(grpOptions, SWT.CHECK);
		btnCheckWrapsearch.setText("Wrap search");
		
		btnCheckIncremental = new Button(grpOptions, SWT.CHECK);
		btnCheckIncremental.setText("Incremental");
		btnCheckIncremental.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clearAll();
				doFind();
			}
		});
		new Label(grpOptions, SWT.NONE);
		
		compositeButtons = new Composite(contentPanel, SWT.NONE);
		compositeButtons.setLayout(new GridLayout(3, true));
		compositeButtons.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 3, 1));
		
		btnFind = new Button(compositeButtons, SWT.NONE);
		btnFind.setText("Find");
		btnFind.setEnabled(false);
		btnFind.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doFindNext();
			}
		});
		btnFind.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		new Label(compositeButtons, SWT.NONE);
		
		Composite composite = new Composite(compositeButtons, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		btnReplaceFind = new Button(compositeButtons, SWT.NONE);
		btnReplaceFind.setText("Replace/Find");
		btnReplaceFind.setEnabled(false);
		btnReplaceFind.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doReplace();
				doFindNext();
			}
		});
		btnReplaceFind.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		btnReplace = new Button(compositeButtons, SWT.NONE);
		btnReplace.setText("Replace");
		btnReplace.setEnabled(false);
		btnReplace.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doReplace();
			}
		});
		btnReplace.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		btnReplaceAll = new Button(compositeButtons, SWT.NONE);
		btnReplaceAll.setText("Replace All");
		btnReplaceAll.setEnabled(false);
		btnReplaceAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doReplaceAll();
			}
		});
		btnReplaceAll.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		compositeStatusAndClose = new Composite(shell, SWT.NONE);
		fd_contentPanel.bottom = new FormAttachment(compositeStatusAndClose);
		FormData fd_compositeStatusAndClose = new FormData();
		fd_compositeStatusAndClose.bottom = new FormAttachment(100);
		fd_compositeStatusAndClose.right = new FormAttachment(100);
		fd_compositeStatusAndClose.left = new FormAttachment(0);
		compositeStatusAndClose.setLayoutData(fd_compositeStatusAndClose);
		compositeStatusAndClose.setLayout(new GridLayout(2, false));
		
		lblStatus = new Label(compositeStatusAndClose, SWT.WRAP);
		lblStatus.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true, 1, 1));
		
		Button btnClose = new Button(compositeStatusAndClose, SWT.RIGHT);
		btnClose.setText("Close");
		btnClose.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnClose.setAlignment(SWT.CENTER);
		btnClose.setFocus();
		btnClose.setSize(btnClose.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		btnClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}
		});
		
		shell.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				text.removeExtendedModifyListener(textModifyListener);
				text.removeSelectionListener(textSelectionListener);
				text.setFocus();
			}
		});
		
		shell.pack();
	}
}
