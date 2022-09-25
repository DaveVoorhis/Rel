package org.reldb.dbrowser.ui.content.cmd;

import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.reldb.dbrowser.commands.CommandActivator;
import org.reldb.dbrowser.commands.Commands;
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
				new CommandActivator(Commands.Do.MakeBackup, this, "safeIcon", SWT.PUSH,  "Make backup", e -> parentTab.makeBackup());
				copyOutputToInputBtn = new CommandActivator(Commands.Do.CopyOutputToInput, this, "copyToInputIcon", SWT.PUSH,  "Copy output to input", e -> cmdPanel.copyOutputToInput());
				copyOutputToInputBtn.setEnabled(!cmdPanel.getEnhancedOutput());
			}
			@Override
			public void addAdditionalItemsAfter(CmdPanelToolbar toolbar) {
				addSeparatorFill();
				new CommandActivator(null, this, "view_fullscreen", SWT.PUSH, "Zoom in or out", e -> cmdPanel.zoom());
			}
		};
		
		FormData fd_toolBar = new FormData();
		fd_toolBar.left = new FormAttachment(0);
		fd_toolBar.top = new FormAttachment(0);
		fd_toolBar.right = new FormAttachment(100);
		toolBar.setLayoutData(fd_toolBar);

		FormData fd_composite = new FormData();
		fd_composite.left = new FormAttachment(0);
		fd_composite.top = new FormAttachment(toolBar);
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
