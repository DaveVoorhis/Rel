package org.reldb.dbrowser.ui.content.rel.script;

import org.reldb.dbrowser.ui.content.rel.RelPanel;
import org.reldb.dbrowser.ui.content.rev.Rev;

public class ScriptPlayer extends ScriptView {	
	public ScriptPlayer(RelPanel relPanel) {
		super(relPanel, Rev.READONLY);
	}
}
