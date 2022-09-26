package org.reldb.rel.tests.main;

import org.junit.After;
import org.junit.Test;
import org.reldb.rel.tests.BaseOfTest;
import org.reldb.rel.v0.values.ValueBoolean;

public class TestComplexExpression6 extends BaseOfTest {
	
	@Test
	public void testComplexExpression6() {
		String src =
			"begin;" +
			" operator go() returns tuple {x integer, a integer, b tuple {x integer, y rational}, c rational};" +
			" begin;" +
			"  var myvar init(TUPLE {x 1});" +
			"  operator blah(p integer, q tuple {x integer, y rational}, r rational) " +
			"              returns tuple {x integer, a integer, b tuple {x integer, y rational}, c rational};" +
		    "     return extend myvar : {a := p, b := q, c := r};" +
		    "  end operator;" +
		    "  return blah(5, tuple {x 1, y 2.3}, 3.8);" +
		    " end;" +
		    " end operator;" +
		    "end;" +
		    "go()";
		String expected =
			"TUPLE {x 1, a 5, b TUPLE {x 1, y 2.3}, c 3.8}";			
		testEquals(expected, src);
	}
	
	@After
	public void testComplexExpression6_cleanup() {
		String src =
			"BEGIN;" +
			"  DROP OPERATOR go();" +
		    "END;" +
		    "true";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());
	}	

}
