package ca.mb.armchair.rel3.shared;

/** A class for holding default values shared by the client and the server. */
public class Defaults {

	private Defaults() {}
	
	/** Get the default port for the client and server. */
	public static int getDefaultPort() {
		return 5514;
	}
}
