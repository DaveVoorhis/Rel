package org.reldb.relui.dbui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.wb.swt.ResourceManager;

public class DbTabContentRel extends DbTabContent {

	public DbTabContentRel(DbTab parentTab) {
		super(parentTab);
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

	@Override
	public Control getContent(Composite contentParent) {
		return new DemoContent(contentParent, SWT.None);
	}

}
