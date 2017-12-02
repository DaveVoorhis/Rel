/*******************************************************************************
 * Copyright (c) 2010 - 2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Lars Vogel <lars.Vogel@gmail.com> - Bug 419770
 *******************************************************************************/
package org.reldb.dbrowser.handlers.output;

import org.eclipse.e4.ui.model.application.ui.menu.MHandledMenuItem;
import org.eclipse.swt.widgets.Shell;
import org.reldb.dbrowser.handlers.MenuItem;
import org.reldb.dbrowser.ui.AboutDialog;

public class SaveAsHTML extends MenuItem {
	@Override
	public void execute(Shell shell, MHandledMenuItem item) {
		new AboutDialog(shell).open();
	}
}