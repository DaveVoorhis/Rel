package org.reldb.dbrowser.ui.content.cmd;

import java.io.IOException;

import org.eclipse.dbrowser.commands.CommandActivator;
import org.eclipse.dbrowser.commands.Commands;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.reldb.dbrowser.ui.DbTab;
import org.reldb.dbrowser.ui.RevDatabase;
import org.reldb.dbrowser.ui.RevDatabase.Script;
import org.reldb.rel.exceptions.DatabaseFormatVersionException;

public class DbTabContentCmd extends Composite {
	
	private CmdPanel cmdPanel = null;
	private RevDatabase database = null;
	private String name = "Untitled";
	private String oldScript;
	private CmdPanelToolbar toolBar;
	
	private CommandActivator copyOutputToInputBtn;
	
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
				if (database != null)
					database.addScriptHistory(name, historyItem);
				oldScript = historyItem;
			}
			@Override
			protected String getDefaultSaveFileName() {
				return name;
			}
		};

		toolBar = new CmdPanelToolbar(this, cmdPanel.getCmdPanelOutput()) {
			@Override
			public void addAdditionalItemsBefore(CmdPanelToolbar toolbar) {
				// backup icon
				addItem(Commands.Do.MakeBackup, "Make backup", "safeIcon", SWT.PUSH).addListener(SWT.Selection, e -> parentTab.makeBackup());
				// copy output to input
				copyOutputToInputBtn = addItem(Commands.Do.CopyOutputToInput, "Copy output to input", "copyToInputIcon", SWT.PUSH);
				copyOutputToInputBtn.setEnabled(!cmdPanel.getEnhancedOutput());
				copyOutputToInputBtn.addListener(SWT.Selection, e -> cmdPanel.copyOutputToInput());
			}
			@Override
			public void addAdditionalItemsAfter(CmdPanelToolbar toolbar) {
				addSeparatorFill();
				// zoom
				addItem(null, "Zoom in or out", "view_fullscreen", SWT.PUSH).addListener(SWT.Selection, e -> zoom());
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
	
	public void setContent(String content) {
		cmdPanel.setContent(content);
	}
}
