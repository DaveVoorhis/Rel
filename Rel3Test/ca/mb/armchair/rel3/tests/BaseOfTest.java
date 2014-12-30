package ca.mb.armchair.rel3.tests;

import static org.junit.Assert.*;

import java.io.*;

import ca.mb.armchair.rel3.generator.Generator;
import ca.mb.armchair.rel3.interpreter.*;
import ca.mb.armchair.rel3.languages.tutoriald.parser.ParseException;
import ca.mb.armchair.rel3.values.Value;
import ca.mb.armchair.rel3.exceptions.*;

public class BaseOfTest {
	
	protected static Instance instance = new Instance("./", true, System.out);
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
