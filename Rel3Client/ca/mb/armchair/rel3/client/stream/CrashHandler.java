package ca.mb.armchair.rel3.client.stream;

// Defines crash handlers for ClientConnection.  Allows us to intercept otherwise unhandled exceptions in ClientLocalConnection and
// ClientNetworkConnection and pass them to "phone home" mechanisms, loggers, etc.
public interface CrashHandler {
	void process(Throwable t);
}
