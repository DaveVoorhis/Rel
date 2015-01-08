package org.reldb.rel.tests.main;

import org.junit.After;
import org.junit.Test;
import org.reldb.rel.tests.BaseOfTest;

public class TestVirtualRelvar1 extends BaseOfTest {
	
	@Test
	public void testVirtualRelvar1() {
		String src =
			"begin;\n" +
			"  var r1 real relation {x integer, y rational} key {x};\n" +
			"  var r2 real relation {x integer, z rational} key {x};\n" +
			"  r1 := relation {tuple {x 1, y 2.3}, tuple {x 2, y 3.4}};\n" +
			"  r2 := relation {tuple {x 1, z 3.3}, tuple {x 2, z 5.7}};\n" +
			"  var r3 virtual r1 join r2;\n" +
		    "end;\n" +
		    "r3";
		String expected = "RELATION {x INTEGER, y RATIONAL, z RATIONAL} {" +
			"\n\tTUPLE {x 1, y 2.3, z 3.3}," +
			"\n\tTUPLE {x 2, y 3.4, z 5.7}" +
			"\n}";
		testEquals(expected, src);		
	}
	
	@After
	public void testVirtualRelvar2() {
		String src =
			"begin;" +
			"  drop var r3;" +
			"  drop var r1;" +
			"  drop var r2;" +
		    "end;" +
		    "true";
		String expected = "true";
		testEquals(expected, src);		
	}

}
