import java.io.IOException;

import ca.mb.armchair.rel3.interpreter.ClassPathHack;

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
		ca.mb.armchair.rel3.interpreter.Instance.main(args);
	}
}
