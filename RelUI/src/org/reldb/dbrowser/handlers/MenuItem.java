package org.reldb.dbrowser.handlers;

import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.swt.widgets.Shell;

public abstract class MenuItem {
	@CanExecute
	public boolean canExecute(MHandledMenuItem item) {
		System.out.println("MenuItem: canExecute");
		return true;
	}

	@Execute
	public abstract void execute(Shell shell, MHandledMenuItem item);
}
