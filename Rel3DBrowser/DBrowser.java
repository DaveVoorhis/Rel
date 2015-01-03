import ca.mb.armchair.rel3.dbrowser.monitor.Monitor;
import ca.mb.armchair.rel3.dbrowser.monitor.BrowserLog;

public class DBrowser {
	
	public static void main(String args[]) {
		if (args.length > 0 && args[0].equals("-nomonitor")) {
			String[] newArgs = new String[args.length - 1];
			System.arraycopy(args, 1, newArgs, 0, newArgs.length);
			BrowserLog.main(newArgs);
		} else
			Monitor.main(args);
	}
}
