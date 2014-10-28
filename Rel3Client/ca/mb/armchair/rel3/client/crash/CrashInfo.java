package ca.mb.armchair.rel3.client.crash;

import javax.swing.tree.TreeNode;

public class CrashInfo {
	private String whatHappened;
	private String userEmail;
	private TreeNode report;

	public CrashInfo() {}

	public CrashInfo(String whatHappened, String userEmail, TreeNode report) {
		setWhatHappened(whatHappened);
		setUserEmail(userEmail);
		setReport(report);
	}
	
	public String getWhatHappened() {
		return whatHappened;
	}
	
	public void setWhatHappened(String whatHappened) {
		this.whatHappened = whatHappened;
	}
	
	public String getUserEmail() {
		return userEmail;
	}
	
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	
	public TreeNode getReport() {
		return report;
	}
	
	public void setReport(TreeNode report) {
		this.report = report;
	}
}