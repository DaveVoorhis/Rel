package org.reldb.rel.tests.main;

import org.junit.After;
import org.junit.Test;
import org.reldb.rel.tests.helpers.BaseOfTest;

public class TestRelvarInsert1 extends BaseOfTest {
	
	@Test
	public void testRelvarInsert1() {
		String src =
			"begin;" +
			"  var myvar1 real relation {y rational, x integer} key {x};" +
			"  var myvar2 real relation {x integer, y rational} key {x};" +
			"  myvar2 := relation {tuple {x 1, y 2.3}, tuple {y 3.2, x 2}};" +
			"  insert myvar1 update myvar2 : {x := x * 2, y := y * 10.0};" +
		    "end;" +
		    "myvar1";
		String expected = "RELATION {y RATIONAL, x INTEGER} {" +
			"\n\tTUPLE {y 23.0, x 2}," +
			"\n\tTUPLE {y 32.0, x 4}" +
			"\n}";
		testEquals(expected, src);				
	}
	
	@After
	public void testRelvarInsert2() {
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
