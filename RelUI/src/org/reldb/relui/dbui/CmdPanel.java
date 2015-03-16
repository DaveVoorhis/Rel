package org.reldb.relui.dbui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

public class CmdPanel extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CmdPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		SashForm sashForm = new SashForm(this, SWT.VERTICAL);
		
		Browser browser = new Browser(sashForm, SWT.BORDER);
		
		Composite panel = new Composite(sashForm, SWT.NONE);
		panel.setLayout(new FormLayout());
		
		Composite entryArea = new Composite(panel, SWT.NONE);
		FormData fd_entryArea = new FormData();
		fd_entryArea.bottom = new FormAttachment(100);
		fd_entryArea.top = new FormAttachment(0);
		fd_entryArea.right = new FormAttachment(100);
		fd_entryArea.left = new FormAttachment(0);
		entryArea.setLayoutData(fd_entryArea);
		sashForm.setWeights(new int[] {3, 1});
	}
}
