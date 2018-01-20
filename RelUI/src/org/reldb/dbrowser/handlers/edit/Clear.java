package org.reldb.dbrowser.handlers.edit;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.reldb.dbrowser.handlers.EditMenuItem;

public class Clear extends EditMenuItem {

	public Clear() {
		super("clear");
	}
	
	@CanExecute
	public boolean canExecute(MHandledMenuItem item) {
		if (super.canExecute(item))
			return true;
		return (getMethod("selectAll") != null && getMethod("cut") != null);
	}
	
	@Execute
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