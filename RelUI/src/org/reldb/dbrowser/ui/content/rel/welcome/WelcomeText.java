package org.reldb.dbrowser.ui.content.rel.welcome;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.reldb.dbrowser.utilities.FontSize;

public class WelcomeText extends StyledText {
	
	public WelcomeText(Composite parent) {
		super(parent, SWT.WRAP | SWT.MULTI);
		setEditable(false);
		setBackground(parent.getBackground());
		setWordWrap(true);
	}

	public WelcomeText(Composite parent, String text) {
		this(parent);
		setText(text);
	}

	public WelcomeText(Composite parent, String text, int size) {
		this(parent, text);
		setFont(FontSize.getThisFontInNewSize(getFont(), 18, SWT.BOLD));
	}
	
}
