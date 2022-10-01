package org.reldb.rel.tests.main;

import org.junit.After;
import org.junit.Test;
import org.reldb.rel.tests.helpers.BaseOfTest;
import org.reldb.rel.v0.values.ValueBoolean;

public class TestComplexExpression7 extends BaseOfTest {
	
	@Test
	public void testComplexExpression7() {
		String src =
			"begin;" +
			"  operator blah(p integer, q tuple {x integer, y rational}, r rational, s char) " +
			"              returns tuple {a tuple {x integer, y rational}, b rational, c integer, d char};" +
		    "     return tuple {a q, b r, c p, d s};" +
		    "  end operator;" +
		    "end;" +
		    "blah(5, tuple {x 1, y 2.3}, 3.8, 'fish')";	
		String expected =
			"TUPLE {a TUPLE {x 1, y 2.3}, b 3.8, c 5, d \"fish\"}";			
		testEquals(expected, src);
	}
	
	@After
	public void testComplexExpression7_cleanup() {
		String src =
			"BEGIN;" +
			"  DROP OPERATOR blah(integer, tuple {x integer, y rational}, rational, char);" +
		    "END;" +
		    "true";
		assertValueEquals(ValueBoolean.select(generator, true), testEvaluate(src).getValue());
	}	

}
