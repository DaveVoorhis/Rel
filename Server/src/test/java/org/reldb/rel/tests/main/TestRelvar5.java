package org.reldb.rel.tests.main;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reldb.rel.tests.helpers.BaseOfTest;

public class TestRelvar5 extends BaseOfTest {

	@BeforeClass
	public static void testRelvar4() {
		String src =
			"BEGIN;" +
		        "VAR testvar REAL RELATION {x INTEGER, y RATIONAL, z CHAR} KEY {ALL BUT};" +
		        "testvar := relation {\n" +
		        "       tuple {x 1, y 4.5, z \"test\"},\n" +
		        "       tuple {x 2, y 2.5, z \"test\"},\n" +
		        "       tuple {x 3, y 3.2, z \"glub\"},\n" +
		        "       tuple {x 4, y 4.5, z \"glub\"},\n" +
		        "       tuple {x 5, y 5.2, z \"test\"},\n" +
		        "       tuple {x 6, y 3.1, z \"zot\"},\n" +
		        "       tuple {x 7, y 4.5, z \"test\"}\n" +
		        "};" +
			"END;" +
			"testvar";
		testEquals("RELATION {x INTEGER, y RATIONAL, z CHARACTER} {\n\tTUPLE {x 1, y 4.5, z \"test\"},\n\tTUPLE {x 2, y 2.5, z \"test\"},\n\tTUPLE {x 3, y 3.2, z \"glub\"},\n\tTUPLE {x 4, y 4.5, z \"glub\"},\n\tTUPLE {x 5, y 5.2, z \"test\"},\n\tTUPLE {x 6, y 3.1, z \"zot\"},\n\tTUPLE {x 7, y 4.5, z \"test\"}\n}", src);	
	}

	@Test
	public void testRelvar4a() {
		String src = "testvar";
		testEquals("RELATION {x INTEGER, y RATIONAL, z CHARACTER} {\n\tTUPLE {x 1, y 4.5, z \"test\"},\n\tTUPLE {x 2, y 2.5, z \"test\"},\n\tTUPLE {x 3, y 3.2, z \"glub\"},\n\tTUPLE {x 4, y 4.5, z \"glub\"},\n\tTUPLE {x 5, y 5.2, z \"test\"},\n\tTUPLE {x 6, y 3.1, z \"zot\"},\n\tTUPLE {x 7, y 4.5, z \"test\"}\n}", src);					
	}
	
	@AfterClass
	public static void testRelvar6() {
		String src = 
			"BEGIN;\n" +
				"DROP VAR testvar;" +
			"END;\n" +
			"true";
		testEquals("true", src);
	}

}
