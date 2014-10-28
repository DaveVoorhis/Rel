package ca.mb.armchair.rel3.tests.main;

import org.junit.After;
import org.junit.Test;

import ca.mb.armchair.rel3.tests.BaseOfTest;
import ca.mb.armchair.rel3.values.*;

public class TestComplexExpression4 extends BaseOfTest {
	
	@Test
	public void testComplexExpression4() {
		String src =
			"begin;" +
			" operator go() returns RELATION {x INTEGER, c TUPLE {x INTEGER, y RATIONAL}};" +
			" begin;" +
			"  var myvar private relation {x INTEGER} key{x};" +
			"  myvar := RELATION {x INTEGER} {" +
			"	  TUPLE {x 1}," +
			"	  TUPLE {x 2}," +
			"	  TUPLE {x 3}" +
			"  };" +
			"  operator blah(p tuple {x integer, y rational}) " +
			"              returns relation {x integer, c tuple {x integer, y rational}};" +
		    "     return extend myvar : {c := p};" +
		    "  end operator;" +
		    "  return blah(tuple {x 1, y 2.3});" +
		    " end;" +
		    " end operator;" +
		    "end;" +
		    "go()";
		String expected =
			"RELATION {x INTEGER, c TUPLE {x INTEGER, y RATIONAL}} {" +
			"\n\tTUPLE {x 1, c TUPLE {x 1, y 2.3}}," +
			"\n\tTUPLE {x 2, c TUPLE {x 1, y 2.3}}," +
			"\n\tTUPLE {x 3, c TUPLE {x 1, y 2.3}}" +
			"\n}";
		testEquals(expected, src);
	}
	
	@After
	public void testComplexExpression4_cleanup() {
		String src =
			"BEGIN;" +
			"  DROP OPERATOR go();" +
		    "END;" +
		    "true";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());
	}	

}
