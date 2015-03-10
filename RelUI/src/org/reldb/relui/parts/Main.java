package org.reldb.relui.parts;

import javax.inject.Inject;
import javax.annotation.PostConstruct;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.reldb.relui.dbui.DbMain;
import org.reldb.relui.dbui.DbTab;
import org.reldb.relui.tools.MainPanel;

public class Main {
	@Inject
	public Main() {		
	}
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		MainPanel mainPanel = new MainPanel(parent, SWT.None);
		DbMain.setMainPanel(mainPanel);
		(new DbTab()).setText("Default");
		new DbTab();
		DbMain.setSelection(0);
	}

}