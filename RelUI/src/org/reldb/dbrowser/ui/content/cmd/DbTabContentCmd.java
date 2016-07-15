package org.reldb.dbrowser.ui.content.cmd;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.reldb.dbrowser.ui.DbTab;
import org.reldb.dbrowser.ui.IconLoader;
import org.reldb.dbrowser.ui.RevDatabase;
import org.reldb.dbrowser.ui.RevDatabase.Script;
import org.reldb.rel.exceptions.DatabaseFormatVersionException;

public class DbTabContentCmd extends Composite {
	
	private CmdPanel cmdPanel = null;
	private RevDatabase database = null;
	private String name;
	private String oldScript;
	
	private ToolItem copyOutputToInputBtn;
	
	public DbTabContentCmd(DbTab parentTab, Composite contentParent) throws NumberFormatException, ClassNotFoundException, IOException, DatabaseFormatVersionException {
		super(contentParent, SWT.None);
		setLayout(new FormLayout());
		
		cmdPanel = new CmdPanel(parentTab.getConnection(), this, CmdPanel.NONE) {
			@Override
			protected void notifyEnhancedOutputChange() {
				copyOutputToInputBtn.setEnabled(!cmdPanel.getEnhancedOutput());
			}
			@Override
			protected void notifyHistoryAdded(String historyItem) {
				database.addScriptHistory(name, historyItem);
				oldScript = historyItem;
			}
		};

		CmdPanelToolbar toolBar = new CmdPanelToolbar(this, cmdPanel.getCmdPanelOutput()) {
			public void addAdditionalItems(ToolBar toolbar) {
				// zoom
				ToolItem maximize = new ToolItem(toolbar, SWT.NONE);
				maximize.setImage(IconLoader.loadIcon("view_fullscreen"));
				maximize.setToolTipText("Zoom in or out");
				maximize.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						zoom();
					}
				});
				// backup icon
				ToolItem tlitmBackup = new ToolItem(toolbar, SWT.NONE);
				tlitmBackup.setToolTipText("Make backup");
				tlitmBackup.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						parentTab.makeBackup();
					}
				});
				addAdditionalItem(tlitmBackup, "safeIcon");
				// copy output to input
				copyOutputToInputBtn = new ToolItem(toolbar, SWT.PUSH);
				copyOutputToInputBtn.setToolTipText("Copy output to input");
				copyOutputToInputBtn.setEnabled(!cmdPanel.getEnhancedOutput());
				copyOutputToInputBtn.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						cmdPanel.copyOutputToInput();
					}
				});
				addAdditionalItem(copyOutputToInputBtn, "copyToInputIcon");
			}
		};
		
		FormData fd_toolBar = new FormData();
		fd_toolBar.left = new FormAttachment(0);
		fd_toolBar.top = new FormAttachment(0);
		fd_toolBar.right = new FormAttachment(100);
		toolBar.getToolBar().setLayoutData(fd_toolBar);

		FormData fd_composite = new FormData();
		fd_composite.left = new FormAttachment(0);
		fd_composite.top = new FormAttachment(toolBar.getToolBar());
		fd_composite.right = new FormAttachment(100);
		fd_composite.bottom = new FormAttachment(100);
		cmdPanel.setLayoutData(fd_composite);
		
	    if (parentTab.getConnection().hasRevExtensions() >= 0) {
		    name = "scratchpad";
		    database = new RevDatabase(parentTab.getConnection());
		    Script script = database.getScript(name);
		    oldScript = script.getContent();
		    cmdPanel.setContent(script);
	    }
	}

	private void zoom() {
		cmdPanel.zoom();
	}
	
	public void dispose() {
		if (database != null) {
			String newScript = cmdPanel.getInputText();
			if (!oldScript.equals(newScript))
				database.addScriptHistory(name, oldScript);
			database.setScript(name, cmdPanel.getInputText());
		}
		cmdPanel.dispose();
		super.dispose();
	}

	public void redisplayed() {
		cmdPanel.redisplayed();
	}

	public void load(String fname) {
		cmdPanel.load(fname);
	}
	
}
