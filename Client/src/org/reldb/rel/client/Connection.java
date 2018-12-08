package org.reldb.rel.client;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.Stack;
import java.util.Vector;

import org.reldb.rel.client.Error;
import org.reldb.rel.client.connection.CrashHandler;
import org.reldb.rel.client.connection.CrashHandlerDefault;
import org.reldb.rel.client.connection.stream.ClientFromURL;
import org.reldb.rel.client.connection.stream.ClientLocalConnection;
import org.reldb.rel.client.connection.stream.InputStreamInterceptor;
import org.reldb.rel.client.connection.stream.StreamReceiverClient;
import org.reldb.rel.client.parser.ResponseAdapter;
import org.reldb.rel.client.parser.ResponseToHTML;
import org.reldb.rel.client.parser.ResponseToHTMLProgressive;
import org.reldb.rel.client.parser.core.ParseException;
import org.reldb.rel.client.parser.core.ResponseParser;
import org.reldb.rel.exceptions.DatabaseFormatVersionException;

/**
 * Connection to a Rel database.
 *
 * @author  dave
 */
public class Connection implements AutoCloseable {
		
	private String dbURL;
	private String serverAnnouncement = "";
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
	
	/** Creates new connection, with additional JAR support for database development. */
	public Connection(String dbURL, boolean createDbAllowed, CrashHandler crashHandler, String[] additionalJars) throws MalformedURLException, IOException, DatabaseFormatVersionException, ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		this.dbURL = dbURL;
		this.crashHandler = crashHandler;
		this.additionalJars = additionalJars;
		// Make sure it exists.
		ClientFromURL.openConnection(dbURL, createDbAllowed, crashHandler, additionalJars).close();
	}
	
	/** Creates new connection. */
	public Connection(String dbURL, boolean createDbAllowed, CrashHandler crashHandler) throws MalformedURLException, IOException, DatabaseFormatVersionException, ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		this(dbURL, createDbAllowed, crashHandler, new String[0]);
	}
	
	/** Creates new connection using CrashHandlerDefault. */
	public Connection(String dbURL, boolean createDbAllowed) throws MalformedURLException, IOException, DatabaseFormatVersionException, ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		this(dbURL, createDbAllowed, new CrashHandlerDefault());
	}
	
	/** Creates new connection. Error thrown if database doesn't exist. */
	public Connection(String dbURL) throws MalformedURLException, IOException, DatabaseFormatVersionException, ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		this(dbURL, false);
	}

	@Override
	public void close() throws Exception {
	}

	/** Attempts update of a database. 
	 * @throws IOException 
	 * @throws DatabaseFormatVersionException */
	public static void convertToLatestFormat(String dbURL, PrintStream conversionOutput, String[] additionalJars) throws DatabaseFormatVersionException, IOException {
		ClientLocalConnection.convertToLatestFormat(dbURL, conversionOutput, additionalJars);
	}
	
	public String getDbURL() {
		return dbURL;
	}
	
	public CrashHandler getCrashHandler() {
		return crashHandler;
	}
	
	public String[] getAdditionalJars() {
		return additionalJars;
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
		(new Thread(() -> {
			try {
				action.run(client);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		})).start();
	}
	
	private Response launchParser(final Action sendAction, final Action receiveComplete) {
		final Response response = new Response();
		final StreamReceiverClient client;
		try {
			client = ClientFromURL.openConnection(dbURL, false, crashHandler, additionalJars);
		} catch (Exception e) {
			response.setResult(new Error(e.toString()));
			return response;
		}
		(new Thread(() -> {
			ErrorMessageTrap errorMessageTrap;
			ResponseParser parser;
			try {
				errorMessageTrap = new ErrorMessageTrap(client.getServerResponseInputStream());
				parser = new ResponseParser(errorMessageTrap);
			} catch (Throwable e1) {
				e1.printStackTrace();
				return;
			}
			parser.setResponseHandler(new ResponseAdapter() {
				Stack<Value> valueReceiver = new Stack<Value>();
				Stack<Heading> headingReceiver = new Stack<Heading>();
				private void endData() {
					Value value = valueReceiver.pop();
					if (valueReceiver.size() > 0)
						valueReceiver.peek().addValue(value, false);
					else
						response.setResult(value);
				}
				public void beginHeading(String typeName) {
					Heading heading = new Heading(typeName);
					headingReceiver.push(heading);
				}
				public Heading endHeading() {
					Heading heading = headingReceiver.pop();
					if (headingReceiver.size() > 0)
						headingReceiver.peek().addAttributeType(heading);
					return heading;
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
				public void primitive(String value, boolean quoted) {
					valueReceiver.peek().addValue(new Scalar(value, quoted), quoted);
				}
				public void beginContainerBody(int depth, Heading heading, String typeName) {
					Tuples tuples = (heading == null) ? new Tuples(typeName) : new Tuples(heading);
					if (depth == 0)
						response.setResult(tuples);
					valueReceiver.push(tuples);
				}
				public void endContainer(int depth) {
					Tuples tuples = (Tuples)valueReceiver.peek();
					tuples.insertNullTuple();
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
				// Debug client-side response parser problems here.
				// System.out.println("Connection: " + e);
				response.setResult(new Error(errorMessageTrap.toString()));
			}
			try {
				if (receiveComplete != null)
					receiveComplete.run(client);
				client.close();
			} catch (IOException e) {
				System.out.println("Connection: run failed: " + e);
				e.printStackTrace();
			}
		})).start();
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
			client = ClientFromURL.openConnection(dbURL, false, crashHandler, additionalJars);
		} catch (Exception e) {
			htmlReceiver.emitInitialHTML("Unable to open connection: " + e.toString().replace(" ", "&nbsp;"));
			return;
		}
		(new Thread(() -> {
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
			} catch (Throwable e1) {
				e1.printStackTrace();
				return;
			}
			try {
				parser.parse();
			} catch (ParseException e) {
				// Debug client-side response parser problems here.
				// System.out.println("Connection: " + e);
				htmlReceiver.emitInitialHTML(errorMessageTrap.toString().replace(" ", "&nbsp;"));
			}
			try {
				client.close();
			} catch (IOException e) {
				System.out.println("Connection: close failed: " + e);
				e.printStackTrace();
			}
		})).start();
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
	
	public static class ExecuteResult {
		private Response response;
		public ExecuteResult(Response response) {
			this.response = response;
		}
		public boolean failed() {
			return (response == null || response.getResult() instanceof Error); 
		}
		public String getErrorMessage() {
			if (response == null)
				return "Connection failed.";
			if (response.getResult() instanceof Error) {
				String error = ((Error)response.getResult()).getErrorMsg();
				int EOTposition = error.indexOf("<EOT>");
				if (EOTposition >= 0)
					error = error.substring(0, EOTposition);
				return error;
			}
			return "Unknown error.";
		}
	}
	
	/** Execute query. */
	public ExecuteResult exec(String query) {
		try {
			return new ExecuteResult(execute(query));
		} catch (IOException e1) {
			return new ExecuteResult(null);
		}
	}

	/** Evaluate query. */
	public Value eval(String query, int queryWaitMilliseconds) {
		Value response;
		try {
			response = evaluate(query).awaitResult(queryWaitMilliseconds);
		} catch (IOException e) {
			System.out.println("Connection: Error: " + e);
			e.printStackTrace();
			return null;
		}
		if (response instanceof Error) {
			Error error = (Error)response;
			System.out.println("Connection: Query evaluate returns error. " + query + "\n" + error.getErrorMsg());
			return null;
		}
		if (response == null) {
			System.out.println("Connection: Unable to obtain query results.");
			return null;
		}
		return response;
	}
	
	/** Evaluate query that returns tuples. */
	public Tuples getTuples(String query, int queryWaitMilliseconds) {
		return (Tuples)eval(query, queryWaitMilliseconds);		
	}
	
}
