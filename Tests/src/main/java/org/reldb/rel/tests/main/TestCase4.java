package org.reldb.rel.tests.main;

import org.junit.After;
import org.junit.Test;
import org.reldb.rel.tests.BaseOfTest;
import org.reldb.rel.v0.values.*;

public class TestCase4 extends BaseOfTest {
		
	@Test
	public void testCase4() {
		String src =
			"BEGIN;" +
				"OPERATOR caseTest(p integer) RETURNS integer;" +
				"    RETURN CASE " +
				"      WHEN p = 1 THEN 1" +
				"      WHEN p = 2 THEN 2" +
				"      WHEN p = 3 THEN 3" +
				"      ELSE 4" +
				"    END CASE;" +
				"END OPERATOR;" +
			"END;" +
			"caseTest(1) + caseTest(2) + caseTest(3) + caseTest(4)";
		assertValueEquals(ValueInteger.select(generator, 10), testEvaluate(src).getValue());	
	}
	
	@After
	public void testCase4_cleanup() {
		String src =
			"BEGIN;" +
			"  DROP OPERATOR caseTest(integer);" +
		    "END;" +
		    "true";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());
	}	

}
