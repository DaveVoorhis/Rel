package ca.mb.armchair.rel3.tests.main;

import org.junit.After;
import org.junit.Test;

import ca.mb.armchair.rel3.tests.BaseOfTest;
import ca.mb.armchair.rel3.values.*;

public class TestComplexExpression1 extends BaseOfTest {
	
	@Test
	public void testComplexExpression1() {
		String src =
			"begin;" +
			" operator go() returns RELATION {x INTEGER, y CHARACTER, z RATIONAL, a INTEGER, b RATIONAL, c TUPLE {x INTEGER, y RATIONAL}};" +
			" begin;" +
			"  var myvar private relation {x INTEGER, y CHARACTER, z RATIONAL} key{x};" +
			"  myvar := RELATION {x INTEGER, y CHARACTER, z RATIONAL} {" +
			"	  TUPLE {x 1, y \"zot\", z 3.4}," +
			"	  TUPLE {x 2, y \"zap\", z 3.5}," +
			"	  TUPLE {x 3, y \"zot\", z 3.4}" +
			"  };" +
			"  operator blah(q integer, r rational, p tuple {x integer, y rational}) " +
			"              returns relation {x integer, y character, z rational, a integer, b rational, " +
			"                                     c tuple {x integer, y rational}};" +
		    "     return extend myvar : {a := q, b := r, c := p};" +
		    "  end operator;" +
		    "  return blah(5, 3.2, tuple {x 1, y 2.3});" +
		    " end;" +
		    " end operator;" +
		    "end;" +
		    "go()";
		String expected =
			"RELATION {x INTEGER, y CHARACTER, z RATIONAL, a INTEGER, b RATIONAL, c TUPLE {x INTEGER, y RATIONAL}} {" +
			"\n\tTUPLE {x 1, y \"zot\", z 3.4, a 5, b 3.2, c TUPLE {x 1, y 2.3}}," +
			"\n\tTUPLE {x 2, y \"zap\", z 3.5, a 5, b 3.2, c TUPLE {x 1, y 2.3}}," +
			"\n\tTUPLE {x 3, y \"zot\", z 3.4, a 5, b 3.2, c TUPLE {x 1, y 2.3}}" +
			"\n}";			
		testEquals(expected, src);
	}
	
	@After
	public void testComplexExpression1_cleanup() {
		String src =
			"BEGIN;" +
			"  DROP OPERATOR go();" +
		    "END;" +
		    "true";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());
	}	

}
