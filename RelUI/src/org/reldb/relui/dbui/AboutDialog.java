package org.reldb.relui.dbui;

import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.wb.swt.ResourceManager;
import org.reldb.relui.version.Version;
import org.eclipse.wb.swt.SWTResourceManager;

public class AboutDialog extends Dialog {

	protected Object result;
	protected Shell shell;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public AboutDialog(Shell parent) {
		super(parent, SWT.None);
		setText("About Rel");
	}

	/**
	 * Open the dialog.
	 * @return the result
	 */
	public Object open() {
		createContents();
		shell.open();
		shell.layout();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return result;
	}

	/**
	 * Create contents of the dialog.
	 */
	private void createContents() {
		shell = new Shell(getParent(), SWT.APPLICATION_MODAL);
		shell.setText("About Rel");
		shell.setSize(500, 330);
		
		Label lblVersion = new Label(shell, SWT.RIGHT);
		lblVersion.setFont(SWTResourceManager.getFont("Arial", 18, SWT.BOLD));
		lblVersion.setBounds(182, 158, 309, 28);
		lblVersion.setText(Version.getVersion());
		
		Label lblCopyright = new Label(shell, SWT.RIGHT);
		lblCopyright.setBounds(192, 192, 299, 21);
		lblCopyright.setText(Version.getCopyright());
		
		Label lblBrowse = new Label(shell, SWT.RIGHT);
		lblBrowse.setForeground(SWTResourceManager.getColor(SWT.COLOR_LINK_FOREGROUND));
		lblBrowse.setBounds(182, 212, 309, 21);
		lblBrowse.setText(Version.getURL());
		lblBrowse.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseUp(MouseEvent e) {
				org.eclipse.swt.program.Program.launch(Version.getURL());
			}			
		});
		
		Button btnOk = new Button(shell, SWT.NONE);
		btnOk.setBounds(396, 289, 95, 28);
		btnOk.setText("Ok");
		btnOk.setFocus();
		btnOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
			}
		});
		
		Label lblImage = new Label(shell, SWT.NONE);
		lblImage.setBackground(SWTResourceManager.getColor(SWT.COLOR_LIST_SELECTION));
		lblImage.setImage(ResourceManager.getPluginImage("RelUI", "icons/RelAboutAndSplash.png"));
		lblImage.setBounds(0, 0, 500, 330);
	}

}
