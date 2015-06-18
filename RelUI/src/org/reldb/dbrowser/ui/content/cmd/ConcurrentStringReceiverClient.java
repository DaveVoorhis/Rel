package org.reldb.dbrowser.ui.content.cmd;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.reldb.rel.client.connection.string.StringReceiverClient;

public abstract class ConcurrentStringReceiverClient {

	private static final int threadLoadMax = 10;
	
	private static class QueueEntry {
		Exception exception;
		String string;
		QueueEntry(Exception exception) {this.exception = exception; this.string = null;}
		QueueEntry(String string) {this.string = string; this.exception = null;}
		QueueEntry() {this.string = null; this.exception = null;}
		public String toString() {
			if (string != null)
				return string;
			else if (exception != null)
				return "Exception: " + exception.toString();
			else
				return "EOL";
		}
		public boolean isEOL() {
			return (string == null && exception == null);
		}
	}
	
	private StringReceiverClient connection;
	private Display display;
	private Composite parent;
	
	private BlockingQueue<QueueEntry> rcache;
	
	public ConcurrentStringReceiverClient(Composite parent, StringReceiverClient connection) {
		this.parent = parent;
		display = parent.getDisplay();
		this.connection = connection;
	}

	private abstract class Runner {
		private boolean running;
		private int updateCount = 0;
		private int updateMax = 0;
		public Runner() {
			rcache = new LinkedBlockingQueue<QueueEntry>();
			running = true;
			// UI updater thread
			new Thread(new Runnable() {
				@Override
				public void run() {
					updateCount = 0;
					updateMax = 0;
					while (running) {
						// wait for data to show up
						QueueEntry awaitedEntry;
						try {
							awaitedEntry = rcache.take();
						} catch (InterruptedException e1) {
							continue;
						}
						if (display.isDisposed()) {
							running = false;
							return;
						}
						display.syncExec(new Runnable() {
							@Override
							public void run() {
								if (!parent.isDisposed()) {
									try {
										QueueEntry r = awaitedEntry;
										int threadLoadCount = 0;
										boolean nullUpdate = false;
										do {
											if (r.isEOL()) {
												running = false;
												finished();
												return;
											} 
											else if (r.string != null) {
												received(r.string);
												if (++updateCount > updateMax) {
													// update every so often, so we can see what's going on
													update();
													updateCount = 0;
													updateMax++;
													nullUpdate = false;
												} else
													nullUpdate = true;
												if (++threadLoadCount > threadLoadMax) {
													// exit every so often, because staying in syncExec too long causes UI lag
													break;
												}
											}
											else if (r.exception != null) {
												running = false;
												received(r.exception);
												finished();
												return;
											}
										} while ((r = rcache.poll(10, TimeUnit.MILLISECONDS)) != null);
										if (nullUpdate)
											update();
									} catch (InterruptedException e) {
										finished();
										return;
									}
								}
							}
						});
					}
				}
			}).start();
			// Query runner thread
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						doQuery();
					} catch (IOException e) {
						rcache.add(new QueueEntry(e));
					}
				}
			}).start();
			// Query processor thread
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						String r;
						while ((r = connection.receive()) != null) {
							rcache.add(new QueueEntry(r));
						}
						rcache.add(new QueueEntry());
					} catch (IOException e) {
						rcache.add(new QueueEntry(e));
					}
				}
			}).start();
		}
		public abstract void doQuery() throws IOException;
	}
	
	public String getInitialServerResponse() {
		try {
			return connection.getServerAnnouncement();
		} catch (IOException e) {
			return "Unable to obtain server announcement.";
		}
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

	public void reset() {
		try {
			connection.reset();
			if (rcache == null)
				return;
			rcache.add(new QueueEntry("Cancel."));
			rcache.add(new QueueEntry());
			rcache.clear();
			rcache.add(new QueueEntry("Cancel."));
			rcache.add(new QueueEntry());
		} catch (IOException e) {
			System.out.println("ConcurrentStringReceiverClient: Exception during reset(): " + e);
		}
	}

	public void close() {
		try {
			connection.reset();
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
