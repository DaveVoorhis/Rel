package org.reldb.rel.tests;

import org.reldb.rel.exceptions.DatabaseFormatVersionException;
import org.reldb.rel.exceptions.ExceptionFatal;
import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.interpreter.Evaluation;
import org.reldb.rel.v0.interpreter.Instance;
import org.reldb.rel.v0.interpreter.Interpreter;
import org.reldb.rel.v0.languages.tutoriald.parser.ParseException;
import org.reldb.rel.v0.values.Value;

import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class BaseOfTest {
	
	protected static Instance getInstance() {
		try {
			return new Instance("./target/TestDB", true, System.out);
		} catch (DatabaseFormatVersionException e) {
			throw new ExceptionFatal("Database already exists and is in an older format.");
		}
	}
	
	protected static Instance instance = getInstance();
	protected static Interpreter interpreter = new Interpreter(instance.getDatabase(), System.out);
	protected static Generator generator = interpreter.getGenerator();
	
	protected static Evaluation testEvaluate(String s) {
		try {
			return interpreter.evaluate(s);
		} catch (ParseException pe) {
			throw new ExceptionSemantic("BaseOfTest: " + pe);
		}
	}
	
	public static void assertValueEquals(Value v1, Value v2) {
		assertEquals(v1.compareTo(v2), 0);
	}
	
	protected static void testEquals(String expected, String src) {
		Evaluation v = testEvaluate(src);
		PrintStream stringStream = new StringStream();
	 	v.toStream(stringStream);
		assertEquals(expected, stringStream.toString());		
	}
	
}
