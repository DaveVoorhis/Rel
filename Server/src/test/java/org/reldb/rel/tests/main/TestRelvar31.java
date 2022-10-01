package org.reldb.rel.tests.main;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reldb.rel.tests.helpers.BaseOfTest;

public class TestRelvar31 extends BaseOfTest {

	@BeforeClass
	public static void testRelvar31() {
		String src = 
			"BEGIN;\n" +
				"VAR testvar REAL INIT(" +
				"RELATION {" +
				"  tuple {x 1, y 2.3}," +
				"  tuple {x 2, y 2.3}," +
				"  tuple {x 3, y 2.3}," +
				"  tuple {x 4, y 2.3}," +
				"  tuple {x 5, y 2.3}" +
				"}) key {x};" +
			"END;\n" +
			"true";
		testEquals("true", src);
	}

	@Test
	public void testRelvar32() {
		String src = 
			"testvar";
		String expected = "RELATION {x INTEGER, y RATIONAL} {" +
			"\n\tTUPLE {x 1, y 2.3}," +
			"\n\tTUPLE {x 2, y 2.3}," +
			"\n\tTUPLE {x 3, y 2.3}," +
			"\n\tTUPLE {x 4, y 2.3}," +
			"\n\tTUPLE {x 5, y 2.3}" +
			"\n}";
		testEquals(expected, src);
	}
		
	@AfterClass
	public static void testRelvar33() {
		String src = 
			"BEGIN;\n" +
				"DROP VAR testvar;" +
			"END;\n" +
			"true";
		String expected = "true";
		testEquals(expected, src);
	}

}
