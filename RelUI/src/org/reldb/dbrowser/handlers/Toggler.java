package org.reldb.dbrowser.handlers;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.swt.widgets.Shell;

public class Toggler extends MenuItem {
	
	@CanExecute
	public boolean canExecute(MHandledMenuItem item) {
		System.out.println("Toggler: " + getClass().toString() + " canExecute");
		return true;
	}
	
	@Execute
	public void execute(Shell shell, MHandledMenuItem item) {
		System.out.println("Toggler: " + getClass().toString() + " execute. Selected = " + item.isSelected());
	}
}
