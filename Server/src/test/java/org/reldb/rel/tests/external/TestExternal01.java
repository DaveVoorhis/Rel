package org.reldb.rel.tests.external;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reldb.rel.tests.BaseOfTest;

public class TestExternal01 extends BaseOfTest {

	@BeforeClass
	public static void testExternal01() {
		String src =
			"BEGIN;" +
			"OPERATOR COMPARE_TO(s CHAR, anotherString CHAR) RETURNS INTEGER Java FOREIGN\n" + 
			"  // Compares two strings lexicographically.\n" +
			"  return ValueInteger.select(context.getGenerator(), s.stringValue().compareTo(anotherString.stringValue()));\n" +
			"END OPERATOR;" +
			"END;" +
			"true";
		String expected = "true";
		testEquals(expected, src);
	}
	
	@Test
	public void testExternal02() {
		String src =
			"COMPARE_TO('string 1', 'string 2')";
		String expected = "-1";
		testEquals(expected, src);
	}
	
	@Test
	public void testExternal03() {
		String src =
			"COMPARE_TO('string 1', 'string 1')";
		String expected = "0";
		testEquals(expected, src);
	}
	
	@AfterClass
	public static void testExternal04() {
		String src =
			"BEGIN;" +
			"DROP OPERATOR COMPARE_TO(CHAR, CHAR);" +
			"END;" +
			"true";
		String expected = "true";
		testEquals(expected, src);
	}

}
