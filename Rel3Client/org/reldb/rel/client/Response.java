package org.reldb.rel.client;

public class Response {

	private Value result = null;
	
	Response() {}
	
	void setResult(Value result) {
		synchronized (this) {
			if (this.result != null)
				return;
			this.result = result;
			notifyAll();
		}
	}
	
	public Value getResult() {
		return result;
	}
	
	public Value awaitResult(long waitMillisecondsBeforeTimeout) {
		synchronized (this) {
			if (result != null)
				return result;
			try {
				wait(waitMillisecondsBeforeTimeout);
			} catch (InterruptedException e) {
			}
			return result;
		}
	}
}
