package org.reldb.rel.tests.main;

import org.junit.After;
import org.junit.Test;
import org.reldb.rel.tests.helpers.BaseOfTest;
import org.reldb.rel.v0.values.ValueBoolean;
import org.reldb.rel.v0.values.ValueInteger;

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
