import java.io.IOException;

import org.reldb.rel.interpreter.ClassPathHack;

/** Convenient runner for the Rel interpreter. */

public class Rel {
		
	public static void main(String[] args) {
		try {
			ClassPathHack.addFile("je.jar");
			ClassPathHack.addFile("relshared.jar");
		} catch (IOException ioe) {
			System.out.println(ioe.toString());
			return;
		}
		org.reldb.rel.interpreter.Instance.main(args);
	}
}
