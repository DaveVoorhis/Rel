package ca.mb.armchair.rel3.tests.main;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.mb.armchair.rel3.tests.BaseOfTest;
import ca.mb.armchair.rel3.values.*;

public class TestLambda3 extends BaseOfTest {

	@BeforeClass
	public static void testLambda3_before() {
		String src =
			"BEGIN;" +
		      "VAR myvar REAL RELATION {x INTEGER, y OPERATOR (INTEGER, INTEGER) RETURNS INTEGER} KEY {x};" +
			  "INSERT myvar RELATION {" +
		      "  TUPLE {x 1, y OPERATOR (a INTEGER, b INTEGER) RETURNS INTEGER; RETURN a + b; END OPERATOR}," +
		      "  TUPLE {x 2, y OPERATOR (a INTEGER, b INTEGER) RETURNS INTEGER; RETURN a - b; END OPERATOR}," +
		      "  TUPLE {x 3, y OPERATOR (a INTEGER, b INTEGER) RETURNS INTEGER; RETURN a * b; END OPERATOR}" +
		      "};" +
		    "END;" +
		    "true";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());
	}
	
	@Test
	public void testLambda3() {
		String src = "EXTEND myvar : {p := (y) (x, 3)}";
		testEquals(
			"RELATION {x INTEGER, y OPERATOR (INTEGER, INTEGER) RETURNS INTEGER, p INTEGER} {\n" +
			"\tTUPLE {x 1, y \"OPERATOR ( a INTEGER , b INTEGER ) RETURNS INTEGER ; RETURN a + b ; END OPERATOR\", p 4},\n" +
			"\tTUPLE {x 2, y \"OPERATOR ( a INTEGER , b INTEGER ) RETURNS INTEGER ; RETURN a - b ; END OPERATOR\", p -1},\n" +
			"\tTUPLE {x 3, y \"OPERATOR ( a INTEGER , b INTEGER ) RETURNS INTEGER ; RETURN a * b ; END OPERATOR\", p 9}\n" +
			"}"
		, src);
	}
	
	@AfterClass
	public static void testLambda3_after() {
		String src =
			"BEGIN;" +
			"  DROP VAR myvar;" +
		    "END;" +
		    "true";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());
	}

}
