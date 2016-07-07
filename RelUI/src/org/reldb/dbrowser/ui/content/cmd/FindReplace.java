package org.reldb.dbrowser.ui.content.cmd;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
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
		text.redraw();
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
			clearSearchResults();
			textModifyTimer.cancel();
			textModifyTimer = new Timer();
			textModifyTimer.schedule(new TimerTask() {
				@Override
				public void run() {
					textModifyTimer.cancel();
					Display.getDefault().syncExec(new Runnable() {
					    public void run() {
							doFind();
						}
					});
				}
			}, 250);
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

	private void clearSearchResults() {
		setStatus("");
		text.redraw();
	}
	
	private void clearAll() {
		clearSearchResults();
		btnReplace.setEnabled(false);
		btnReplaceFind.setEnabled(false);
	}
	
	private void doFind(int offset) {
		setStatus("");
		Pattern pattern = compilePattern();
		if (pattern == null)
			return;
		Matcher matcher = pattern.matcher(text.getText());
		if (offset >= text.getCharCount())
			offset = 0;
		if (matcher.find(offset))
			text.setSelection(matcher.start(), matcher.end());
		else
			setStatus("Not found.");
	}

	private void doFind() {
		doFind(text.getCaretOffset());
	}
	
	private void doFindNext() {
		setStatus("");
		doFind(text.getSelectionRange().x + text.getSelectionRange().y);
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
		text.replaceTextRange(text.getSelectionRange().x, text.getSelectionRange().y, changeBuffer.toString());
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
		lblFind.setAlignment(SWT.RIGHT);
		lblFind.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblFind.setText("Find:");
		textFind = new Text(searchTextPanel, SWT.BORDER);
		textFind.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				boolean findEmpty = textFind.getText().isEmpty();
				btnFind.setEnabled(!findEmpty);
				btnReplaceAll.setEnabled(!findEmpty);
				clearAll();
				if (!btnCheckIncremental.getSelection())
					return;
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
		lblReplace.setAlignment(SWT.RIGHT);
		lblReplace.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblReplace.setText("Replace with:");
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
		btnRadioAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doFind();
			}
		});
		btnRadioAll.setSelection(true);
		btnRadioAll.setText("All");
		
		btnRadioSelected = new Button(grpScope, SWT.RADIO);
		btnRadioSelected.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doFind();
			}
		});
		btnRadioSelected.setText("Selected lines");
		
		compositeOptions = new Composite(contentPanel, SWT.NONE);
		compositeOptions.setLayout(new FillLayout(SWT.HORIZONTAL));
		compositeOptions.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		grpOptions = new Group(compositeOptions, SWT.NONE);
		grpOptions.setText("Options");
		grpOptions.setLayout(new GridLayout(2, false));
		
		btnCheckCaseSensitive = new Button(grpOptions, SWT.CHECK);
		btnCheckCaseSensitive.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doFind();
			}
		});
		btnCheckCaseSensitive.setText("Case sensitive");
		
		btnCheckWholeWord = new Button(grpOptions, SWT.CHECK);
		btnCheckWholeWord.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doFind();
			}
		});
		btnCheckWholeWord.setText("Whole word");
		
		btnCheckRegexp = new Button(grpOptions, SWT.CHECK);
		btnCheckRegexp.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doFind();
				btnCheckIncremental.setEnabled(!btnCheckRegexp.getSelection());
				btnCheckWholeWord.setEnabled(!btnCheckRegexp.getSelection());
			}
		});
		btnCheckRegexp.setText("Regular expressions");
		
		btnCheckWrapsearch = new Button(grpOptions, SWT.CHECK);
		btnCheckWrapsearch.setText("Wrap search");
		
		btnCheckIncremental = new Button(grpOptions, SWT.CHECK);
		btnCheckIncremental.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doFind();
			}
		});
		btnCheckIncremental.setText("Incremental");
		new Label(grpOptions, SWT.NONE);
		
		compositeButtons = new Composite(contentPanel, SWT.NONE);
		compositeButtons.setLayout(new GridLayout(3, true));
		compositeButtons.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 3, 1));
		
		btnFind = new Button(compositeButtons, SWT.NONE);
		btnFind.setEnabled(false);
		btnFind.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doFindNext();
			}
		});
		btnFind.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnFind.setText("Find");
		new Label(compositeButtons, SWT.NONE);
		
		Composite composite = new Composite(compositeButtons, SWT.NONE);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		btnReplaceFind = new Button(compositeButtons, SWT.NONE);
		btnReplaceFind.setEnabled(false);
		btnReplaceFind.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doReplace();
				doFindNext();
			}
		});
		btnReplaceFind.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnReplaceFind.setText("Replace/Find");
		
		btnReplace = new Button(compositeButtons, SWT.NONE);
		btnReplace.setEnabled(false);
		btnReplace.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doReplace();
			}
		});
		btnReplace.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnReplace.setText("Replace");
		
		btnReplaceAll = new Button(compositeButtons, SWT.NONE);
		btnReplaceAll.setEnabled(false);
		btnReplaceAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				doReplaceAll();
			}
		});
		btnReplaceAll.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnReplaceAll.setText("Replace All");
		
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
		btnClose.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnClose.setAlignment(SWT.CENTER);
		btnClose.setText("Close");
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
				text.setFocus();
			}
		});
		
		shell.pack();
	}
}
