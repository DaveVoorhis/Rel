package org.reldb.dbrowser.ui.content.recent;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.reldb.dbrowser.Core;
import org.reldb.dbrowser.ui.DbTab;
import org.reldb.dbrowser.ui.IconLoader;

public class RecentPanel extends Composite {

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public RecentPanel(Composite parent, DbTab dbTab, int style) {
		super(parent, style);
		RowLayout rowLayout = new RowLayout();
		rowLayout.wrap = true;
		rowLayout.type = SWT.HORIZONTAL;
		setLayout(rowLayout);
		
		Composite newDb = new Composite(this, SWT.BORDER);
		newDb.setLayout(new RowLayout());
		Button newDBIcon = new Button(newDb, SWT.NONE);
		newDBIcon.addListener(SWT.Selection, e -> Core.newDatabase());
		newDBIcon.setBackgroundImage(IconLoader.loadIcon("large_database_create"));
		newDBIcon.setText("Create database");
		
		for (String dbURL: Core.getRecentlyUsedDatabaseList()) {
			Composite openDb = new Composite(this, SWT.BORDER);
			openDb.setLayout(new RowLayout());
			Button openDbIcon = new Button(openDb, SWT.NONE);
			openDbIcon.addListener(SWT.Selection, e -> Core.openDatabase(dbURL));
			openDbIcon.setText(dbURL);
			if (dbURL.startsWith("db:"))
				openDbIcon.setBackgroundImage(IconLoader.loadIcon("large_database_load"));
			else
				openDbIcon.setBackgroundImage(IconLoader.loadIcon("large_remote_database_load"));
		}
		
		pack();
	}
}
