package org.reldb.rel.tests.main;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reldb.rel.tests.BaseOfTest;
import org.reldb.rel.values.*;

public class TestLambda2 extends BaseOfTest {

	@BeforeClass
	public static void testLambda2_before() {
		String src =
			"BEGIN;" +
		      "VAR myvar REAL RELATION {y OPERATOR (INTEGER, INTEGER) RETURNS INTEGER} KEY {y};" +
			  "INSERT myvar RELATION {" +
		      "  TUPLE {y OPERATOR (a INTEGER, b INTEGER) RETURNS INTEGER; RETURN a + b; END OPERATOR}," +
		      "  TUPLE {y OPERATOR (a INTEGER, b INTEGER) RETURNS INTEGER; RETURN a - b; END OPERATOR}," +
		      "  TUPLE {y OPERATOR (a INTEGER, b INTEGER) RETURNS INTEGER; RETURN a * b; END OPERATOR}" +
		      "};" +
		    "END;" +
		    "true";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());
	}
	
	@Test
	public void testLambda2() {
		String src = "EXTEND myvar : {p := (y) (2, 3)}";
		testEquals(
			"RELATION {y OPERATOR (INTEGER, INTEGER) RETURNS INTEGER, p INTEGER} {\n" +
			"\tTUPLE {y \"OPERATOR ( a INTEGER , b INTEGER ) RETURNS INTEGER ; RETURN a * b ; END OPERATOR\", p 6},\n" +
			"\tTUPLE {y \"OPERATOR ( a INTEGER , b INTEGER ) RETURNS INTEGER ; RETURN a + b ; END OPERATOR\", p 5},\n" +
			"\tTUPLE {y \"OPERATOR ( a INTEGER , b INTEGER ) RETURNS INTEGER ; RETURN a - b ; END OPERATOR\", p -1}\n" +
			"}"
		, src);
	}
	
	@AfterClass
	public static void testLambda2_after() {
		String src =
			"BEGIN;" +
			"  DROP VAR myvar;" +
		    "END;" +
		    "true";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());
	}

}
