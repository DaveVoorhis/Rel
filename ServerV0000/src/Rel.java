import java.io.IOException;

import org.reldb.rel.v0.interpreter.ClassPathHack;

/** Convenient runner for a stand-alone Rel interpreter. */

public class Rel {
	// Stand-alone invocation
	public static void main(String[] args) {
		try {
			ClassPathHack.addFile("je.jar");
			ClassPathHack.addFile("relshared.jar");
		} catch (IOException ioe) {
			System.out.println(ioe.toString());
			return;
		}
		org.reldb.rel.v0.interpreter.Instance.main(args);
	}
}
