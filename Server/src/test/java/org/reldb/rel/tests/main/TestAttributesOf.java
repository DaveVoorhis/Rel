package org.reldb.rel.tests.main;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reldb.rel.tests.BaseOfTest;

public class TestAttributesOf extends BaseOfTest {
	
	@BeforeClass
	public static void testAggregateSetup() {
		String src =
			"begin;" +
			"  VAR myvar REAL RELATION {x INT, y INT} KEY {x};" +
			"  myvar := RELATION {" +
			"	TUPLE {x 1, y 2}," +
			"	TUPLE {x 2, y 2}," +
			"	TUPLE {x 3, y 3}," +
			"	TUPLE {x 4, y 3}," +
			"	TUPLE {x 5, y 4}," +
			"	TUPLE {x 6, y 5}" +
			"  };" +
			"end;" +
			"true";
		String expected = "true";
		testEquals(expected, src);								
	}
	
	@Test
	public void testAttributesOf00() {
		String src =
			"myvar {ATTRIBUTES_OF(myvar {x})}";
		String expected = 
			"RELATION {x INTEGER} {\n" +
			"\tTUPLE {x 1},\n" +
			"\tTUPLE {x 2},\n" +
			"\tTUPLE {x 3},\n" +
			"\tTUPLE {x 4},\n" +
			"\tTUPLE {x 5},\n" +
			"\tTUPLE {x 6}\n" +
			"}";
		testEquals(expected, src);						
	}
	
	@Test
	public void testAttributesOf01() {
		String src =
			"myvar {ALL BUT ATTRIBUTES_OF(myvar {x})}";
		String expected = 
			"RELATION {y INTEGER} {\n" +
			"\tTUPLE {y 2},\n" +
			"\tTUPLE {y 3},\n" +
			"\tTUPLE {y 4},\n" +
			"\tTUPLE {y 5}\n" +
			"}";
		testEquals(expected, src);						
	}
		
	@AfterClass
	public static void testSummarizeComplexTeardown() {
		String src =
			"begin;" +
			"  DROP VAR myvar;" +
			"end;" +
			"true";
		String expected = "true";
		testEquals(expected, src);						
	}

}
