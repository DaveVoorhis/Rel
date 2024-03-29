package org.reldb.rel.tests.external;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reldb.rel.tests.helpers.BaseOfTest;

public class TestExternal05 extends BaseOfTest {
	
	@BeforeClass
	public static void testExternal05() {
		String src =
			"BEGIN; " +
			"SET VerboseExternalCompilation On; " +
			"OPERATOR ZOT(s CHAR) Java FOREIGN\n" + 
			"  System.out.println(s);\n" +
			"END OPERATOR;" +
			"END;" +
			"true";
		String expected = "true";
		testEquals(expected, src);
	}
	
	@Test
	public void testExternal06() {
		String src =
			"BEGIN; CALL ZOT('Jello, whirled.'); END; true";
		String expected = "true";
		testEquals(expected, src);
	}
	
	@AfterClass
	public static void testExternal07() {
		String src =
			"BEGIN;" +
			"DROP OPERATOR ZOT(CHAR);" +
			"END;" +
			"true";
		String expected = "true";
		testEquals(expected, src);
	}

}
