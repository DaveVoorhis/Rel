package org.reldb.relui.dbui;

import java.io.IOException;

import org.eclipse.swt.widgets.Display;
import org.reldb.rel.client.connection.string.StringReceiverClient;

public abstract class ConcurrentStringReceiverClient {

	private StringReceiverClient connection;
	private Display display;
	
	public ConcurrentStringReceiverClient(DbTab dbTab) {
		this.connection = dbTab.getConnection();
		display = dbTab.getDisplay();
	}

	private abstract class Runner {
		public Runner() {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						doQuery();
						String r;
						while ((r = connection.receive()) != null) {
							final String rcvd = r;
							display.asyncExec(new Runnable() {
								@Override
								public void run() {
									received(rcvd);
								}
							});
						}
						display.asyncExec(new Runnable() {
							@Override
							public void run() {
								finished();
							}
						});
					} catch (IOException e) {
						display.asyncExec(new Runnable() {
							@Override
							public void run() {
								received(e);
								finished();
							}
						});
					}
				}
			}).start();
		}
		public abstract void doQuery() throws IOException;
	}
	
	public void sendExecute(final String s) {
		new Runner() {
			public void doQuery() throws IOException {
				connection.sendExecute(s);
			}
		};
	}
	
	public void sendEvaluate(String s) {
		new Runner() {
			public void doQuery() throws IOException {
				connection.sendEvaluate(s);
			}
		};
	}

	/** Override to be notified that a string was received.  This will run in the SWT widget thread so is safe to update SWT widgets. */
	public abstract void received(String s);
	
	/** Override to be notified that an exception occurred.  This will run in the SWT widget thread so is safe to update SWT widgets. */
	public abstract void received(Exception e);
	
	/** Override to be notified that processing has finished.  This will run in the SWT widget thread so is safe to update SWT widgets. */
	public abstract void finished();
	
}
