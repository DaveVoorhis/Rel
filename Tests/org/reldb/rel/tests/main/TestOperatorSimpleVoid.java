package org.reldb.rel.tests.main;

import org.junit.After;
import org.junit.Test;
import org.reldb.rel.tests.BaseOfTest;
import org.reldb.rel.v0.values.*;

public class TestOperatorSimpleVoid extends BaseOfTest {
	
	@Test
	public void testOperatorSimpleVoid() {
		String src =
			"BEGIN;" +
			  "OPERATOR go() RETURNS INTEGER;" +
			  "BEGIN;" +
				"var a integer;" +
				"OPERATOR blah();" +
				"   a := 3;" +
				"END OPERATOR;" +
				"CALL blah();" +
				"RETURN a;" +
			  "END;" +
			  "END OPERATOR;" +
			"END;" +
			"go()";
		assertValueEquals(ValueInteger.select(generator, 3), testEvaluate(src).getValue());		
	}

	@After
	public void testOperatorSimpleVoid_cleanup() {
		String src =
			"BEGIN;" +
			"  DROP OPERATOR go();" +
		    "END;" +
		    "true";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());
	}

}
