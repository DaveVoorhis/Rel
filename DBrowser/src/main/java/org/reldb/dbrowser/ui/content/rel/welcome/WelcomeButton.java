package org.reldb.dbrowser.ui.content.rel.welcome;

import java.util.function.Consumer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class WelcomeButton extends Button {

	public WelcomeButton(Composite parent, String text, int style) {
		super(parent, style);
		setText(text);
	}
	
	public WelcomeButton(Composite parent, String text) {
		this(parent, text, SWT.PUSH);
	}
	
	public WelcomeButton(Composite parent, String text, int style, Consumer<SelectionEvent> action) {
		this(parent, text, style);
		addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				action.accept(e);
			}
		});		
	}

	public WelcomeButton(Composite parent, String text, Consumer<SelectionEvent> action) {
		this(parent, text, SWT.PUSH, action);
	}
	
	public void checkSubclass() {}
}
