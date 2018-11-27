package org.reldb.dbrowser.ui.content.recent;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.layout.RowLayout;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Rectangle;
import org.reldb.dbrowser.Core;
import org.reldb.dbrowser.DBrowser;
import org.reldb.dbrowser.ui.DbTab;
import org.reldb.dbrowser.ui.IconLoader;

public class RecentPanel extends ScrolledComposite {

	private void createItem(Composite parent, String prompt, String iconName, String dbURL, Listener action) {
		boolean enabled = true;
		if (dbURL != null)
			enabled = Core.databaseMayExist(dbURL);

		Composite panel = new Composite(parent, SWT.TRANSPARENT);		
		panel.setLayout(new RowLayout(SWT.VERTICAL));
		
		Composite topPanel = new Composite(panel, SWT.TRANSPARENT);		
		topPanel.setLayout(new RowLayout(SWT.HORIZONTAL));
		
		Button icon = new Button(topPanel, SWT.FLAT);
		icon.setImage(IconLoader.loadIcon(iconName));
		icon.addListener(SWT.Selection, action);
		icon.setEnabled(enabled);
		
		if (dbURL != null) {
			Button removeButton = new Button(topPanel, SWT.NONE);
			removeButton.setText("X");
			removeButton.setToolTipText("Remove this entry from this list of recently-used databases." + ((enabled) ? " The database will not be deleted." : ""));
			removeButton.addListener(SWT.Selection, e -> {
				Core.removeFromRecentlyUsedDatabaseList(dbURL);
				((DbTabContentRecent)getParent()).redisplayed();
			});
		}

		Label urlButton = new Label(panel, SWT.NONE);
		urlButton.setText(prompt);
		urlButton.addListener(SWT.MouseUp, action);
		if (!enabled)
			urlButton.setForeground(getDisplay().getSystemColor(SWT.COLOR_TITLE_INACTIVE_FOREGROUND));
	}

	private Composite obtainContent() {
		Composite content = new Composite(this, SWT.TRANSPARENT);
		
		RowLayout rowLayout = new RowLayout(SWT.HORIZONTAL);
		rowLayout.wrap = true;
		rowLayout.pack = false;
		content.setLayout(rowLayout);
		
		createItem(content, "Create a new database", "large_database_create", null, e -> Core.newDatabase());
		createItem(content, "Open a local database", "large_database_load", null, e -> Core.openLocalDatabase());
		createItem(content, "Open a remote database", "large_database_load", null, e -> Core.openRemoteDatabase());
		for (String dbURL: Core.getRecentlyUsedDatabaseList()) {
			if (dbURL.startsWith("db:") && !DBrowser.hasLocalRel())
				continue;
			createItem(content,
					"Open " + dbURL, 
					"large_database_load",
					dbURL,
					e -> Core.openDatabase(dbURL));
		}
		
		return content;
	}

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public RecentPanel(DbTabContentRecent parent, DbTab dbTab) {
		super(parent, SWT.V_SCROLL | SWT.TRANSPARENT);

		Composite content = obtainContent();

		setContent(content);
		setExpandVertical(true);
		setExpandHorizontal(true);

		addListener(SWT.Resize, e -> {
			Rectangle clientArea = getClientArea();
			setMinSize(content.computeSize(clientArea.width, SWT.DEFAULT));
		});
	}
}
