package org.reldb.dbrowser.ui.content.rel.script;

import org.reldb.dbrowser.ui.content.rel.RelPanel;
import org.reldb.dbrowser.ui.content.rev.Rev;

public class CmdPlayer extends CmdView {	
	public CmdPlayer(RelPanel relPanel) {
		super(relPanel, Rev.READONLY);
	}
}
