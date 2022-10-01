package org.reldb.rel.tests.main;

import org.junit.After;
import org.junit.Test;
import org.reldb.rel.tests.helpers.BaseOfTest;

public class TestRelvar15 extends BaseOfTest {
	
	@Test
	public void testRelvar15() {
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
				"UPDATE testvar WHERE x > 2 AND x < 5 : {y := y + 1.0};" +
			"END;" +
			"testvar";
		testEquals("RELATION {x INTEGER, y RATIONAL, z CHARACTER} {\n\tTUPLE {x 1, y 4.5, z \"test\"},\n\tTUPLE {x 2, y 2.5, z \"test\"},\n\tTUPLE {x 3, y 4.2, z \"glub\"},\n\tTUPLE {x 4, y 5.5, z \"glub\"},\n\tTUPLE {x 5, y 5.2, z \"test\"},\n\tTUPLE {x 6, y 3.1, z \"zot\"},\n\tTUPLE {x 7, y 4.5, z \"test\"}\n}", src);	
	}
	
	@After
	public void testRelvar16() {
		String src = 
			"BEGIN;\n" +
				"DROP VAR testvar;" +
			"END;\n" +
			"true";
		testEquals("true", src);
	}

}
