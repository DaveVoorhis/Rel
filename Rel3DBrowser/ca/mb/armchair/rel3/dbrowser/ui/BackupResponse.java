package ca.mb.armchair.rel3.dbrowser.ui;

class BackupResponse {
	private String message;
	private String title;
	private int messageType;
	private boolean succeeded;
	public BackupResponse(String message, String title, int messageType, boolean succeeded) {
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
		javax.swing.JOptionPane.showMessageDialog(null, getMessage(), title, messageType);
	}
}