package org.reldb.rel.tests.main;

import org.junit.After;
import org.junit.Test;
import org.reldb.rel.tests.BaseOfTest;

public class TestRelvarInsert5 extends BaseOfTest {
	
	@Test
	public void testRelvarInsert1() {
		String src =
			"begin;" +
			"  var myvar1 real relation {x integer, y integer} key {x} key {y};" +
			"  myvar1 := relation {tuple {x 1, y 1}};" +
			"  insert myvar1 relation {tuple {x 2, y 1}};" +
		    "end;" +
		    "myvar1";
		String expected = "RELATION {x INTEGER, y INTEGER} {" +
			"\n\tTUPLE {x 1, y 1}" +
			"\n}";
		testEquals(expected, src);				
	}
	
	@After
	public void testRelvarInsert2() {
		String src =
			"begin;" +
			"  drop var myvar1;" +
		    "end;" +
		    "true";
		String expected = "true";
		testEquals(expected, src);		
	}

}
