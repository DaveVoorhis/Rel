package org.reldb.rel.tests.main;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reldb.rel.tests.BaseOfTest;

public class TestAggregate extends BaseOfTest {
	
	@BeforeClass
	public static void testAggregateSetup() {
		String src =
			"begin;" +
			"  VAR myvar REAL RELATION {x INT} KEY {x};" +
			"  myvar := RELATION {" +
			"	TUPLE {x 1}," +
			"	TUPLE {x 2}," +
			"	TUPLE {x 3}," +
			"	TUPLE {x 4}," +
			"	TUPLE {x 5}," +
			"	TUPLE {x 6}" +
			"  };" +
			"end;" +
			"true";
		String expected = "true";
		testEquals(expected, src);								
	}
	
	@Test
	public void testAggregate00() {
		String src =
			"SUM(myvar, x)";
		String expected = "21";
		testEquals(expected, src);						
	}
	
	@Test
	public void testAggregate01() {
		String src =
			"SUM(myvar, x) = AGGREGATE(myvar, x); RETURN VALUE1 + VALUE2; END AGGREGATE";
		String expected = "true";
		testEquals(expected, src);						
	}
	
	@Test
	public void testAggregate02() {
		String src =
			"SUM(myvar, x * 10) = AGGREGATE(myvar, x * 10); RETURN VALUE1 + VALUE2; END AGGREGATE";
		String expected = "true";
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
