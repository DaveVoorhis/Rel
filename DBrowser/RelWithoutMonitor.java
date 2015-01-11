import org.reldb.rel.dbrowser.monitor.BrowserLog;

/** Alternative startup without execution monitor.  Useful when developing and debugging Rel itself. */
public class RelWithoutMonitor {	
	public static void main(String args[]) {
			BrowserLog.main(args);
	}
}
