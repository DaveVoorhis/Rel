package org.reldb.rel.tests.main;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reldb.rel.tests.BaseOfTest;

public class TestRelvarAlter01 extends BaseOfTest {

	@BeforeClass
	public static void testBefore() {
		String src = 
			"BEGIN;\n" +
			" VAR myvar REAL RELATION { x INT, y CHAR } KEY {x};" +
			" INSERT myvar RELATION {TUPLE {x 1, y 'a'}, TUPLE {x 2, y 'b'}};" +
			"END;\n" +
			"true";
		testEquals("true", src);
		src = 
			"BEGIN;\n" +
			" ALTER VAR myvar RENAME x TO xx RENAME y TO yy;" +
			"END;\n" +
			"true";
		testEquals("true", src);
	}

	@Test
	public void test01() {
		String src = 
			"myvar";
		String expected = 
			"RELATION {xx INTEGER, yy CHARACTER} {\n" +
			"\tTUPLE {xx 1, yy \"a\"},\n" +
			"\tTUPLE {xx 2, yy \"b\"}\n" +
			"}";
		testEquals(expected, src);
	}
	
	@AfterClass
	public static void testAfter() {
		String src = 
			"BEGIN;\n" +
				"DROP VAR myvar;" +
			"END;\n" +
			"true";
		String expected = "true";
		testEquals(expected, src);
	}

}
