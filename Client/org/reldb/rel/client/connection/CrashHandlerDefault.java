package org.reldb.rel.client.connection;

public class CrashHandlerDefault implements CrashHandler {

	private String initialServerResponse;
	
	@Override
	public void process(Throwable t, String lastQuery) {
		System.err.println("CRASH: Rel crash handler caught a crash caused by: " + t.getMessage() + " from query: " + lastQuery);
		t.printStackTrace();
	}

	@Override
	public void setInitialServerResponse(String string) {
		initialServerResponse = string;
	}
	
	public String getInitialServerResponse() {
		return initialServerResponse;
	}

}
