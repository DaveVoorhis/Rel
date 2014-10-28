package ca.mb.armchair.rel3.tests.main;

import org.junit.After;
import org.junit.Test;

import ca.mb.armchair.rel3.tests.BaseOfTest;
import ca.mb.armchair.rel3.values.*;

public class TestCase2 extends BaseOfTest {
		
	@Test
	public void testCase2() {
		String src =
			"BEGIN;" +
				"OPERATOR caseTest(p integer) RETURNS integer;" +
				"    CASE;" +
				"      WHEN p = 1 THEN RETURN 1;" +
				"      WHEN p = 2 THEN RETURN 2;" +
				"      WHEN p = 3 THEN RETURN 3;" +
				"      ELSE RETURN 4;" +
				"    END CASE;" +
				"END OPERATOR;" +
			"END;" +
			"caseTest(1) + caseTest(2) + caseTest(3) + caseTest(4)";
		assertValueEquals(ValueInteger.select(generator, 10), testEvaluate(src).getValue());
	}
	
	@After
	public void testCase2_cleanup() {
		String src =
			"BEGIN;" +
			"  DROP OPERATOR caseTest(integer);" +
		    "END;" +
		    "true";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());
	}	

}
