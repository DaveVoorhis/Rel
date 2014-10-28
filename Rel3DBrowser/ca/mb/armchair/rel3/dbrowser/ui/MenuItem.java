package ca.mb.armchair.rel3.dbrowser.ui;

import javax.swing.JMenuItem;

public abstract class MenuItem extends JMenuItem {
	private static final long serialVersionUID = 1L;

	public MenuItem(String title) {
		super(title);
		addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				action();
			}
		});
	}
	
	public MenuItem(String title, int mnemonic) {
		this(title);
		setMnemonic(mnemonic);
	}
	
	public MenuItem(String title, javax.swing.KeyStroke keystroke) {
		this(title);
		this.setAccelerator(keystroke);
	}
	
	public abstract void action();
}
