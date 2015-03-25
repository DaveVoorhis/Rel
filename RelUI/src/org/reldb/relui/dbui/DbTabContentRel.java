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

	private DemoContent demoContent = null;
	
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
		if (demoContent == null)
			demoContent = new DemoContent(contentParent, SWT.None);
		return demoContent;
	}

	@Override
	public void dispose() {
		if (demoContent != null)
			demoContent.dispose();
		demoContent = null;
	}

}
