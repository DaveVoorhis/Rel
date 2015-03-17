package org.reldb.relui.parts;

import javax.inject.Inject;
import javax.annotation.PostConstruct;

import org.eclipse.swt.widgets.Composite;

import org.reldb.relui.dbui.DbMain;

public class Main {
	@Inject
	public Main() {		
	}
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		DbMain.run(parent);
	}

}