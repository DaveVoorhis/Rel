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
package org.reldb.relui.tools;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class DemoContent extends Composite {
	private Table table;

	static int i = 10000;

	public DemoContent(Composite parent, int style) {
		super(parent, style);
		
		setLayout(new GridLayout(1, false));

		table = new Table (this, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		table.setLinesVisible (true);
		table.setHeaderVisible (true);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
		data.heightHint = 200;
		table.setLayoutData(data);
		String[] titles = {" ", "C", "!", "Description", "Resource", "In Folder", "Location"};
		for (int i=0; i<titles.length; i++) {
			TableColumn column = new TableColumn (table, SWT.NONE);
			column.setText (titles [i]);
		}
		int count = 128;
		for (int i=0; i<count; i++) {
			TableItem item = new TableItem (table, SWT.NONE);
			item.setText (0, "x");
			item.setText (1, "y");
			item.setText (2, "!");
			item.setText (3, "this stuff behaves the way I expect");
			item.setText (4, "almost everywhere");
			item.setText (5, "some.folder");
			item.setText (6, "line " + i + " in nowhere");
		}
		for (int i=0; i<titles.length; i++) {
			table.getColumn (i).pack ();
		}	

		final Runnable r = new Runnable() {
		    public void run() {
		    	for (int n=0; n<100; n++) {
					if (table.isDisposed())
						return;
					TableItem item = new TableItem(table, SWT.NONE);
					item.setText (0, "x");
					item.setText (1, "y");
					item.setText (2, "!");
					item.setText (3, "zot");
					item.setText (4, "zap");
					item.setText (5, "zip");
					item.setText (6, "line " + i++ + " in nowhere");
					if (table.isDisposed())
						return;
					table.showItem(item);
		    	}
				Display.getDefault().asyncExec(this);
		    }
		};
		
		Display.getDefault().asyncExec(r);				
	}
}