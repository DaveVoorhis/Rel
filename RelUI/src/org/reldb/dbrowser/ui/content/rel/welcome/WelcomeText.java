package org.reldb.dbrowser.ui.content.rel.welcome;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wb.swt.SWTResourceManager;

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
		FontData[] fontdata = getFont().getFontData();
		Font newFont = SWTResourceManager.getFont(fontdata[0].getName(), 18, SWT.BOLD); 
		setFont(newFont);
	}
	
}
