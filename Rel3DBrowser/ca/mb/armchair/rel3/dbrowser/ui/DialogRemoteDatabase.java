package ca.mb.armchair.rel3.dbrowser.ui;

import java.awt.*;
import javax.swing.*;

/**
 * This allows the user to edit application options.
 */
public class DialogRemoteDatabase extends JDialog {
	private static final long serialVersionUID = 0;

	private JPanel buttonPanel = new JPanel();
	private JPanel topPanel = new JPanel();
	private JPanel contentPanel = new JPanel();
	private JButton okButton = new JButton();
	private JButton cancelButton = new JButton();
	private JTextField jTextFieldLocation = new JTextField();
	private JTextField jTextFieldPort = new JTextField();

	public DialogRemoteDatabase(final Browser browser) {
		super(browser, "Open Remote Database", false);
		
		setResizable(false);
		
		okButton.setText("Ok");
		okButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				if (jTextFieldPort.getText().trim().length() > 0)
					browser.openRemoteDatabase(jTextFieldLocation.getText() + ":" + jTextFieldPort.getText());
				else
					browser.openRemoteDatabase(jTextFieldLocation.getText());
				setVisible(false);
			}
		});
		
		cancelButton.setText("Cancel");
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				setVisible(false);
			}
		});

		topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		jTextFieldLocation.setPreferredSize(new Dimension(400, 20));
		jTextFieldPort.setPreferredSize(new Dimension(60, 20));
		
		JPanel locationPanel = new JPanel();
		locationPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		locationPanel.add(new JLabel("Domain name or IP address: "));
		locationPanel.add(jTextFieldLocation);
		
		JPanel portPanel = new JPanel();
		portPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		portPanel.add(new JLabel("Port: "));
		portPanel.add(jTextFieldPort);
		
		contentPanel.setLayout(new GridLayout(2, 1));
		contentPanel.add(locationPanel);
		contentPanel.add(portPanel);
		
		topPanel.add(contentPanel);
		
		buttonPanel.setLayout(new GridBagLayout());

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(topPanel, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		buttonPanel.add(okButton, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));
		buttonPanel.add(cancelButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));
		
		getRootPane().setDefaultButton(okButton);
		
		pack();

		java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(screenSize.width / 2 - getSize().width / 2, screenSize.height / 2 - getSize().height / 2);
	}
}