package ca.mb.armchair.rel3.dbrowser.ui;

import java.awt.Component;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;

public class DatabaseChooser extends JFileChooser {

	private static final long serialVersionUID = 1L;
	   
    public static boolean isRelDatabase(File f) {
    	return (f.isDirectory() && (new File(f + File.separator + "Reldb.rel").exists()));
    }

	public boolean accept(File f) {
		return isRelDatabase(f);
	}
	
	public DatabaseChooser(String title, String buttonText) {
		super();
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
        ArrayList<JPanel> ldcpanels = new ArrayList<JPanel>();
        for (Component c: getComponents()) {
            if (c instanceof JPanel) {
            	ldcpanels.add((JPanel)c);
            }
        }
        ldcpanels.get(0).getComponent(0).setVisible(false);
        ldcpanels.get(2).getComponent(0).setVisible(false);
	}
}
