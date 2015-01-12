import org.reldb.rel.dbrowser.monitor.Monitor;
import org.reldb.rel.dbrowser.ui.Browser;

public class Rel {
	
	public static void main(String args[]) {
		if (args.length > 0 && args[0].equals("-nomonitor")) {
			String[] newArgs = new String[args.length - 1];
			System.arraycopy(args, 1, newArgs, 0, newArgs.length);
			Browser.main(newArgs);
		} else
			Monitor.main(args);
	}
}
