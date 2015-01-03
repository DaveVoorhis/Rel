package ca.mb.armchair.rel3.dbrowser.ui;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.filechooser.FileFilter;

public class DatabaseChooser extends JFileChooser {

	private static final long serialVersionUID = 1L;
	   
    public static boolean isRelDatabase(File f) {
    	return (f.isDirectory() && (new File(f + File.separator + "Reldb.rel").exists()));
    }

	public boolean accept(File f) {
		return isRelDatabase(f);
	}
	
	private JTextField obtainTheJTextFieldInComponentHierarchy(Component[] components) {
        for (Component c: components)
            if (c instanceof JPanel) {
            	JTextField jtf = obtainTheJTextFieldInComponentHierarchy(((JPanel)c).getComponents());
            	if (jtf != null)
            		return jtf;
            }
            else if (c instanceof JTextField)
            	return (JTextField)c;
        return null;
	}
	
	public DatabaseChooser(String title, String buttonText) {
		super();
		setAcceptAllFileFilterUsed(false);
        setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        setDialogTitle(title);
        setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
            	return DatabaseChooser.this.accept(f);
            }
            @Override
            public String getDescription() {
                return "Any folder";
            }
        });
        setDialogType(JFileChooser.SAVE_DIALOG);
        setApproveButtonText(buttonText);
        // Hide file name textbox
        JTextField fileNameTextField = obtainTheJTextFieldInComponentHierarchy(getComponents());
        if (fileNameTextField != null)
        	fileNameTextField.getParent().setVisible(false);
	}
}
