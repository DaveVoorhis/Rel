package org.reldb.rel.tests.main;

import org.junit.After;
import org.junit.Test;
import org.reldb.rel.tests.BaseOfTest;
import org.reldb.rel.v0.values.*;

public class TestRecursion extends BaseOfTest {
	
	@Test
	public void testRecursion() {
		String src =
			"BEGIN;\n" +
			  "OPERATOR go() RETURNS INTEGER;\n" +
			  "BEGIN;\n" +
				"var a integer;\n" +
				"OPERATOR recursive(p integer);\n" +
				"  BEGIN;\n" +
				"    a := a + 1;\n" +
				"    if p = 0 then\n" +
				"        return;\n" +
				"    else\n" +
				"        CALL recursive(p - 1);\n" +
				"    end if;\n" +
				"  END;\n" +
				"END OPERATOR;\n" +
				"CALL recursive(10);\n" +
				"RETURN a;\n" +
		      "END;\n" +
		      "END OPERATOR;\n" +
			"END;\n" +
			"go()\n";
		assertValueEquals(ValueInteger.select(generator, 11), testEvaluate(src).getValue());
	}

	@After
	public void testRecursion_cleanup() {
		String src =
			"BEGIN;" +
			"  DROP OPERATOR go();" +
		    "END;" +
		    "true";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());
	}

}
