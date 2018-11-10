package org.reldb.dbrowser.handlers.edit;

import org.reldb.dbrowser.DecoratedMenuItem;
import org.reldb.dbrowser.handlers.EditMenuItem;

public class Clear extends EditMenuItem {

	public Clear() {
		super("clear");
	}
	
	public boolean canExecute(DecoratedMenuItem item) {
		if (super.canExecute(item))
			return true;
		return (getMethod("selectAll") != null && getMethod("cut") != null);
	}
	
	public void execute() {
		if (doExecute())
			return;
		if (getDefaultMethod() != null)
			doDefaultMethod();
		else {
			doMethod("selectAll");
			doMethod("cut");
		}
	}
	
}