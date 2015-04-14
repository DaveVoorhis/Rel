package org.reldb.relui.dbui.backup;

import org.eclipse.jface.dialogs.MessageDialog;
import org.reldb.relui.dbui.DbMain;

class BackupResponse {
	public static enum ResponseType {
		INFORMATION,
		ERROR
	};
	
	private String message;
	private String title;
	private ResponseType messageType;
	private boolean succeeded;
	
	public BackupResponse(String message, String title, ResponseType messageType, boolean succeeded) {
		this.message = message;
		this.title = title;
		this.messageType = messageType;
		this.succeeded = succeeded;
	}
	
	public boolean isSuccessful() {
		return succeeded;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void showMessage() {
		if (messageType == ResponseType.ERROR)
			MessageDialog.openError(DbMain.getShell(), title, message);
		else if (messageType == ResponseType.INFORMATION)
			MessageDialog.openInformation(DbMain.getShell(), title, message);
	}
}