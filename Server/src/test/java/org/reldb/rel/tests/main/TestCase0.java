package org.reldb.rel.tests.main;

import org.junit.After;
import org.junit.Test;
import org.reldb.rel.tests.helpers.BaseOfTest;
import org.reldb.rel.v0.values.ValueBoolean;
import org.reldb.rel.v0.values.ValueInteger;

public class TestCase0 extends BaseOfTest {
	
	@Test
	public void testCase0() {
		String src =
			"BEGIN;" +
				"OPERATOR caseTest(p integer) RETURNS integer;" +
				"    CASE;" +
				"      WHEN p = 1 THEN RETURN 1;" +
				"    END CASE;" +
				"END OPERATOR;" +
			"END;" +
			"caseTest(1)";
		assertValueEquals(ValueInteger.select(generator, 1), testEvaluate(src).getValue());	
	}
	
	@After
	public void testCase0_cleanup() {
		String src =
			"BEGIN;" +
			"  DROP OPERATOR caseTest(integer);" +
		    "END;" +
		    "true";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());
	}	

}
