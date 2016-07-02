package org.reldb.dbrowser.ui.content.cmd;

import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import java.util.Vector;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.LineBackgroundEvent;
import org.eclipse.swt.custom.LineBackgroundListener;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;

public class FindReplace extends Dialog {

	protected Object result;
	protected Shell shlSearchreplace;
	private Label lblFind;
	private Text textFind;
	private Label lblReplace;
	private Text textReplace;
	private Composite compositeDirectionScope;
	private Composite compositeOptions;
	private Composite compositeButtons;
	private Group grpDirection;
	private Group grpScope;
	private Group grpOptions;
	private Button btnFind;
	private Button btnReplaceFind;
	private Button btnReplace;
	private Button btnReplaceAll;
	private Composite compositeVerticalBuffer;
	private Label lblStatus;
	
	private StyledText text;

	private LineBackgroundListener lineBackgroundListener = new LineBackgroundListener() {
		@Override
		public void lineGetBackground(LineBackgroundEvent event) {
			int line = text.getLineAtOffset(event.lineOffset);
			if (line == 2)
				event.lineBackground = SWTResourceManager.getColor(230, 230, 255);
		}			
	};
	
	private LineStyleListener lineStyleListener = new LineStyleListener() {
		@Override
		public void lineGetStyle(LineStyleEvent event) {
			int line = text.getLineAtOffset(event.lineOffset);
			if (line == 2) {
				Vector<StyleRange> styles = new Vector<StyleRange>();
				Color color = SWTResourceManager.getColor(180, 180, 255);
				StyleRange style = new StyleRange(event.lineOffset + 10, 5, null, color);
				styles.add(style);
				event.styles = styles.toArray(new StyleRange[0]);
			}
		}
	};
	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public FindReplace(Shell parent, StyledText text) {
		super(parent, SWT.DIALOG_TRIM | SWT.RESIZE);
		this.text = text;
		setText("Find/Replace");
		text.addLineBackgroundListener(lineBackgroundListener);
		text.addLineStyleListener(lineStyleListener);
		text.redraw();
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shlSearchreplace.open();
		shlSearchreplace.layout();
		Display display = getParent().getDisplay();
		while (!shlSearchreplace.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlSearchreplace = new Shell(getParent(), getStyle());
		shlSearchreplace.setText("Find/Replace");
		shlSearchreplace.setLayout(new GridLayout(3, false));
	
		new Label(shlSearchreplace, SWT.NONE);		
		lblFind = new Label(shlSearchreplace, SWT.NONE);
		lblFind.setAlignment(SWT.RIGHT);
		lblFind.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblFind.setText("Find:");
		textFind = new Text(shlSearchreplace, SWT.BORDER);
		textFind.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		new Label(shlSearchreplace, SWT.NONE);
		lblReplace = new Label(shlSearchreplace, SWT.NONE);
		lblReplace.setAlignment(SWT.RIGHT);
		lblReplace.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblReplace.setText("Replace with:");
		textReplace = new Text(shlSearchreplace, SWT.BORDER);
		textReplace.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		compositeDirectionScope = new Composite(shlSearchreplace, SWT.NONE);
		compositeDirectionScope.setLayout(new FillLayout(SWT.HORIZONTAL));
		compositeDirectionScope.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		grpDirection = new Group(compositeDirectionScope, SWT.NONE);
		grpDirection.setText("Direction");
		
		Button btnRadioForward = new Button(grpDirection, SWT.RADIO);
		btnRadioForward.setBounds(10, 10, 90, 18);
		btnRadioForward.setSelection(true);
		btnRadioForward.setText("Forward");
		
		Button btnRadioBackward = new Button(grpDirection, SWT.RADIO);
		btnRadioBackward.setBounds(10, 34, 90, 18);
		btnRadioBackward.setText("Backward");
		
		grpScope = new Group(compositeDirectionScope, SWT.NONE);
		grpScope.setText("Scope");
		
		Button btnRadioAll = new Button(grpScope, SWT.RADIO);
		btnRadioAll.setBounds(10, 10, 119, 18);
		btnRadioAll.setSelection(true);
		btnRadioAll.setText("All");
		
		Button btnRadioSelected = new Button(grpScope, SWT.RADIO);
		btnRadioSelected.setBounds(10, 34, 119, 18);
		btnRadioSelected.setText("Selected lines");
		
		compositeOptions = new Composite(shlSearchreplace, SWT.NONE);
		compositeOptions.setLayout(new FillLayout(SWT.HORIZONTAL));
		compositeOptions.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		grpOptions = new Group(compositeOptions, SWT.NONE);
		grpOptions.setText("Options");
		
		Button btnCheckCaseSensitive = new Button(grpOptions, SWT.CHECK);
		btnCheckCaseSensitive.setBounds(10, 10, 127, 18);
		btnCheckCaseSensitive.setText("Case sensitive");
		
		Button btnCheckWholeWord = new Button(grpOptions, SWT.CHECK);
		btnCheckWholeWord.setBounds(10, 34, 127, 18);
		btnCheckWholeWord.setText("Whole word");
		
		Button btnCheckRegexp = new Button(grpOptions, SWT.CHECK);
		btnCheckRegexp.setBounds(10, 58, 175, 18);
		btnCheckRegexp.setText("Regular expressions");
		
		Button btnCheckWrapsearch = new Button(grpOptions, SWT.CHECK);
		btnCheckWrapsearch.setBounds(144, 10, 138, 18);
		btnCheckWrapsearch.setText("Wrap search");
		
		Button btnCheckIncremental = new Button(grpOptions, SWT.CHECK);
		btnCheckIncremental.setBounds(144, 34, 138, 18);
		btnCheckIncremental.setText("Incremental");
		
		compositeVerticalBuffer = new Composite(shlSearchreplace, SWT.NONE);
		GridData gd_compositeVerticalBuffer = new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1);
		gd_compositeVerticalBuffer.heightHint = 3;
		compositeVerticalBuffer.setLayoutData(gd_compositeVerticalBuffer);
		
		new Label(shlSearchreplace, SWT.NONE);
		new Label(shlSearchreplace, SWT.NONE);
		compositeButtons = new Composite(shlSearchreplace, SWT.NONE);
		compositeButtons.setLayout(new GridLayout(2, true));
		compositeButtons.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, false, true, 1, 1));
		
		btnFind = new Button(compositeButtons, SWT.NONE);
		btnFind.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
			}
		});
		btnFind.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnFind.setText("Find");
		
		btnReplaceFind = new Button(compositeButtons, SWT.NONE);
		btnReplaceFind.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnReplaceFind.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnReplaceFind.setText("Replace/Find");
		
		btnReplace = new Button(compositeButtons, SWT.NONE);
		btnReplace.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnReplace.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnReplace.setText("Replace");
		
		btnReplaceAll = new Button(compositeButtons, SWT.NONE);
		btnReplaceAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});
		btnReplaceAll.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnReplaceAll.setText("Replace All");
		
		Composite compositeStatusAndClose = new Composite(shlSearchreplace, SWT.NONE);
		compositeStatusAndClose.setLayout(new GridLayout(2, false));
		compositeStatusAndClose.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, true, true, 3, 1));
		
		lblStatus = new Label(compositeStatusAndClose, SWT.NONE);
		lblStatus.setText("");
		
		Button btnClose = new Button(compositeStatusAndClose, SWT.RIGHT);
		btnClose.setAlignment(SWT.CENTER);
		btnClose.setText("Close");
		btnClose.setFocus();
		btnClose.setSize(btnClose.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		btnClose.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				text.removeLineBackgroundListener(lineBackgroundListener);
				text.removeLineStyleListener(lineStyleListener);
				text.redraw();
				shlSearchreplace.dispose();
			}
		});
		
		shlSearchreplace.pack();
	}

	/*
	public static void main(String args[]) {
		Display display = new Display();
		Shell shell = new Shell(display);
		(new SearchReplace(shell, new StyledText(shell, SWT.NONE))).open();
	}
	*/

}
