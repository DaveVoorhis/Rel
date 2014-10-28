package ca.mb.armchair.rel3.client.stream;

import java.io.*;

import ca.mb.armchair.rel3.interpreter.*;
import ca.mb.armchair.rel3.client.utilities.ClassPathHack;
import ca.mb.armchair.rel3.client.utilities.ParseExceptionPrinter;
import ca.mb.armchair.rel3.exceptions.*;
import ca.mb.armchair.rel3.languages.tutoriald.parser.ParseException;
import ca.mb.armchair.rel3.languages.tutoriald.parser.TokenMgrError;

public class ClientLocalConnection extends ClientConnection implements StreamReceiverClient {
	
	private Interpreter interpreter;
	private PipedInputStream input;
	private PrintStream output;

	/** Establish a connection with a server. */
	public ClientLocalConnection(String databaseDir) throws IOException {
		ClassPathHack.addFile("je.jar");
		ClassPathHack.addFile("Rel.jar");
		ClassPathHack.addFile("relshared.jar");
		ClassPathHack.addFile("commons-codec-1.4.jar");
		ClassPathHack.addFile("commons-logging-1.1.1.jar");
		ClassPathHack.addFile("httpclient-4.1.3.jar");
		ClassPathHack.addFile("httpclient-cache-4.1.3.jar");
		ClassPathHack.addFile("httpcore-4.1.4.jar");
		ClassPathHack.addFile("httpmime-4.1.3.jar");
		input = new PipedInputStream();
		PipedOutputStream pipeOutput = new PipedOutputStream(input);
		output = new PrintStream(pipeOutput, true);
		Instance instance = new Instance(databaseDir);
		interpreter = new Interpreter(instance.getDatabase(), output);
		instance.announceActive(output);
		output.println("<EOT>");
	}
	
	public InputStream getServerResponseInputStream() throws IOException {
		return input;
	}
	
	private static abstract class Action {
		public abstract void execute() throws ParseException;
	}

	private void send(Action action, CrashHandler errorHandler) throws IOException {
		try {
			action.execute();
		} catch (ParseException pe) {
			interpreter.reset();
			output.println("ERROR: " + ParseExceptionPrinter.getParseExceptionMessage(pe));
		} catch (TokenMgrError tme) {
			interpreter.reset();
			output.println("ERROR: " + tme.getMessage());
		} catch (ExceptionSemantic es) {
			interpreter.reset();
			output.println("ERROR: " + es.getMessage());
		} catch (ExceptionFatal et) {
			interpreter.reset();
			output.println("ERROR: " + et.getMessage());
			et.printStackTrace(output);
			et.printStackTrace();
			errorHandler.process(et);
		} catch (Throwable t) {
			interpreter.reset();
			output.println("ERROR: " + t);
			t.printStackTrace(output);
			t.printStackTrace();
			errorHandler.process(t);
		}
		output.println("<EOT>");			
	}
	
	public void sendEvaluate(final String source, CrashHandler errorHandler) throws IOException {
		send(new Action() {
			public void execute() throws ParseException {
				interpreter.evaluate(source).toStream(output);
				output.println();					
			}
		}, errorHandler);
	}
	
	public void sendExecute(final String source, CrashHandler errorHandler) throws IOException {
		send(new Action() {
			public void execute() throws ParseException {
				interpreter.interpret(source);
				output.println("\nOk.");
			}
		}, errorHandler);
	}
	
	public void close() throws IOException {
	}
}
