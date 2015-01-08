package org.reldb.rel.v0.engine;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

import org.reldb.rel.exceptions.ExceptionFatal;
import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.v0.interpreter.ClassPathHack;
import org.reldb.rel.v0.interpreter.Instance;
import org.reldb.rel.v0.interpreter.Interpreter;
import org.reldb.rel.v0.interpreter.ParseExceptionPrinter;
import org.reldb.rel.v0.languages.tutoriald.parser.ParseException;
import org.reldb.rel.v0.languages.tutoriald.parser.TokenMgrError;

/** Convenient access point for running a stand-alone interpreter. */

public class Rel {
	
	private Interpreter interpreter;
	private PipedInputStream input;
	private PrintStream output;

	/** Establish a connection with this server. */
	public Rel(String databaseDir, boolean createDbAllowed) throws IOException {
		ClassPathHack.addFile("je.jar");
		ClassPathHack.addFile("relshared.jar");
		input = new PipedInputStream();
		PipedOutputStream pipeOutput = new PipedOutputStream(input);
		output = new PrintStream(pipeOutput, true);		
		Instance instance = new Instance(databaseDir, createDbAllowed, output);
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

	private void send(Action action) throws IOException, ExceptionFatal {
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
			throw et;
		} catch (Throwable t) {
			interpreter.reset();
			output.println("ERROR: " + t);
			t.printStackTrace(output);
			t.printStackTrace();
			throw new ExceptionFatal("ERROR: ", t);
		}
		output.println("<EOT>");			
	}
	
	public void sendEvaluate(final String source) throws IOException {
		send(new Action() {
			public void execute() throws ParseException {
				interpreter.evaluate(source).toStream(output);
				output.println();					
			}
		});
	}
	
	public void sendExecute(final String source) throws IOException {
		send(new Action() {
			public void execute() throws ParseException {
				interpreter.interpret(source);
				output.println("\nOk.");
			}
		});
	}
	
}
