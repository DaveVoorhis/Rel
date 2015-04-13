package org.reldb.rel.client;

import java.io.*;
import java.util.Stack;
import java.util.Vector;

import org.reldb.rel.client.connection.CrashHandler;
import org.reldb.rel.client.connection.stream.ClientFromURL;
import org.reldb.rel.client.connection.stream.InputStreamInterceptor;
import org.reldb.rel.client.connection.stream.StreamReceiverClient;
import org.reldb.rel.client.parser.ResponseAdapter;
import org.reldb.rel.client.parser.ResponseToHTML;
import org.reldb.rel.client.parser.ResponseToHTMLProgressive;
import org.reldb.rel.client.parser.core.ParseException;
import org.reldb.rel.client.parser.core.ResponseParser;

/**
 * Connection to a Rel database.
 *
 * @author  dave
 */
public class Connection {
		
	private String dbURL;
	private String serverAnnouncement = "";
	private boolean createDbAllowed;
	private CrashHandler crashHandler;
	private String[] additionalJars;
	
	private final static String errorPrefix = "ERROR:";
	
	private static class Snippet {
		private StringBuffer buffer = new StringBuffer();
		public void clear() {
			buffer = null;
		}
		public void create() {
			buffer = new StringBuffer();
		}
		public boolean isClear() {
			return (buffer == null);
		}
		public void append(int n) {
			buffer.append((char)n);
		}
		public String toString() {
			if (buffer == null)
				return "";
			return buffer.toString();
		}
	}
	
	/** Creates new connection */
	public Connection(String dbURL, boolean createDbAllowed, CrashHandler crashHandler, String[] additionalJars) {
		this.dbURL = dbURL;
		this.createDbAllowed = createDbAllowed;
		this.crashHandler = crashHandler;
		this.additionalJars = additionalJars;
	}
	
	private abstract class Action {
		public abstract void run(StreamReceiverClient client) throws IOException;
	};
	
	public interface CharacterListener {
		public void receive(int character);
	}
	
	private Vector<CharacterListener> characterListeners = new Vector<CharacterListener>();
	
	/** Add listener which receives every character in the response. */
	public void addCharacterListener(CharacterListener listener) {
		characterListeners.addElement(listener);
	}

	/** Remove listener which receives every character in the response. */
	public void removeCharacterListener(CharacterListener listener) {
		characterListeners.removeElement(listener);
	}
	
	/** Override to obtain every character received in the response.  If super.capturedResponseStream(character) is not
	 * called in an overridden capturedResponseStream, addCharacterListener becomes a no-op. */
	protected void capturedResponseStream(int character) {
		for (CharacterListener listener: characterListeners)
			listener.receive(character);
	}
	
	private class ErrorMessageTrap extends InputStreamInterceptor {
		private Snippet errorMessageTrap = new Snippet();
		ErrorMessageTrap(InputStream input) {
			super(input);
		}
		public void interceptedRead(int r) {
			capturedResponseStream(r);
			if (errorMessageTrap.isClear()) {
				if (r == '\n')
					errorMessageTrap.create();
			} else {
				errorMessageTrap.append(r);
				String possibleErrorStr = errorMessageTrap.toString();
				if (possibleErrorStr.length() >= errorPrefix.length() && !possibleErrorStr.startsWith(errorPrefix))
					errorMessageTrap.clear();
			}
		}
		public String toString() {
			return errorMessageTrap.toString();
		}
	}
	
	private void launchTransmitter(final StreamReceiverClient client, final Action action) {
		// Transmit needs to run in separate thread for local connection, or the pipe will deadlock.
		Thread sendRunner = new Thread() {
			public void run() {
				try {
					action.run(client);
				} catch (IOException e) {
					e.printStackTrace();
					return;
				}						
			}
		};
		sendRunner.start();
	}
	
	private Response launchParser(final Action sendAction, final Action receiveComplete) {
		final Response response = new Response();
		final StreamReceiverClient client;
		try {
			client = ClientFromURL.openConnection(dbURL, createDbAllowed, crashHandler, additionalJars);
		} catch (Exception e) {
			response.setResult(new Error(e.toString()));
			return response;
		}
		Thread parseRunner = new Thread() {
			public void run() {
				ErrorMessageTrap errorMessageTrap;
				ResponseParser parser;
				try {
					errorMessageTrap = new ErrorMessageTrap(client.getServerResponseInputStream());
					parser = new ResponseParser(errorMessageTrap);
				} catch (IOException e1) {
					e1.printStackTrace();
					return;
				}
				parser.setResponseHandler(new ResponseAdapter() {
					Stack<Value> valueReceiver = new Stack<Value>();
					Stack<Heading> headingReceiver = new Stack<Heading>();
					private void endData() {
						Value value = valueReceiver.pop();
						if (valueReceiver.size() > 0)
							valueReceiver.peek().addValue(value);
						else
							response.setResult(value);					
					}
					public void beginHeading() {
						headingReceiver.push(new Heading());
					}
					public void endHeading() {
						Heading heading = headingReceiver.pop();
						if (headingReceiver.size() > 0)
							headingReceiver.peek().addAttributeType(heading);
						else
							((Tuples)valueReceiver.peek()).setHeading(heading);
					}
					public void attributeName(String name) {
						headingReceiver.peek().addAttributeName(name);
					}
					public void typeReference(String name) {
						headingReceiver.peek().addAttributeType(new ScalarType(name));
					}
					public void beginScalar(int depth) {
						valueReceiver.push(new Scalar());
					}
					public void endScalar(int depth) {
						endData();
					}
					public void beginPossrep(String name) {
						valueReceiver.push(new Selector(name));
					}
					public void endPossrep() {
						endData();
					}
					public void primitive(String value) {
						valueReceiver.peek().addValue(new Scalar(value));
					}
					public void beginContainer(int depth) {
						if (depth == 0) {
							Tuples tuples = new Tuples(client);
							valueReceiver.push(tuples);
							response.setResult(tuples);
						} else {
							valueReceiver.push(new Tuples(new Heading()));
						}
					}
					public void endContainer(int depth) {
						((Tuples)valueReceiver.peek()).insertNullTuple();
						endData();
					}
					public void beginTuple(int depth) {
						valueReceiver.push(new Tuple());
					}
					public void endTuple(int depth) {
						endData();
					}
					public void attributeNameInTuple(int depth, String name) {
						((Tuple)valueReceiver.peek()).addAttributeName(name);
					}
				});
				try {
					parser.parse();
				} catch (ParseException e) {
					response.setResult(new Error(errorMessageTrap.toString()));
				}
				try {
					if (receiveComplete != null)
						receiveComplete.run(client);
					client.close();
				} catch (IOException e) {
					System.out.println("Connection: close failed: " + e);
					e.printStackTrace();
				}
			}
		};
		parseRunner.start();
		launchTransmitter(client, sendAction);
		return response;
	}
	
	public interface HTMLReceiver {
		public void emitInitialHTML(String s);
		public void endInitialHTML();
		public void emitProgressiveHTML(String s);
		public void endProgressiveHTMLRow();
	}
	
	private void launchParserToHTML(final Action action, final HTMLReceiver htmlReceiver) {
		final StreamReceiverClient client;
		try {
			client = ClientFromURL.openConnection(dbURL, createDbAllowed, crashHandler, additionalJars);
		} catch (Exception e) {
			htmlReceiver.emitInitialHTML("Unable to open connection: " + e.toString().replace(" ", "&nbsp;"));
			return;
		}
		Thread parseRunner = new Thread() {
			public void run() {
				ErrorMessageTrap errorMessageTrap;
				ResponseToHTML parser;
				try {
					errorMessageTrap = new ErrorMessageTrap(client.getServerResponseInputStream());
					parser = new ResponseToHTMLProgressive(errorMessageTrap) {						
						public void emitInitialHTML(String s) {
							htmlReceiver.emitInitialHTML(s);
						}
						public void endInitialHTML() {
							htmlReceiver.endInitialHTML();
						}
						public void emitProgressiveHTML(String s) {
							htmlReceiver.emitProgressiveHTML(s);
						}
						public void endProgressiveHTMLRow() {
							htmlReceiver.endProgressiveHTMLRow();
						}
					};
				} catch (IOException e1) {
					e1.printStackTrace();
					return;
				}
				try {
					parser.parse();
				} catch (ParseException e) {
					htmlReceiver.emitInitialHTML(errorMessageTrap.toString().replace(" ", "&nbsp;"));
				}
				try {
					client.close();
				} catch (IOException e) {
					System.out.println("Connection: close failed: " + e);
					e.printStackTrace();
				}
			}
		};
		parseRunner.start();
		launchTransmitter(client, action);
	}

	private static class Indicator {
		boolean indicated = false;
		public void setIndicated(boolean indicated) {this.indicated = indicated;}
		boolean isIndicated() {return this.indicated;}
	}
	
	/** Execute query and return Response. */
	public Response execute(final String input) throws IOException {
		final Indicator finished = new Indicator();
		Response response = launchParser(
			new Action() {
				public void run(StreamReceiverClient client) throws IOException {
					client.sendExecute(input);
				}
			}, 
			new Action() {
				public void run(StreamReceiverClient client) throws IOException {
					synchronized (finished) {
						finished.setIndicated(true);
						finished.notify();
					}
				}
			}
		);
		synchronized (finished) {
			while (!finished.isIndicated())
				try {
					finished.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}
		return response;
	}
	
	/** Get server announcement. */
	public String getServerAnnouncement() {
		return serverAnnouncement;
	}
	
	/** Evaluate query and return Response. */
	public Response evaluate(final String input) throws IOException {
		return launchParser(new Action() {
			public void run(StreamReceiverClient client) throws IOException {
				client.sendEvaluate(input);
			}
		}, null);
	}
	
	/** Evaluate query and emit response as HTML. */
	public void evaluate(final String input, final HTMLReceiver htmlReceiver) {
		launchParserToHTML(new Action() {
			public void run(StreamReceiverClient client) throws IOException {
				client.sendEvaluate(input);
			}
		}, htmlReceiver);
	}

}
