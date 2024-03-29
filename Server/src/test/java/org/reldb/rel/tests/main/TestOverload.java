package org.reldb.rel.tests.main;

import org.junit.After;
import org.junit.Test;
import org.reldb.rel.tests.helpers.BaseOfTest;
import org.reldb.rel.v0.values.ValueBoolean;
import org.reldb.rel.v0.values.ValueInteger;

public class TestOverload extends BaseOfTest {

	@Test
	public void testOverload() {
		String src =
			"BEGIN;" +
				"OPERATOR overload(p integer, q char) RETURNS integer;" +
				"  RETURN 10;" +
				"END OPERATOR;" +
				"OPERATOR overload(p rational, q integer) RETURNS integer;" +
				"  RETURN 5;" +
				"END OPERATOR;" +
				"OPERATOR overload(p boolean, q rational) RETURNS integer;" +
				"  RETURN 2;" +
				"END OPERATOR;" +
			"END;" +
			"overload(5, 'test') - overload(1.2, 3) - overload(true, 1.8)";
		assertValueEquals(ValueInteger.select(generator, 3), testEvaluate(src).getValue());
	}

	@After
	public void testNestedOverload_cleanup() {
		String src =
			"BEGIN;" +
			"  DROP OPERATOR overload(integer, char);" +
			"  DROP OPERATOR overload(rational, integer);" +
			"  DROP OPERATOR overload(boolean, rational);" +
		    "END;" +
		    "true";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());
	}	

}
