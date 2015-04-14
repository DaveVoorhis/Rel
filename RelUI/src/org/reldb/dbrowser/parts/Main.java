package org.reldb.dbrowser.parts;

import javax.inject.Inject;
import javax.annotation.PostConstruct;

import org.eclipse.swt.widgets.Composite;
import org.reldb.dbrowser.dbui.DbMain;

public class Main {
	@Inject
	public Main() {		
	}
	
	@PostConstruct
	public void postConstruct(Composite parent) {
		DbMain.run(parent);
	}

}