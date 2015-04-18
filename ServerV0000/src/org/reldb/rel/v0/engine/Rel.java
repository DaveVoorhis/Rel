package org.reldb.rel.v0.engine;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

import org.reldb.rel.exceptions.DatabaseFormatVersionException;
import org.reldb.rel.exceptions.ExceptionFatal;
import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.v0.interpreter.ClassPathHack;
import org.reldb.rel.v0.interpreter.Instance;
import org.reldb.rel.v0.interpreter.Interpreter;
import org.reldb.rel.v0.interpreter.ParseExceptionPrinter;
import org.reldb.rel.v0.languages.tutoriald.parser.ParseException;
import org.reldb.rel.v0.languages.tutoriald.parser.TokenMgrError;
import org.reldb.rel.v0.version.Version;

/** Convenient access point for running an embedded or stand-alone interpreter. */

public class Rel {
	
	private Interpreter interpreter;
	private Instance instance;
	private PipedInputStream input;
	private PrintStream output;
	
	private static void buildClasspath() throws IOException {
		ClassPathHack.addFile(Version.getBerkeleyDbJarFilename());
		ClassPathHack.addFile("relshared.jar");	
		ClassPathHack.addFile("ecj-4.4.2.jar");
	}
	
	/** Convenient runner for a stand-alone Rel interpreter. 
	 * @throws IOException */
	public static void main(String[] args) throws IOException {
		buildClasspath();
		org.reldb.rel.v0.interpreter.Instance.main(args);
	}
	
	/** Open this database and back it up to the named file. */
	public static void backup(String databaseDir, String backupFileName) throws IOException, ParseException, DatabaseFormatVersionException {
		buildClasspath();
		PrintStream output = new PrintStream(backupFileName);
		Instance instance = new Instance(databaseDir, false, output);
		Interpreter interpreter = new Interpreter(instance.getDatabase(), output);
		interpreter.interpret("BACKUP;");
		output.close();
		instance.close();
	}

	/** Convert this database to the latest format, if necessary.  Throw exception if not necessary.  Normally only needed if invoking
	 * the constructor throws DatabaseFormatVersionException. */
	public static void convertToLatestFormat(String databaseDir, PrintStream conversionOutput, String[] additionalJars) throws DatabaseFormatVersionException, IOException {
		buildClasspath();
		Instance.convertToLatestFormat(new File(databaseDir), conversionOutput, additionalJars);
	}
	
	/** Establish a connection with this server. */
	public Rel(String databaseDir, boolean createDbAllowed, String[] additionalJars) throws IOException, DatabaseFormatVersionException {
		buildClasspath();
		input = new PipedInputStream();
		PipedOutputStream pipeOutput = new PipedOutputStream(input);
		output = new PrintStream(pipeOutput, true);		
		instance = new Instance(databaseDir, createDbAllowed, output, additionalJars);
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
			throw t;
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

	public void reset() {
		interpreter.reset();
		output.println();
		output.println("Cancel.");
	}

	public void close() {
		instance.close();
		output.close();
	}
	
}
