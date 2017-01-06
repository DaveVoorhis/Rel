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
		public boolean matches(KeyEvent e) {
			if ((e.stateMask & SWT.CTRL) == 0)
				return false;
			if ((e.stateMask & SWT.SHIFT) != 0)
				return (Character.isUpperCase(altOf) && e.keyCode == Character.toLowerCase(altOf)) || e.keyCode == actualKeypress;
			return e.keyCode == altOf;
		}
	}

	private static SpecialCharacter[] specialCharacters = {
		new SpecialCharacter('≤', 'l', "Less than or equal to"),
		new SpecialCharacter('≥', 'g', "Greater than or equal to"),
		new SpecialCharacter('≠', '=', "Not equal"),
		new SpecialCharacter('×', 't', "Times"),
		new SpecialCharacter('÷', 'q', "Divide"),
		new SpecialCharacter('‼', '!', "Image in", '1'),
		new SpecialCharacter('⊂', 'c', "Proper subset"),
		new SpecialCharacter('⊆', 'C', "Subset"),
		new SpecialCharacter('⊃', 'd', "Proper superset"),
		new SpecialCharacter('⊇', 'D', "Superset")
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
		(new Label(shlSpecialCharacters, SWT.BORDER_SOLID)).setText("Symbol");
		(new Label(shlSpecialCharacters, SWT.BORDER)).setText("Ctrl-<key> shortcut");
		(new Label(shlSpecialCharacters, SWT.BORDER)).setText("Description");
		for (SpecialCharacter special: specialCharacters) {
			Button charButton = new Button(shlSpecialCharacters, SWT.PUSH | SWT.BORDER);
			charButton.setText(Character.toString(special.symbol));
			charButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					emit(special.symbol);
				}
			});
			Label charLabel = new Label(shlSpecialCharacters, SWT.NONE | SWT.BORDER);
			charLabel.setText(Character.toString(special.altOf));
			Label charDescription = new Label(shlSpecialCharacters, SWT.BORDER);
			charDescription.setText(special.description);
		}
		shlSpecialCharacters.pack();
	}

}
