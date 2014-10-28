package ca.mb.armchair.rel3.tests.main;

import org.junit.After;
import org.junit.Test;

import ca.mb.armchair.rel3.tests.BaseOfTest;
import ca.mb.armchair.rel3.values.*;

public class TestNestedOp2 extends BaseOfTest {
	
	@Test
	public void testNestedOp2() {
		String src =
			"BEGIN;" +
			  "OPERATOR go() RETURNS INTEGER;" +
			  "BEGIN;" +
				"VAR a INIT(5);" +
				"var b INIT(7);" +
				"var c integer INIT(700);" +
				"OPERATOR blah(p1 integer, p2 integer) RETURNS integer;" +
				"  BEGIN;" +
				"    OPERATOR bleat(p3 integer, p4 integer) RETURNS integer;" +
				"       RETURN 10 + p3 + p4 + p1 + p2;" +
				"    END OPERATOR;" +
				"    OPERATOR blat(p5 integer, p6 integer) RETURNS integer;" +
				"       RETURN 12 + p5 + p6 + p1 + p2;" +
				"    END OPERATOR;" +
				"    RETURN bleat(10, 20) + blat(100, 200) + c;" +
				"  END;" +
				"END OPERATOR;" +
				"RETURN blah(1, 2);" +
			  "END;" +
			  "END OPERATOR;" +
			"END;" +
			"go()";
		assertValueEquals(ValueInteger.select(generator, 1058), testEvaluate(src).getValue());						
	}
	
	@After
	public void testNestedOp2_cleanup() {
		String src =
			"BEGIN;" +
			"  DROP OPERATOR go();" +
		    "END;" +
		    "true";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());
	}

}
