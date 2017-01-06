package org.reldb.dbrowser.ui.content.cmd;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;

public class SpecialCharacters extends Dialog {
	private Shell shlSpecialCharacters;
	private StyledText inputText;
	
	private static class SpecialCharacter {
		char symbol;
		char altOf;
		String description;
		char actualKeypress;
		public SpecialCharacter(char symbol, char altOf, String description, char actualKeypress) {
			this.symbol = symbol;
			this.altOf = altOf;
			this.description = description;
			this.actualKeypress = actualKeypress;
		}
		public SpecialCharacter(char symbol, char altOf, String description) {
			this(symbol, altOf, description, (char)0);
		}
		// Not universal! Works for the characters below.
		public boolean matches(KeyEvent e) {
			if ((e.stateMask & SWT.CTRL) == 0)
				return false;
			if ((e.stateMask & SWT.SHIFT) != 0)
				return (Character.isUpperCase(altOf) && e.keyCode == Character.toLowerCase(altOf)) || e.keyCode == actualKeypress;
			return e.keyCode == altOf;
		}
	}

	private static SpecialCharacter[] specialCharacters = {
		new SpecialCharacter('\u00D7', 't', "Times"),
		new SpecialCharacter('\u00F7', 'd', "Divide"),
		new SpecialCharacter('\u203C', '!', "Image in", '1'),
		new SpecialCharacter('\u2260', '=', "Not equal"),
		new SpecialCharacter('\u2264', 'l', "Less than or equal to"),
		new SpecialCharacter('\u2265', 'g', "Greater than or equal to"),
		new SpecialCharacter('\u2282', 'c', "Proper subset"),
		new SpecialCharacter('\u2283', 'p', "Proper superset"),
		new SpecialCharacter('\u2286', 'C', "Subset", ','),
		new SpecialCharacter('\u2287', 'P', "Superset", '.')
	};
	
	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public SpecialCharacters(Shell parent, StyledText inputText) {
		super(parent, SWT.NONE);
		this.inputText = inputText;
		inputText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				// System.out.println("character = '" + e.character + "' keycode = '" + (char)e.keyCode + "' " + (((e.stateMask & SWT.CTRL) != 0) ? "CTRL" : "") + " " + (((e.stateMask & SWT.SHIFT) != 0) ? "SHIFT" : ""));
				for (SpecialCharacter specialCharacter: specialCharacters) {
					if (specialCharacter.matches(e)) {
						emit(specialCharacter.symbol);
						break;
					}
				}
			}
		});
	}

	protected void emit(char symbol) {
		inputText.insert(Character.toString(symbol));
		inputText.setCaretOffset(inputText.getCaretOffset() + 1);
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public void open() {
		createContents();
		shlSpecialCharacters.open();
		shlSpecialCharacters.layout();
		Display display = getParent().getDisplay();
		while (!shlSpecialCharacters.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shlSpecialCharacters = new Shell(getParent(), SWT.DIALOG_TRIM);
		shlSpecialCharacters.setText("Special Characters");
		GridLayout gridLayout = new GridLayout(3, false);
		gridLayout.verticalSpacing = 2;
		shlSpecialCharacters.setLayout(gridLayout);
		Label heading = new Label(shlSpecialCharacters, SWT.NONE);
		heading.setText("Symbol");
		heading.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		heading = new Label(shlSpecialCharacters, SWT.NONE);
		heading.setText("Ctrl-<key> shortcut");
		heading.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		heading = new Label(shlSpecialCharacters, SWT.NONE);
		heading.setText("Description");
		heading.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		for (SpecialCharacter special: specialCharacters) {
			Button charButton = new Button(shlSpecialCharacters, SWT.PUSH);
			charButton.setText(Character.toString(special.symbol));
			charButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					emit(special.symbol);
				}
			});
			charButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
			Label charLabel = new Label(shlSpecialCharacters, SWT.NONE);
			charLabel.setText(Character.toString(special.altOf));
			charLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
			Label charDescription = new Label(shlSpecialCharacters, SWT.NONE);
			charDescription.setText(special.description);
			charDescription.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true));
		}
		shlSpecialCharacters.pack();
	}

}
