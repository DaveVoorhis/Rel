package org.reldb.relui.dbui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.ResourceManager;

public class DbTabContentRev extends DbTabContent {

	public DbTabContentRev(DbTab parentTab) {
		super(parentTab);
	}
	
	@Override
	public Control getContent(Composite contentParent) {
		Label label = new Label(contentParent, SWT.None);
		label.setText("This is the Rev content.");
		return label;
	}

	@Override
	public void getToolBarItems(ToolBar toolBar) {
		ToolItem tlitmBackup = new ToolItem(toolBar, SWT.NONE);
		tlitmBackup.setToolTipText("Make backup");
		tlitmBackup.setImage(ResourceManager.getPluginImage("RelUI", "icons/safeIcon.png"));
		tlitmBackup.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				getDbTab().makeBackup();
			}
		});
	}

}
