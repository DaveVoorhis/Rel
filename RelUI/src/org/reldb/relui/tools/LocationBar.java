package org.reldb.relui.tools;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Text;

public class LocationBar {
	
	@Inject
	public LocationBar() {
		
	}

    @PostConstruct
    public void createControls(final Composite parent) {
		parent.setLayout(new FillLayout());
    	
		Text txtInput = new Text(parent, SWT.BORDER);
		txtInput.setMessage("Enter text to mark part as dirty");
		txtInput.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
			}
		});
				
		CoolBar bar = new CoolBar (parent, SWT.BORDER);
		for (int i=0; i<2; i++) {
			CoolItem item = new CoolItem(bar, SWT.NONE);
			Button button = new Button(bar, SWT.PUSH);
			button.setText ("Button " + i);
			Point size = button.computeSize (SWT.DEFAULT, SWT.DEFAULT);
			item.setPreferredSize (item.computeSize (size.x, size.y));
			item.setControl (button);
		}
//		Rectangle clientArea = shell.getClientArea ();
//		bar.setLocation (clientArea.x, clientArea.y);
		bar.pack ();
//		shell.open ();

    }

}