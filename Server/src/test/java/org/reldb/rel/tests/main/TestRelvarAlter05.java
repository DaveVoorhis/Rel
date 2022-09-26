package org.reldb.rel.tests.main;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reldb.rel.tests.BaseOfTest;

public class TestRelvarAlter05 extends BaseOfTest {

	@BeforeClass
	public static void testBefore() {
		String src = 
			"BEGIN;\n" +
			" VAR myvar REAL RELATION { x INT, y CHAR, z RATIONAL } KEY {x};" +
			" INSERT myvar RELATION {TUPLE {x 1, y 'a', z 0.2}, TUPLE {x 2, y 'b', z 0.4}};" +
			"END;\n" +
			"true";
		testEquals("true", src);
		src = 
			"BEGIN;\n" +
			" ALTER VAR myvar KEY {y, z};" +
			"END;\n" +
			"true";
		testEquals("true", src);
	}

	@Test
	public void test01() {
		String src = 
			"myvar";
		String expected = 
			"RELATION {x INTEGER, y CHARACTER, z RATIONAL} {\n" +
			"\tTUPLE {x 1, y \"a\", z 0.2},\n" +
			"\tTUPLE {x 2, y \"b\", z 0.4}\n" +
			"}";
		testEquals(expected, src);
	}
	
	@Test
	public void test02() {
		String src = 
				"BEGIN;\n" +
				"   INSERT myvar REL {TUP {x 3, y 'a', z 0.2}};" +
				"END;\n" +
				"true";
		testEquals("true", src);
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
