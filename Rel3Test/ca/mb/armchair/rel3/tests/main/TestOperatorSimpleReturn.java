package ca.mb.armchair.rel3.tests.main;

import org.junit.After;
import org.junit.Test;

import ca.mb.armchair.rel3.tests.BaseOfTest;
import ca.mb.armchair.rel3.values.*;

public class TestOperatorSimpleReturn extends BaseOfTest {
	
	@Test
	public void testOperatorSimpleReturn() {
		String src =
			"BEGIN;" +
				"OPERATOR blah() RETURNS integer;" +
				"   RETURN 3;" +
				"END OPERATOR;" +
			"END;" +
			"blah()";
		assertValueEquals(ValueInteger.select(generator, 3), testEvaluate(src).getValue());
	}
	
	@After
	public void testOperatorSimpleReturn_cleanup() {
		String src =
			"BEGIN;" +
			"  DROP OPERATOR blah();" +
		    "END;" +
		    "true";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());
	}

}
