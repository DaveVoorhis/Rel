package ca.mb.armchair.rel3.dbrowser.ui;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;

import ca.mb.armchair.rel3.dbrowser.utilities.Preferences;

/**
 * This allows the user to edit the display properties of the application.
 */
public class DisplayPropertiesEditor extends JPanel implements EditorOptions {
	private static final long serialVersionUID = 0;

	private GridBagLayout gridBagLayout1 = new GridBagLayout();
	private JComboBox<Integer> fontSizeComboBox = new JComboBox<Integer>();
	private JLabel jLabel3 = new JLabel();
	private JLabel jLabel2 = new JLabel();
	private JComboBox<String> fontFamilyComboBox = new JComboBox<String>();
	private JLabel jLabel1 = new JLabel();
	private TitledBorder titledBorder1;
	private JTextArea sampleLabel = new JTextArea();

	public DisplayPropertiesEditor() {
		try {
			jbInit();
			
			String fontFamilies[] = java.awt.GraphicsEnvironment
					.getLocalGraphicsEnvironment()
					.getAvailableFontFamilyNames();
			for (int i = 0; i < fontFamilies.length; ++i) {
				fontFamilyComboBox.addItem(fontFamilies[i]);
			}
			for (int size = 6; size <= 24; size += 2) {
				fontSizeComboBox.addItem(new Integer(size));
			}

			fontFamilyComboBox.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					updateSample();
				}
			});

			fontSizeComboBox.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					updateSample();
				}
			});

			updateSample();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/** Override to be notified every time font changes as user interacts with this. */
	public void sampleUpdate(Font font) {
		Preferences.getInstance().fireInputOutputFontChange(font);
	}

	public void save() {
		Preferences.getInstance().setInputOutputFont(getSelectedFont());
	}

	public void cancel() {
		sampleUpdate(Preferences.getInstance().getInputOutputFont());
	}

	public void open() {
		fontFamilyComboBox.setSelectedItem(Preferences.getInstance().getInputOutputFont().getFamily());
		fontSizeComboBox.setSelectedItem(new Integer(Preferences.getInstance().getInputOutputFont().getSize()));							
	}
	
	private void updateSample() {
		sampleLabel.setFont(getSelectedFont());
		sampleLabel.setText(
				"ABCDEFGHIJKLMNOPQRSTUVWXYZ\n\r" +
				"abcdefghijklmnopqrstuvwxyz\n\r" +
				"01234567890`¬!\"£$%^&*()-_=\n\r" + 
				"+|\\<,>.?/:;@'~#{[]}'");
		sampleUpdate(getSelectedFont());
	}

	private java.awt.Font getSelectedFont() {
		return new java.awt.Font((String) fontFamilyComboBox.getSelectedItem(),
				   java.awt.Font.PLAIN, ((Integer) fontSizeComboBox.getSelectedItem()).intValue());
	}

	void jbInit() throws Exception {
		titledBorder1 = new TitledBorder(BorderFactory.createEtchedBorder(
				Color.white, new Color(148, 145, 140)), "Input/Output Font");
		jLabel1.setText("Font Family");
		jLabel2.setText("Font Size");
		jLabel3.setText("Sample");
		this.setLayout(gridBagLayout1);
		this.setBorder(titledBorder1);
		sampleLabel.setBackground(Color.white);
		sampleLabel.setBorder(BorderFactory.createLoweredBevelBorder());
		sampleLabel.setMaximumSize(new Dimension(41, 100));
		sampleLabel.setMinimumSize(new Dimension(41, 100));
		sampleLabel.setOpaque(true);
		sampleLabel.setPreferredSize(new Dimension(41, 100));
		sampleLabel.setEditable(false);
		this.add(fontFamilyComboBox, new GridBagConstraints(1, 0, 1, 1, 0.0,
				0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));
		this.add(jLabel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));
		this.add(jLabel2, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));
		this.add(fontSizeComboBox, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(5, 5, 5, 5), 0, 0));
		this.add(jLabel3, new GridBagConstraints(0, 1, 1, 1, 0.0, 1.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.NONE,
				new Insets(5, 5, 0, 5), 0, 0));
		this.add(sampleLabel, new GridBagConstraints(0, 2, 4, 1, 0.0, 1.0,
				GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 0), 0, 0));
	}
}