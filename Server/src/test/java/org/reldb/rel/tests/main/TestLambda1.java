package org.reldb.rel.tests.main;

import org.junit.Test;
import org.reldb.rel.tests.BaseOfTest;

public class TestLambda1 extends BaseOfTest {
	
	@Test
	public void testLambda0() {
		String src = "(OPERATOR (x INTEGER, y INTEGER) RETURNS INTEGER;" + 
				     "   RETURN x + y;" +
				     " END OPERATOR) (2, 4)";
		testEquals("6", src);
	}

	@Test
	public void testLambda1() {
		String src = 
			"BEGIN;" +
			"  VAR myvar OPERATOR (INTEGER, INTEGER) RETURNS INTEGER;" +
			"  myvar := OPERATOR (x INTEGER, y INTEGER) RETURNS INTEGER; RETURN x + y; END OPERATOR;" +
			"END;" +
			"(myvar) (2, 4)";
		testEquals("6", src);
	}

	@Test
	public void testLambda2() {
		String src = 
			"(operator (x integer, y integer) returns integer;" +
			"   return (operator (z integer) returns integer; return x + z; end operator) (y);" +
			"end operator) (2, 4)";
		testEquals("6", src);
	}
	
	@Test
	public void testLambda4() {
		String src =
			"((OPERATOR (x INTEGER) RETURNS OPERATOR (INTEGER) RETURNS INTEGER;" +
			"      RETURN ~[ (a INTEGER) RETURNS INTEGER; RETURN x + a; ]~;" +
			"  END OPERATOR)" +
			"(2)) (4)";
		testEquals("6", src);
	}
	
}
