package org.reldb.relui.tools;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.custom.CBanner;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.wb.swt.ResourceManager;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.RowLayout;

public class ToolPanel extends Composite {

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ToolPanel(Composite parent, int style) {
		super(parent, style);
		
		setLayout(new RowLayout(SWT.HORIZONTAL));
/*		
		CoolBar coolBar = new CoolBar(this, SWT.FLAT);
		
		CoolItem coolItem = new CoolItem(coolBar, SWT.NONE);
*/		
		ToolBar toolBar = new ToolBar(this, SWT.FLAT | SWT.RIGHT);
//		coolItem.setControl(toolBar);
		
		ToolItem tltmNewItem = new ToolItem(toolBar, SWT.NONE);
		tltmNewItem.setText("New Item");
		
		ToolItem tltmNewItem_1 = new ToolItem(toolBar, SWT.NONE);
		tltmNewItem_1.setText("New Item");
		
		ToolItem tltmNewItem_2 = new ToolItem(toolBar, SWT.NONE);
		tltmNewItem_2.setText("New Item");
		
//		coolBar.setVisible(true);
		
		pack();
	}
	
	public static void main (String [] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setLayout(new FillLayout ());
		new ToolPanel(shell, SWT.NONE);
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
	
	private Point preferredSize() {
		return new Point(500, 50);
	}
	
	public Point computeSize(int w, int h) {
		return preferredSize(); 
	}

	public Point computeSize(int w, int h, boolean changed) {
		return preferredSize();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
