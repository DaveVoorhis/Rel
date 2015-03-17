package org.reldb.relui.tools;

import java.util.Vector;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;

public abstract class ToolPanel extends Composite {
	
	private static class Mode {
		public ToolItem toolItem;
		public String modeName;
		public Mode(ToolItem toolItem, String modeName) {this.toolItem = toolItem; this.modeName = modeName;}
	}
	
	private ToolBar toolBar;
	private ToolBar rightBar;
	private ToolItem separator;
	private Mode lastSelected = null;
	
	private Vector<Mode> modes = new Vector<Mode>();

	public abstract void notifyModeChange(String modeName);
	
	SelectionListener listener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			Mode selected = null;
			for (Mode mode: modes) {
				if (mode.toolItem.getSelection())
					selected = mode;
			}
			if (selected == lastSelected)
				return;
			lastSelected = selected;
			notifyModeChange(selected.modeName);
		}
	};
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public ToolPanel(Composite parent, int style) {
		super(parent, style);
		setLayout(new FormLayout());
		
		toolBar = new ToolBar(this, SWT.NONE);
		FormData fd_toolBar = new FormData();
		fd_toolBar.top = new FormAttachment(0);
		fd_toolBar.left = new FormAttachment(0);
		toolBar.setLayoutData(fd_toolBar);

		rightBar = new ToolBar(this, SWT.NONE);
		FormData fd_rightBar = new FormData();
		fd_rightBar.right = new FormAttachment(100);
		fd_rightBar.top = new FormAttachment(0);
		rightBar.setLayoutData(fd_rightBar);
	}
	
	public ToolBar getToolBar() {
		return toolBar;
	}

	public void addMode(Image iconImage, String toolTipText, String modeName) {
		if (modes.isEmpty())			
			separator = new ToolItem(rightBar, SWT.SEPARATOR);
		ToolItem item = new ToolItem(rightBar, SWT.RADIO);
		item.setImage(iconImage);
		item.setToolTipText(toolTipText);
		item.addSelectionListener(listener);
		modes.add(new Mode(item, modeName));
	}

	public void setMode(int modeNumber) {
		if (modeNumber >= modes.size() || modeNumber < 0)
			return;
		modes.get(modeNumber).toolItem.setSelection(true);
		notifyModeChange(modes.get(modeNumber).modeName);
	}

	public void clearModes() {
		for (Mode mode: modes)
			mode.toolItem.dispose();
		modes.clear();
		separator.dispose();
	}

}
