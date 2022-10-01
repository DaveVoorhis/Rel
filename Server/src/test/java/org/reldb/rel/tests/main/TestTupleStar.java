package org.reldb.rel.tests.main;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reldb.rel.tests.helpers.BaseOfTest;

public class TestTupleStar extends BaseOfTest {
	
	@BeforeClass
	public static void testSetup() {
		String src =
				"begin;" +
				" VAR myvar REAL RELATION{x INTEGER, c CHARACTER} KEY {x};\n" +
				" myvar := RELATION {\n" +
				"	TUPLE {x 1, c 'A'},\n" +
				"	TUPLE {x 2, c 'A'},\n" +
				"	TUPLE {x 3, c 'B'},\n" +
				"	TUPLE {x 4, c 'B'},\n" +
				"	TUPLE {x 5, c 'B'},\n" +
				"	TUPLE {x 10, c 'D'},\n" +
				"	TUPLE {x 12, c 'D'}\n" +
				" };\n" +
				"end;" +
				"true";
			String expected = "true";
			testEquals(expected, src);								
	}
	
	@Test
	public void testTupleStar00() {
		String src = "SUMMARIZE myvar: {x := UNION(REL{TUP{*}} MINUS REL{TUP{*}})}";
		String expected = 
				"RELATION {x RELATION {x INTEGER, c CHARACTER}} {\n" +
				"\tTUPLE {x RELATION {x INTEGER, c CHARACTER} {\n" +
				"}}\n" +
				"}";
		testEquals(expected, src);
	}
	
	@Test
	public void testTupleStar01() {
		String src = "UNION(myvar, REL{TUP{*}})";
		String expected = 
				"RELATION {x INTEGER, c CHARACTER} {\n" +
				"\tTUPLE {x 1, c \"A\"},\n" +
				"\tTUPLE {x 2, c \"A\"},\n" +
				"\tTUPLE {x 3, c \"B\"},\n" +
				"\tTUPLE {x 4, c \"B\"},\n" +
				"\tTUPLE {x 5, c \"B\"},\n" +
				"\tTUPLE {x 10, c \"D\"},\n" +
				"\tTUPLE {x 12, c \"D\"}\n" +
				"}";
		testEquals(expected, src);
	}
	
	@Test
	public void testTupleStar02() {
		String src = "SUMMARIZE myvar: {x := UNION(REL{TUP{*}} INTERSECT REL{TUP{*}})}";
		String expected = 
				"RELATION {x RELATION {x INTEGER, c CHARACTER}} {\n" +
				"\tTUPLE {x RELATION {x INTEGER, c CHARACTER} {\n" +
				"\tTUPLE {x 1, c \"A\"},\n" +
				"\tTUPLE {x 2, c \"A\"},\n" +
				"\tTUPLE {x 3, c \"B\"},\n" +
				"\tTUPLE {x 4, c \"B\"},\n" +
				"\tTUPLE {x 5, c \"B\"},\n" +
				"\tTUPLE {x 10, c \"D\"},\n" +
				"\tTUPLE {x 12, c \"D\"}\n" +
				"}}\n" +
				"}";	
		testEquals(expected, src);
	}
	
	@Test
	public void testTupleStar03() {
		String src = "myvar WHERE (x FROM TUP{*}) = 1";
		String expected = 
				"RELATION {x INTEGER, c CHARACTER} {\n" +
				"\tTUPLE {x 1, c \"A\"}\n" +
				"}";
		testEquals(expected, src);
	}
	
	@Test
	public void testTupleStar04() {
		String src = "EXTEND myvar: {t := REL{TUP{*}}}";
		String expected = 
				"RELATION {x INTEGER, c CHARACTER, t RELATION {x INTEGER, c CHARACTER}} {\n" +
				"\tTUPLE {x 1, c \"A\", t RELATION {x INTEGER, c CHARACTER} {\n" +
				"\tTUPLE {x 1, c \"A\"}\n" +
				"}},\n" +
				"\tTUPLE {x 2, c \"A\", t RELATION {x INTEGER, c CHARACTER} {\n" +
				"\tTUPLE {x 2, c \"A\"}\n" +
				"}},\n" +
				"\tTUPLE {x 3, c \"B\", t RELATION {x INTEGER, c CHARACTER} {\n" +
				"\tTUPLE {x 3, c \"B\"}\n" +
				"}},\n" +
				"\tTUPLE {x 4, c \"B\", t RELATION {x INTEGER, c CHARACTER} {\n" +
				"\tTUPLE {x 4, c \"B\"}\n" +
				"}},\n" +
				"\tTUPLE {x 5, c \"B\", t RELATION {x INTEGER, c CHARACTER} {\n" +
				"\tTUPLE {x 5, c \"B\"}\n" +
				"}},\n" +
				"\tTUPLE {x 10, c \"D\", t RELATION {x INTEGER, c CHARACTER} {\n" +
				"\tTUPLE {x 10, c \"D\"}\n" +
				"}},\n" +
				"\tTUPLE {x 12, c \"D\", t RELATION {x INTEGER, c CHARACTER} {\n" +
				"\tTUPLE {x 12, c \"D\"}\n" +
				"}}\n" +
				"}";
		testEquals(expected, src);
	}
	
	@Test
	public void testTupleStar05() {
		String src = "IMAGE_IN(myvar, TUP {c 'A'})";
		String expected = 
				"RELATION {x INTEGER} {\n" +
				"\tTUPLE {x 1},\n" +
				"\tTUPLE {x 2}\n" +
				"}";
		testEquals(expected, src);
	}
	
	@Test
	public void testTupleStar06() {
		String src = "!!(myvar, TUP {c 'A'})";
		String expected = 
				"RELATION {x INTEGER} {\n" +
				"\tTUPLE {x 1},\n" +
				"\tTUPLE {x 2}\n" +
				"}";
		testEquals(expected, src);
	}
	
	@Test
	public void testTupleStar07() {
		String src = "‼(myvar, TUP {c 'A'})";
		String expected = 
				"RELATION {x INTEGER} {\n" +
				"\tTUPLE {x 1},\n" +
				"\tTUPLE {x 2}\n" +
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
