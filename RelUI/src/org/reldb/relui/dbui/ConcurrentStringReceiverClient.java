package org.reldb.relui.dbui;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;

import org.eclipse.swt.widgets.Display;
import org.reldb.rel.client.connection.string.StringReceiverClient;

public abstract class ConcurrentStringReceiverClient {

	private static final int cachedLineMaximum = 100;
	
	private StringReceiverClient connection;
	private Display display;
	private DbTab tab;
//	private ConcurrentQueue<String> queue = new ConcurrentLinkedQueue<String>();
	
	public ConcurrentStringReceiverClient(DbTab dbTab) {
		this.connection = dbTab.getConnection();
		tab = dbTab;
		display = dbTab.getDisplay();
	}

	private abstract class Runner {
		public Runner() {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Integer clearedSemaphore = 0;
						doQuery();
						String r;
						Collection<String> rcache = new LinkedList<String>();
						while ((r = connection.receive()) != null) {
							boolean cacheFilled = false;
							synchronized(rcache) {
								rcache.add(r);
								cacheFilled = (rcache.size() > cachedLineMaximum);
							}
							if (cacheFilled) {
								display.asyncExec(new Runnable() {
									@Override
									public void run() {
										if (!tab.isDisposed()) {
											synchronized(rcache) {
												for (String r: rcache)
													received(r);
												rcache.clear();
												clearedSemaphore.notify();
											}
											update();
										}
									}
								});
								try {
									clearedSemaphore.wait();
								} catch (InterruptedException e) {
								}
							}
						}
						display.asyncExec(new Runnable() {
							@Override
							public void run() {
								if (!tab.isDisposed()) {
									synchronized(rcache) {
										for (String r: rcache)
											received(r);
									}
									update();
									finished();
								}
							}
						});
					} catch (IOException e) {
						display.asyncExec(new Runnable() {
							@Override
							public void run() {
								if (!tab.isDisposed()) {
									received(e);
									update();
									finished();
								}
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
	
	/** Override to perform expensive periodic display updates after having received multiple strings.  Safe to update SWT widgets. */
	public abstract void update();
	
}
