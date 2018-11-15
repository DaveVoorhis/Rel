package org.eclipse.dbrowser.commands;


import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;
import org.reldb.dbrowser.ui.IconLoader;

public class DecoratedMenuItem extends MenuItem {
	
	/** MenuItem with text, accelerator, image, style and Listener. */
	public DecoratedMenuItem(Menu parentMenu, String text, int accelerator, Image image, int style, Listener listener) {
		super(parentMenu, style);
		if (text != null)
			setText(text);
		if (accelerator != 0)
			setAccelerator(accelerator);
		if (image != null)
			setImage(image);
		if (listener != null)
			addListener(SWT.Selection, listener);
	}

	/** MenuItem with text, accelerator, image, style and Listener. */
	public DecoratedMenuItem(Menu parentMenu, String text, int accelerator, String imageName, int style, Listener listener) {
		super(parentMenu, style);
		if (text != null)
			setText(text);
		if (accelerator != 0)
			setAccelerator(accelerator);
		if (imageName != null)
			setImage(IconLoader.loadIcon(imageName));
		if (listener != null)
			addListener(SWT.Selection, listener);
	}
	
	/** MenuItem with text, accelerator, image and Listener. */
	public DecoratedMenuItem(Menu parentMenu, String text, int accelerator, Image image, Listener listener) {
		this(parentMenu, text, accelerator, image, SWT.PUSH, listener);
	}

	/** MenuItem with text, accelerator, image and Listener. */
	public DecoratedMenuItem(Menu parentMenu, String text, int accelerator, String imageName, Listener listener) {
		this(parentMenu, text, accelerator, imageName, SWT.PUSH, listener);
	}
	
	/** MenuItem with text, accelerator, and Listener. */
	public DecoratedMenuItem(Menu parentMenu, String text, int accelerator, Listener listener) {
		this(parentMenu, text, accelerator, (Image)null, listener);
	}

	/** MenuItem with text, accelerator, and image */
	public DecoratedMenuItem(Menu parentMenu, String text, int accelerator, Image image) {
		this(parentMenu, text, accelerator, image, null);
	}
	
	/** MenuItem with text, accelerator, and image */
	public DecoratedMenuItem(Menu parentMenu, String text, int accelerator, String imageName) {
		this(parentMenu, text, accelerator, IconLoader.loadIcon(imageName), null);
	}
	
	public boolean canExecute() {
		return true;
	}
		
	public void checkSubclass() {}
}
