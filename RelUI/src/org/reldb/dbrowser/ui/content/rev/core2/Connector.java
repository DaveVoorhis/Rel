package org.reldb.dbrowser.ui.content.rev.core2;

public class Connector extends Visualiser {
	Connector(Model model, String id, int parmNum, int xpos, int ypos) {
		super(model, id + "_" + parmNum, "<<parameter " + parmNum + ">>", xpos, ypos);
		btnInfo.dispose();
		btnEdit.dispose();
		btnRun.dispose();
		pack();
	}
}
