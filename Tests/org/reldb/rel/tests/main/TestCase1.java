package org.reldb.rel.tests.main;

import org.junit.After;
import org.junit.Test;
import org.reldb.rel.tests.BaseOfTest;
import org.reldb.rel.values.*;

public class TestCase1 extends BaseOfTest {
		
	@Test
	public void testCase1() {
		String src =
			"BEGIN;" +
				"OPERATOR caseTest(p integer) RETURNS integer;" +
				"    CASE;" +
				"      WHEN p = 1 THEN RETURN 1;" +
				"      WHEN p = 2 THEN RETURN 2;" +
				"      WHEN p = 3 THEN RETURN 3;" +
				"    END CASE;" +
				"END OPERATOR;" +
			"END;" +
			"caseTest(1) + caseTest(2) + caseTest(3) + caseTest(4)";
		assertValueEquals(ValueInteger.select(generator, 6), testEvaluate(src).getValue());	
	}
	
	@After
	public void testCase1_cleanup() {
		String src =
			"BEGIN;" +
			"  DROP OPERATOR caseTest(integer);" +
		    "END;" +
		    "true";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());
	}	

}
