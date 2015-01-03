import ca.mb.armchair.rel3.dbrowser.monitor.BrowserLog;

/** Alternative startup without execution monitor.  Useful when developing and debugging DBrowser itself. */
public class DBrowserWithoutMonitor {	
	public static void main(String args[]) {
			BrowserLog.main(args);
	}
}
