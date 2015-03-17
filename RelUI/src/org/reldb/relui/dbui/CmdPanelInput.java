package org.reldb.relui.dbui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

public class CmdPanelInput extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CmdPanelInput(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());
		
		StyledText styledText = new StyledText(this, SWT.BORDER);
		FormData fd_styledText = new FormData();
		fd_styledText.right = new FormAttachment(100);
		fd_styledText.top = new FormAttachment(0);
		fd_styledText.left = new FormAttachment(0);
		styledText.setLayoutData(fd_styledText);
		
		CmdPanelBottom cmdPanelBottom = new CmdPanelBottom(this, SWT.NONE);
		FormData fd_cmdPanelBottom = new FormData();
		fd_cmdPanelBottom.left = new FormAttachment(0);
		fd_cmdPanelBottom.right = new FormAttachment(100);
		fd_cmdPanelBottom.bottom = new FormAttachment(100);
		fd_styledText.bottom = new FormAttachment(cmdPanelBottom);
		cmdPanelBottom.setLayoutData(fd_cmdPanelBottom);

	}
}
