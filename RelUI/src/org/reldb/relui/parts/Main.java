package org.reldb.relui.parts;

import javax.inject.Inject;
import javax.annotation.PostConstruct;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.reldb.relui.tools.MainPanel;
import org.reldb.relui.ui.DbTab;

public class Main {
	@Inject
	public Main() {		
	}
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		MainPanel main = new MainPanel(parent, SWT.None);
		new DbTab(main);
		main.getTabFolder().setSelection(0);
	}

}