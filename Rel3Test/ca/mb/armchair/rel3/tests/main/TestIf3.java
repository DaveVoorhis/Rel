package ca.mb.armchair.rel3.tests.main;

import org.junit.After;
import org.junit.Test;

import ca.mb.armchair.rel3.tests.BaseOfTest;
import ca.mb.armchair.rel3.values.*;

public class TestIf3 extends BaseOfTest {

	@Test
	public void testIf3() {
		String src =
			"BEGIN;" +
		      "OPERATOR caseTest(p integer) RETURNS integer;" +
		        "IF p = 1 THEN " +
				   "RETURN 1;" +
		        "END IF;" +
		      "END OPERATOR;" +
		    "END;" +
		    "caseTest(2)";
		assertValueEquals(ValueInteger.select(generator, 0), testEvaluate(src).getValue());
	}
	
	@After
	public void testIf3_cleanup() {
		String src =
			"BEGIN;" +
			"  DROP OPERATOR caseTest(integer);" +
		    "END;" +
		    "true";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());
	}

}
