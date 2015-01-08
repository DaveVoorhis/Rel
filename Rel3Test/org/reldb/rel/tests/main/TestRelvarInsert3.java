package org.reldb.rel.tests.main;

import org.junit.After;
import org.junit.Test;
import org.reldb.rel.tests.BaseOfTest;

public class TestRelvarInsert3 extends BaseOfTest {
	
	@Test
	public void testRelvarInsert3() {
		String src =
			"begin;" +
			"  var myvar1 real relation {x integer, y rational} key {x};" +
			"  var myvar2 real relation {y rational, x integer} key {x};" +
			"  myvar2 := relation {tuple {x 1, y 2.3}, tuple {y 3.2, x 2}};" +
			"  insert myvar1 (myvar2 where x >= 2 and y > 2.0);" +
		    "end;" +
		    "myvar1";
		String expected = "RELATION {x INTEGER, y RATIONAL} {" +
			"\n\tTUPLE {x 2, y 3.2}" +
			"\n}";
		testEquals(expected, src);				
	}
	
	@After
	public void testRelvarInsert4() {
		String src =
			"begin;" +
			"  drop var myvar1;" +
			"  drop var myvar2;" +
		    "end;" +
		    "true";
		String expected = "true";
		testEquals(expected, src);		
	}

}
