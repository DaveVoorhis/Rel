package org.reldb.rel.dbrowser.crash;

import org.reldb.rel.client.connection.CrashHandler;

public class CrashTrap implements CrashHandler {
	private String serverInitialResponse = "";
	private String clientVersion;
	
	public CrashTrap(String clientVersion) {
		this.clientVersion = clientVersion;
	}
	
	public void setInitialServerResponse(String s) {
		this.serverInitialResponse = s;
	}
	
	@Override
	public void process(Throwable t, String lastQuery) {
		CrashDialog.launch(t, lastQuery, serverInitialResponse, clientVersion);
	}

	@SuppressWarnings("null")
	public static void main(String args[]) {
		CrashTrap crashTrap = new CrashTrap("- Test Client 1.0 -");
		try {
			Object nullObject = null;
			nullObject.toString();
		} catch (Exception e) {
			crashTrap.process(e, "- Test -");
		}
	}

}
