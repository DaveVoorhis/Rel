package ca.mb.armchair.rel3.tests.main;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.mb.armchair.rel3.tests.BaseOfTest;

public class TestRelvar26 extends BaseOfTest {
	
	@BeforeClass
	public static void testRelvar26() {
		String src = 
			"BEGIN;\n" +
				"VAR testvar REAL RELATION {x integer, y rational} key {x};" +
				"testvar := RELATION {" +
				"  tuple {x 1, y 2.3}," +
				"  tuple {x 2, y 2.3}," +
				"  tuple {x 3, y 2.3}," +
				"  tuple {x 4, y 2.3}," +
				"  tuple {x 5, y 2.3}" +
				"};" +
			"END;\n" +
			"true";
			testEquals("true", src);
	}

	@Test
	public void testRelvar28() {
		String src = 
				"BEGIN;\n" +
					"testvar := testvar;" +
				"END;\n" +
				"testvar";
			String expected = "RELATION {x INTEGER, y RATIONAL} {" +
				"\n\tTUPLE {x 1, y 2.3}," +
				"\n\tTUPLE {x 2, y 2.3}," +
				"\n\tTUPLE {x 3, y 2.3}," +
				"\n\tTUPLE {x 4, y 2.3}," +
				"\n\tTUPLE {x 5, y 2.3}" +
				"\n}";
			testEquals(expected, src);

		src = 
			"BEGIN;\n" +
				"INSERT testvar UPDATE testvar : {x := x + 10, y := y + 10.0};" +
			"END;\n" +
			"testvar";
		expected = "RELATION {x INTEGER, y RATIONAL} {" +
			"\n\tTUPLE {x 1, y 2.3}," +
			"\n\tTUPLE {x 2, y 2.3}," +
			"\n\tTUPLE {x 3, y 2.3}," +
			"\n\tTUPLE {x 4, y 2.3}," +
			"\n\tTUPLE {x 5, y 2.3}," +
			"\n\tTUPLE {x 11, y 12.3}," +
			"\n\tTUPLE {x 12, y 12.3}," +
			"\n\tTUPLE {x 13, y 12.3}," +
			"\n\tTUPLE {x 14, y 12.3}," +
			"\n\tTUPLE {x 15, y 12.3}" +
			"\n}";
		testEquals(expected, src);
		
		src = 
			"BEGIN;\n" +
				"UPDATE testvar : {x := x + 10, y := y + 10.0};" +
			"END;\n" +
			"testvar";
		expected = "RELATION {x INTEGER, y RATIONAL} {" +
			"\n\tTUPLE {x 11, y 12.3}," +
			"\n\tTUPLE {x 12, y 12.3}," +
			"\n\tTUPLE {x 13, y 12.3}," +
			"\n\tTUPLE {x 14, y 12.3}," +
			"\n\tTUPLE {x 15, y 12.3}," +
			"\n\tTUPLE {x 21, y 22.3}," +
			"\n\tTUPLE {x 22, y 22.3}," +
			"\n\tTUPLE {x 23, y 22.3}," +
			"\n\tTUPLE {x 24, y 22.3}," +
			"\n\tTUPLE {x 25, y 22.3}" +
			"\n}";
		testEquals(expected, src);
	}
	
	@AfterClass
	public static void testRelvar30() {
		String src = 
			"BEGIN;\n" +
				"DROP VAR testvar;" +
			"END;\n" +
			"true";
		String expected = "true";
		testEquals(expected, src);
	}

}
