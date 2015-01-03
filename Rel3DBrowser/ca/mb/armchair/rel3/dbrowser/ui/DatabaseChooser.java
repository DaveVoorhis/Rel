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
	
	private boolean isJTextFieldInComponentHierarchy(Component[] components) {
        for (Component c: components)
            if (c instanceof JPanel && isJTextFieldInComponentHierarchy(((JPanel)c).getComponents()))
            	return true;
            else if (c instanceof JTextField)
            	return true;
        return false;
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
        for (Component c: getComponents()) {
        	if (c instanceof JPanel)
        		if (isJTextFieldInComponentHierarchy(((JPanel)c).getComponents())) {
        			c.setVisible(false);
        			break;
        		}
        }
	}
}
