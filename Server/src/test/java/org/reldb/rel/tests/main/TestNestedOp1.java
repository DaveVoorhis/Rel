package org.reldb.rel.tests.main;

import org.junit.After;
import org.junit.Test;
import org.reldb.rel.tests.helpers.BaseOfTest;
import org.reldb.rel.v0.values.ValueBoolean;
import org.reldb.rel.v0.values.ValueInteger;

public class TestNestedOp1 extends BaseOfTest {
	
	@Test
	public void testNestedOp1() {
		String src =
			"BEGIN;" +
			  "OPERATOR go() RETURNS INTEGER;" +
			  "BEGIN;" +
				"var a integer;" +
				"var b integer;" +
				"var c integer;" +
				"OPERATOR blah();" +
				"  BEGIN;" +
				"    OPERATOR bleat();" +
				"       a := 10;" +
				"    END OPERATOR;" +
				"    OPERATOR blat();" +
				"       b := 12;" +
				"    END OPERATOR;" +
				"    CALL bleat();" +
				"    CALL blat();" +
				"    c := a + b;" +
				"  END;" +
				"END OPERATOR;" +
				"CALL blah();" +
				"RETURN c;" +
			  "END;" +
			  "END OPERATOR;" +
			"END;" +
			"go()";
		assertValueEquals(ValueInteger.select(generator, 22), testEvaluate(src).getValue());					
	}
	
	@After
	public void testNestedOp1_cleanup() {
		String src =
			"BEGIN;" +
			"  DROP OPERATOR go();" +
		    "END;" +
		    "true";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());
	}

}
