package org.reldb.relui.dbui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ExpandBar;
import org.eclipse.swt.widgets.ExpandItem;
import org.eclipse.swt.widgets.Table;

public class RelPanel extends Composite {
	private Table table;
	private Table table_1;
	private Table table_2;
	private Table table_3;
	private Table table_4;

	/**
	 * Create the composite.
	 * @param parentTab 
	 * @param parent
	 * @param style
	 */
	public RelPanel(DbTab parentTab, Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		SashForm sashForm = new SashForm(this, SWT.NONE);
		
		ExpandBar expandBar = new ExpandBar(sashForm, SWT.NONE);
		
		ExpandItem xpndtmNewExpanditem = new ExpandItem(expandBar, SWT.NONE);
		xpndtmNewExpanditem.setText("Variables");
		
		table = new Table(expandBar, SWT.BORDER | SWT.FULL_SELECTION);
		xpndtmNewExpanditem.setControl(table);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		xpndtmNewExpanditem.setHeight(xpndtmNewExpanditem.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		
		ExpandItem xpndtmNewExpanditem_1 = new ExpandItem(expandBar, SWT.NONE);
		xpndtmNewExpanditem_1.setText("Views");
		
		table_1 = new Table(expandBar, SWT.BORDER | SWT.FULL_SELECTION);
		xpndtmNewExpanditem_1.setControl(table_1);
		table_1.setHeaderVisible(true);
		table_1.setLinesVisible(true);
		xpndtmNewExpanditem_1.setHeight(xpndtmNewExpanditem_1.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		
		ExpandItem xpndtmForms = new ExpandItem(expandBar, SWT.NONE);
		xpndtmForms.setText("Forms");
		
		table_2 = new Table(expandBar, SWT.BORDER | SWT.FULL_SELECTION);
		xpndtmForms.setControl(table_2);
		table_2.setHeaderVisible(true);
		table_2.setLinesVisible(true);
		xpndtmForms.setHeight(xpndtmForms.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		
		ExpandItem xpndtmReports = new ExpandItem(expandBar, SWT.NONE);
		xpndtmReports.setText("Reports");
		
		table_3 = new Table(expandBar, SWT.BORDER | SWT.FULL_SELECTION);
		xpndtmReports.setControl(table_3);
		table_3.setHeaderVisible(true);
		table_3.setLinesVisible(true);
		xpndtmReports.setHeight(xpndtmReports.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		
		ExpandItem xpndtmScripts = new ExpandItem(expandBar, SWT.NONE);
		xpndtmScripts.setText("Scripts");
		
		table_4 = new Table(expandBar, SWT.BORDER | SWT.FULL_SELECTION);
		xpndtmScripts.setControl(table_4);
		table_4.setHeaderVisible(true);
		table_4.setLinesVisible(true);
		xpndtmReports.setHeight(xpndtmReports.getControl().computeSize(SWT.DEFAULT, SWT.DEFAULT).y);
		
		CTabFolder tabFolder = new CTabFolder(sashForm, SWT.BORDER);
		tabFolder.setSelectionBackground(Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		sashForm.setWeights(new int[] {1, 3});

	}
}
