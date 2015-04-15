package org.reldb.dbrowser.ui.crash;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.reldb.rel.client.connection.CrashHandler;

public class CrashTrap implements CrashHandler {
	private String serverInitialResponse = "";
	private Shell shell;
	private String clientVersion;
	
	public CrashTrap(Shell shell, String clientVersion) {
		this.shell = shell;
		this.clientVersion = clientVersion;
	}

	@Override
	public void setInitialServerResponse(String s) {
		this.serverInitialResponse = s;
	}
	
	@Override
	public void process(Throwable t, String lastQuery) {
		CrashDialog.launch(t, lastQuery, serverInitialResponse, shell, clientVersion);
	}

	@SuppressWarnings("null")
	public static void main(String args[]) {
		Display display = new Display();
		Shell shell = new Shell(display);
		CrashTrap crashTrap = new CrashTrap(shell, "- Test Client 1.0 -");
		try {
			Object nullObject = null;
			nullObject.toString();
		} catch (Exception e) {
			crashTrap.process(e, "- Test -");
		}
	}

}
