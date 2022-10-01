package org.reldb.rel.tests.main;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reldb.rel.tests.helpers.BaseOfTest;

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
	
	@Test
	public void testAggregate03() {
		String src =
			"COUNT(myvar) = 6";
		String expected = "true";
		testEquals(expected, src);						
	}
	
	@Test
	public void testAggregate04() {
		String src =
			"COUNT(REL {x INT} {}) = 0";
		String expected = "true";
		testEquals(expected, src);						
	}
	
	@Test
	public void testAggregate05() {
		String src =
			"COUNT(REL {x INT} {}) = AGGREGATE(REL {x INT} {}, 1, 0); RETURN VALUE1 + VALUE2; END AGGREGATE";
		String expected = "true";
		testEquals(expected, src);						
	}
		
	@Test
	public void testAggregate06() {
		String src =
			"COUNT(myvar) = AGGREGATE(myvar, 1, 0); RETURN VALUE1 + VALUE2; END AGGREGATE";
		String expected = "true";
		testEquals(expected, src);						
	}
	
	@Test
	public void testAggregate07() {
		String src =
			"arithmeticMean FROM TUPLE FROM EXTEND " + 
			"	SUMMARIZE myvar:  " + 
			"	{ " + 
			"		total := AGGREGATE(x); RETURN VALUE1 + VALUE2; END AGGREGATE, " + 
			"		N := AGGREGATE(1); RETURN VALUE1 + VALUE2; END AGGREGATE " + 
			"	} " + 
			": {arithmeticMean := CAST_AS_RATIONAL(total) / CAST_AS_RATIONAL(N)} ";
		String expected = "3.5";
		testEquals(expected, src);						
	}
	
	@Test
	public void testAggregate08() {
		String src =
			"arithmeticMean FROM TUPLE FROM EXTEND " + 
			"	SUMMARIZE myvar:  " + 
			"	{ " + 
			"		total := AGGREGATE(x, 0); RETURN VALUE1 + VALUE2; END AGGREGATE, " + 
			"		N := AGGREGATE(1, 0); RETURN VALUE1 + VALUE2; END AGGREGATE " + 
			"	} " + 
			": {arithmeticMean := CAST_AS_RATIONAL(total) / CAST_AS_RATIONAL(N)} ";
		String expected = "3.5";
		testEquals(expected, src);						
	}
	
	@Test
	public void testAggregateDistinctAgainstSumDistinct() {
		String src =
			"BEGIN;\n" +
		        "VAR a PRIVATE INIT(relation {\n" +
		        "       tuple {x 1, y 4.5, z \"test\"},\n" +
		        "       tuple {x 2, y 2.5, z \"test\"},\n" +
		        "       tuple {x 3, y 3.2, z \"glub\"},\n" +
		        "       tuple {x 4, y 4.5, z \"glub\"},\n" +
		        "       tuple {x 5, y 5.2, z \"test\"},\n" +
		        "       tuple {x 6, y 3.1, z \"zot\"},\n" +
		        "       tuple {x 7, y 4.5, z \"test\"}\n" +
		        "}) KEY {ALL BUT};\n" +
			"END;\n" +
			"SUMMARIZE a PER (a {z}) : {totalOfxSUMD := SUMD(x), totalOfxAGGREGATED := AGGREGATED(x); RETURN VALUE1 + VALUE2; END AGGREGATED}";
		testEquals("RELATION {z CHARACTER, totalOfxSUMD INTEGER, totalOfxAGGREGATED INTEGER} {\n\tTUPLE {z \"test\", totalOfxSUMD 15, totalOfxAGGREGATED 15},\n\tTUPLE {z \"glub\", totalOfxSUMD 7, totalOfxAGGREGATED 7},\n\tTUPLE {z \"zot\", totalOfxSUMD 6, totalOfxAGGREGATED 6}\n}", src);		
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
