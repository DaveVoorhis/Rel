package org.reldb.dbrowser.ui.content.rev;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.reldb.dbrowser.commands.CommandActivator;
import org.reldb.dbrowser.commands.Commands;
import org.reldb.dbrowser.commands.ManagedToolbar;
import org.reldb.dbrowser.ui.DbTab;
import org.reldb.dbrowser.ui.content.cmd.CmdPanelToolbar;
import org.reldb.dbrowser.ui.content.rel.var.VarEditorToolbar;

public class DbTabContentRev extends Composite {

	private Rev rev;
	private ManagedToolbar toolBar = null;

	private void addZoom(ManagedToolbar toolbar) {
		toolbar.addSeparatorFill();
		new CommandActivator(null, toolBar, "view_fullscreen", SWT.PUSH, "Zoom in or out", e -> rev.zoom());
	}

	private void makeToolbar(DbTab parentTab) {
		if (toolBar != null) {
			toolBar.dispose();
			toolBar = null;
		}

		RelvarEditorPanel relvarEditorView = rev.getCmdPanelOutput().getRelvarEditorView();
		if (relvarEditorView != null)
			toolBar = new VarEditorToolbar(this, relvarEditorView.getRelvarEditor()) {
				@Override
				public void addAdditionalItemsBefore(VarEditorToolbar toolbar) {
					new CommandActivator(Commands.Do.MakeBackup, toolbar, "safeIcon", SWT.PUSH, "Make backup", e -> parentTab.makeBackup());
				}
			};
		else
			toolBar = new CmdPanelToolbar(this, rev.getCmdPanelOutput()) {
				@Override
				public void addAdditionalItemsBefore(CmdPanelToolbar toolbar) {
					new CommandActivator(Commands.Do.MakeBackup, toolbar, "safeIcon", SWT.PUSH, "Make backup", e -> parentTab.makeBackup());
				}
			};
		addZoom(toolBar);

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
		rev.setLayoutData(fd_composite);

		layout();
	}

	public DbTabContentRev(DbTab parentTab, Composite contentParent) {
		super(contentParent, SWT.None);
		setLayout(new FormLayout());

		rev = new Rev(this, parentTab, parentTab.getConnection(), parentTab.getCrashHandler(), "scratchpad", Rev.SAVE_AND_LOAD_BUTTONS) {
			@Override
			protected void changeToolbar() {
				makeToolbar(parentTab);
			}
		};

		makeToolbar(parentTab);
	}

	public void redisplayed() {
		rev.refresh();
	}

}
