package org.reldb.rel.tests.main;

import org.junit.After;
import org.junit.Test;
import org.reldb.rel.tests.helpers.BaseOfTest;
import org.reldb.rel.v0.values.ValueBoolean;

public class TestComplexExpression5 extends BaseOfTest {
	
	@Test
	public void testComplexExpression5() {
		String src =
			"begin;" +
			" operator go() returns tuple {x integer, a integer, c tuple {x integer, y rational}};" +
			" begin;" +
			"  var myvar init(TUPLE {x 1});" +
			"  operator blah(q integer, p tuple {x integer, y rational}) " +
			"              returns tuple {x integer, a integer, c tuple {x integer, y rational}};" +
		    "     return extend myvar : {a := q, c := p};" +
		    "  end operator;" +
		    "  return blah(5, tuple {x 1, y 2.3});" +	
		    " end;" +
		    " end operator;" +
		    "end;" +
		    "go()";
		String expected =
			"TUPLE {x 1, a 5, c TUPLE {x 1, y 2.3}}";			
		testEquals(expected, src);
	}
	
	@After
	public void testComplexExpression5_cleanup() {
		String src =
			"BEGIN;" +
			"  DROP OPERATOR go();" +
		    "END;" +
		    "true";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());
	}	

}
