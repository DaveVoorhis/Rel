package org.reldb.relui.tools;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.lang.reflect.Field;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wb.swt.SWTResourceManager;

public class Experiment extends Shell {

	/**
	 * Launch the application.
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			Experiment shell = new Experiment(display);
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the shell.
	 * @param display
	 */
	public Experiment(Display display) {
		super(display, SWT.SHELL_TRIM);
		createContents();
	}
	
	public static boolean isRetina() {
		boolean isRetina = false;
		GraphicsDevice graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		try {
			Field field = graphicsDevice.getClass().getDeclaredField("scale");
			if (field != null) {
				field.setAccessible(true);
				Object scale = field.get(graphicsDevice);
				if (scale instanceof Integer && ((Integer) scale).intValue() == 2) {
					isRetina = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return isRetina;
	}
	
	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("SWT Application");
		setSize(450, 300);
		Image image1 = SWTResourceManager.getImage("/Users/dave/git/Rel/RelUI/icons/iconmonstr-database-3-icon-32.png");
		Image image2 = SWTResourceManager.getImage("/Users/dave/git/Rel/RelUI/icons/iconmonstr-database-3-icon-16.png");
		setLayout(null);
		Canvas canvas1 = new Canvas(this, SWT.NONE);
		canvas1.setBounds(0, 0, 18, 18);
		if (image1 == null) {
			System.out.println("Image1 is null.");
		} else {
			canvas1.addListener(SWT.Paint, new Listener () {
					@Override
					public void handleEvent (Event e) {
						GC gc = e.gc;
			//			if (isRetina())
							gc.drawImage(image1, 0, 0, 32, 32, 0, 0, 16, 16);
			//			else
			//				gc.drawImage(image2, 0, 0);
					}
			});
		}
		Canvas canvas2 = new Canvas(this, SWT.NONE);
		canvas2.setBounds(25, 0, 18, 18);
		if (image2 == null) {
			System.out.println("Image2 is null.");
		} else {
			canvas2.addListener(SWT.Paint, new Listener () {
					@Override
					public void handleEvent (Event e) {
						GC gc = e.gc;
						gc.drawImage(image2, 0, 0, 16, 16, 0, 0, 16, 16);
					}
			});
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
