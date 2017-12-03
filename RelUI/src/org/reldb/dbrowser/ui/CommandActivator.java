package org.reldb.dbrowser.ui;

import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.reldb.dbrowser.handlers.MenuItem;

/*
 * A combination of a ToolItem, for display on a ToolBar, and a reference to a menu handler.
 * 
 * When the ToolItem is active, the associated menu item will be active and can launch the ToolItem.
 * 
 * When the ToolItem is inactive or disposed, the associated menu item will be inactive.
 * 
 * If mnuClass is null, there is no associated menu item.
 * 
 */
public class CommandActivator extends ToolItem {

	public CommandActivator(Class<? extends MenuItem> mnuClass, ToolBar parent, int style) {
		super(parent, style);
	}

	public void checkSubclass() {}
}
