package org.reldb.rel.tests.main;

import org.junit.After;
import org.junit.Test;
import org.reldb.rel.tests.BaseOfTest;
import org.reldb.rel.v0.values.ValueBoolean;

public class TestComplexExpression8 extends BaseOfTest {
	
	@Test
	public void testComplexExpression8() {
		String src =
			"begin;" +
			"  operator blah(p integer, q tuple {x integer, y rational}, r rational, s char) returns rational;" +
			"    begin;" +
		    "      var d integer;" +
		    "      var e tuple {x integer, y rational};" +
		    "      var f rational;" +
		    "      var g char;" +
		    "      d := p;" +
		    "      e := q;" +
		    "      f := r;" +
		    "      g := s;" +
		    "      return f;" +
			"    end;" +
		    "  end operator;" +
		    "end;" +
		    "blah(3, tuple {x 1, y 1.2}, 3.5, 'fish')";
		String expected = "3.5";			
		testEquals(expected, src);
	}
	
	@After
	public void testComplexExpression8_cleanup() {
		String src =
			"BEGIN;" +
			"  DROP OPERATOR blah(integer, tuple {x integer, y rational}, rational, char);" +
		    "END;" +
		    "true";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());
	}	

}
