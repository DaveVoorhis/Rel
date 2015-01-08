package org.reldb.rel.dbrowser.ui;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.*;

/**
 * This allows the user to edit application options.
 */
public class DialogOptions extends JDialog {
	private static final long serialVersionUID = 0;

	private JPanel panel1 = new JPanel();
	private BorderLayout borderLayout1 = new BorderLayout();
	private JTabbedPane jTabbedPane1 = new JTabbedPane();
	private JPanel jPanel1 = new JPanel();
	private GridBagLayout gridBagLayout1 = new GridBagLayout();
	private JButton saveButton = new JButton();
	private JButton cancelButton = new JButton();
	private DisplayPropertiesEditor displayPropertiesEditor = new DisplayPropertiesEditor();
	
	public DialogOptions(Frame frame, String title, boolean modal) {
		super(frame, title, modal);

		jbInit();
					
		addComponentListener(new ComponentAdapter() {
			public void componentShown(ComponentEvent e) {
				for (int i = 0; i < jTabbedPane1.getTabCount(); ++i) {
					EditorOptions editor = (EditorOptions) jTabbedPane1.getComponentAt(i);
					editor.open();
				}
			}
		});
		
		jTabbedPane1.addTab("Display", displayPropertiesEditor);

		java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(screenSize.width / 2 - getSize().width / 2,
				screenSize.height / 2 - getSize().height / 2);

		saveButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				for (int i = 0; i < jTabbedPane1.getTabCount(); ++i) {
					EditorOptions editor = (EditorOptions) jTabbedPane1.getComponentAt(i);
					editor.save();
				}
				setVisible(false);
			}
		});

		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				// TODO - add change detection and confirmation dialog.
				for (int i = 0; i < jTabbedPane1.getTabCount(); ++i) {
					EditorOptions editor = (EditorOptions) jTabbedPane1.getComponentAt(i);
					editor.cancel();
				}
				setVisible(false);
			}
		});
		
		pack();
	}

	public DialogOptions() {
		this(null, "", false);
	}

	private void jbInit() {
		panel1.setLayout(borderLayout1);
		jPanel1.setLayout(gridBagLayout1);
		saveButton.setText("Save");
		cancelButton.setText("Cancel");
		this.setTitle("Options");
		this.getContentPane().add(panel1, BorderLayout.CENTER);
		panel1.add(jTabbedPane1, BorderLayout.CENTER);
		panel1.add(jPanel1, BorderLayout.SOUTH);
		jPanel1.add(saveButton, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));
		jPanel1.add(cancelButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));
	}
}