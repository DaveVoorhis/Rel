package org.reldb.relui.tools;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.custom.CTabItem;

public class ContentPanel extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ContentPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		CTabFolder tabFolder = new CTabFolder(this, SWT.BORDER);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		
		CTabItem tbtmNewItem = new CTabItem(tabFolder, SWT.NONE);
		tbtmNewItem.setText("Default");
		
		DemoContent cntnt1 = new DemoContent(tabFolder, SWT.NONE);
		tbtmNewItem.setControl(cntnt1);
		
		CTabItem tbtmNewItem_1 = new CTabItem(tabFolder, SWT.CLOSE);
		tbtmNewItem_1.setText("Demo1");
		
		DemoContent cntnt2 = new DemoContent(tabFolder, SWT.NONE);
		tbtmNewItem_1.setControl(cntnt2);
		
		CTabItem tbtmNewItem_2 = new CTabItem(tabFolder, SWT.CLOSE);
		tbtmNewItem_2.setText("Demo2");
		
		DemoContent cntnt3 = new DemoContent(tabFolder, SWT.NONE);
		tbtmNewItem_2.setControl(cntnt3);
		
		tabFolder.setSelection(tbtmNewItem);
	}
}
