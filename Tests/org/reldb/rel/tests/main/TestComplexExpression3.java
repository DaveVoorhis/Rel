package org.reldb.rel.tests.main;

import org.junit.After;
import org.junit.Test;
import org.reldb.rel.tests.BaseOfTest;
import org.reldb.rel.v0.values.*;

public class TestComplexExpression3 extends BaseOfTest {
	
	@Test
	public void testComplexExpression3() {
		String src =
			"begin;" +
			" operator go() returns RELATION {x INTEGER, a INTEGER, c TUPLE {x INTEGER, y RATIONAL}};" +
			" begin;" +
			"  var myvar private relation {x INTEGER} key{x};" +
			"  myvar := RELATION {x INTEGER} {" +
			"	  TUPLE {x 1}," +
			"	  TUPLE {x 2}," +
			"	  TUPLE {x 3}" +
			"  };" +
			"  operator blah(q integer, p tuple {x integer, y rational}) " +
			"              returns relation {x integer, a integer, c tuple {x integer, y rational}};" +
		    "     return extend myvar : {a := q, c := p};" +
		    "  end operator;" +
		    "  return blah(5, tuple {x 1, y 2.3});" +
		    " end;" +
		    " end operator;" +
		    "end;" +
		    "go()";
		String expected =
			"RELATION {x INTEGER, a INTEGER, c TUPLE {x INTEGER, y RATIONAL}} {" +
			"\n\tTUPLE {x 1, a 5, c TUPLE {x 1, y 2.3}}," +
			"\n\tTUPLE {x 2, a 5, c TUPLE {x 1, y 2.3}}," +
			"\n\tTUPLE {x 3, a 5, c TUPLE {x 1, y 2.3}}" +
			"\n}";			
		testEquals(expected, src);
	}
	
	@After
	public void testComplexExpression3_cleanup() {
		String src =
			"BEGIN;" +
			"  DROP OPERATOR go();" +
		    "END;" +
		    "true";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());
	}	

}
