package org.reldb.dbrowser.ui.content.filtersorter;

import org.eclipse.swt.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

/**
 * Class for displaying a pop-up control in its own shell. The control behaves
 * similar to a pop-up menu, but is a composite so can contain arbitrary
 * controls.
 * 
 * <p>
 * <b>Sample Usage:</b>
 * 
 * <pre>
 * PopupComposite popup = new PopupComposite(getShell());
 * Text text = new Text(popup, SWT.BORDER);
 * popup.pack();
 * popup.show(shell.toDisplay(new Point(10, 10)));
 * </pre>
 * 
 * @author Kevin McGuinness
 * 
 * From http://palea.cgrb.oregonstate.edu/svn/jaiswallab/Annotation/src/ie/dcu/swt/PopupComposite.java
 * 
 * With modifications by Dave Voorhis.
 * 
 */
public class PopupComposite extends Composite {
	
	/**
	 * Style of the shell that will house the composite
	 */ 
	private static final int SHELL_STYLE = SWT.MODELESS | SWT.NO_TRIM | SWT.ON_TOP | SWT.BORDER;
	
	/**
	 * Shell that will house the composite
	 */ 
	private final Shell shell;
		
	/**
	 * Create a Pop-up composite with the default {@link SWT#BORDER} style.
	 * 
	 * @param parent
	 *          The parent shell.
	 */
	public PopupComposite(Shell parent) {
		this(parent, SWT.BORDER);
	}	
	
	/**
	 * Create a Pop-up composite. The default layout is a fill layout.
	 * 
	 * @param parent
	 *          The parent shell.
	 * @param style
	 *          The composite style.
	 */
	public PopupComposite(Shell parent, int style) {
		super(new Shell(parent, SHELL_STYLE), style);
		shell = getShell();
		shell.setLayout(new FillLayout());;
		shell.addShellListener(new ActivationListener());
		setLayout(createLayout());
	}
	
	/**
	 * Display the composite below the given tool item. The item will be sized
	 * such that it's width is at least the width of the given tool item.
	 * 
	 * @param item
	 *          The tool item.
	 */
	public void showBelow(ToolItem item) {
		Rectangle r = item.getBounds();
		Point p = item.getParent().toDisplay(new Point(r.x, r.y + r.height));
		setSize(computeSize(item));
		show(p);
	}
		
	/**
	 * Display the composite in its own shell at the given point.
	 * 
	 * @param pt
	 *          The point where the pop-up should appear.
	 */
	public void show(Point pt) {
		// Match shell and component sizes
		shell.setSize(getSize());
		
		if (pt != null) {
			shell.setLocation(pt);
		}
		
		shell.open();
	}
	
	/**
	 * Display the pop-up where it was last displayed.
	 */
	public void show() {
		show(null);
	}
		
	/**
	 * Hide the pop-up.
	 */
	public void hide() {
		shell.setVisible(false);
	}
	
	/**
	 * Close and dispose the pop-up.
	 */
	public void close() {
		hide();
		shell.dispose();
	}
	
	/**
	 * Returns <code>true</code> if the shell is currently activated.
	 * 
	 * @return <code>true</code> if the shell is visible.
	 */
	public boolean isDisplayed() {
		return shell.isVisible();
	}
	
	/**
	 * Creates the default layout for the composite.
	 * 
	 * @return the default layout.
	 */
	private FillLayout createLayout() {
		FillLayout layout = new FillLayout();
		layout.marginWidth = 5;
		layout.marginHeight = 5;
		return layout;
	}
	
	/**
	 * Computes the optimal size with respect to the given tool item.
	 * 
	 * @param item
	 *          The tool item.
	 * @return The optimal size.
	 */
	private Point computeSize(ToolItem item) {
		Point s2 = computeSize(item.getWidth(), SWT.DEFAULT);
		Point s1 = computeSize(SWT.DEFAULT, SWT.DEFAULT);
		return s1.x > s2.x ? s1 : s2;
	}	

	/**
	 * Pack this Composite and its Shell.
	 */
	public void pack() {
		super.pack();
		getShell().pack();
	}
	
	private final class ActivationListener extends ShellAdapter {
		@Override
		public void shellDeactivated(ShellEvent e) {
			hide();
		}
	};
	
}