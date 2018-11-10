package org.reldb.dbrowser.ui.content.rel;

import java.io.IOException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.reldb.dbrowser.ui.content.cmd.CmdPanel;
import org.reldb.dbrowser.ui.content.rel.DbTreeItem;
import org.reldb.dbrowser.ui.content.rel.DbTreeTab;
import org.reldb.dbrowser.ui.content.rel.RelPanel;
import org.reldb.rel.exceptions.DatabaseFormatVersionException;

public abstract class NaiveCreatorTab extends DbTreeTab {
	
	private Text name;
	private StyledText definition;
	private CmdPanel cmdPanel;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public NaiveCreatorTab(RelPanel parent, DbTreeItem item) {
		super(parent, item);
		
		setControl(getContents(parent.getTabFolder()));
		getControl().setFocus();
	}

	protected Composite getContents(Composite parent) {
		SashForm sash = new SashForm(parent, SWT.HORIZONTAL);
		
		Composite entrySide = new Composite(sash, SWT.NONE);
		
		try {
			cmdPanel = new CmdPanel(relPanel.getConnection(), sash, CmdPanel.NO_INPUT_TOOLBAR) {
				public void notifyExecuteSuccess() {
					relPanel.redisplayed();
					MessageDialog.openInformation(relPanel.getShell(), "Note", "Success!");					
					NaiveCreatorTab.this.dispose();
				}
			};
		} catch (NumberFormatException | ClassNotFoundException | IOException | DatabaseFormatVersionException e) {
			e.printStackTrace();
		}
		
		entrySide.setLayout(new FormLayout());
		
		Label lblName = new Label(entrySide, SWT.NONE);
		FormData fd_lblName = new FormData();
		fd_lblName.left = new FormAttachment(0, 10);
		fd_lblName.top = new FormAttachment(0, 10);
		lblName.setLayoutData(fd_lblName);
		lblName.setText("Name: ");
		
		name = new Text(entrySide, SWT.BORDER);
		FormData fd_text = new FormData();
		fd_text.left = new FormAttachment(lblName);
		fd_text.top = new FormAttachment(0, 5);
		fd_text.right = new FormAttachment(100, -10);
		name.setLayoutData(fd_text);
		name.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateGeneratedCommand();
			}
		});
		
		Label lblDefinition = new Label(entrySide, SWT.NONE);
		FormData fd_lblDefinition = new FormData();
		fd_lblDefinition.left = new FormAttachment(0, 10);
		fd_lblDefinition.top = new FormAttachment(lblName, 5);
		lblDefinition.setLayoutData(fd_lblDefinition);
		lblDefinition.setText("Definition:");
		
		definition = new StyledText(entrySide, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		definition.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateGeneratedCommand();
			}
		});
		
		FormData fd_styledTextDefinition = new FormData();
		fd_styledTextDefinition.left = new FormAttachment(0, 10);
		fd_styledTextDefinition.top = new FormAttachment(lblDefinition, 2);
		fd_styledTextDefinition.right = new FormAttachment(100, -10);
		fd_styledTextDefinition.bottom = new FormAttachment(100, -10);
		definition.setLayoutData(fd_styledTextDefinition);
		
		return sash;
	}

	protected void updateGeneratedCommand() {
		cmdPanel.setInputText(getGeneratedCommand(name.getText(), definition.getText()));
	}

	protected abstract String getGeneratedCommand(String name, String definition);
}
