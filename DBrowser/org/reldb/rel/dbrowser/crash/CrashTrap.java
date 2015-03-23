package org.reldb.rel.dbrowser.crash;

import org.reldb.rel.client.stream.CrashHandler;

public class CrashTrap implements CrashHandler {
	private String lastQuery;
	private String serverInitialResponse;
	private String clientVersion;
	
	public CrashTrap(String lastQuery, String serverInitialResponse, String clientVersion) {
		this.lastQuery = lastQuery;
		this.serverInitialResponse = serverInitialResponse;
	}
	
	@Override
	public void process(Throwable t) {
		CrashDialog.launch(t, lastQuery, serverInitialResponse, clientVersion);
	}

	@SuppressWarnings("null")
	public static void main(String args[]) {
		CrashTrap crashTrap = new CrashTrap("- Test -", "- Test -", "- Test Client 1.0 -");
		try {
			Object nullObject = null;
			nullObject.toString();
		} catch (Exception e) {
			crashTrap.process(e);
		}
	}

}
