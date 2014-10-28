package ca.mb.armchair.rel3.tests.main;

import org.junit.After;
import org.junit.Test;

import ca.mb.armchair.rel3.tests.BaseOfTest;
import ca.mb.armchair.rel3.values.*;

public class TestOperatorSimpleParms extends BaseOfTest {
	
	@Test
	public void testOperatorSimpleParms() {
		String src =
			"BEGIN;" +
				"OPERATOR blah(a integer, b integer) RETURNS integer;" +
				"   RETURN 3 * a + b;" +
				"END OPERATOR;" +
			"END;" +
			"blah(4, 5) + blah(3, 2)";
		assertValueEquals(ValueInteger.select(generator, 17 + 11), testEvaluate(src).getValue());		
	}
	
	@After
	public void testOperatorSimpleParms_cleanup() {
		String src =
			"BEGIN;" +
			"  DROP OPERATOR blah(integer, integer);" +
		    "END;" +
		    "true";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());
	}

}
