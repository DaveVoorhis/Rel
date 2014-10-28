package ca.mb.armchair.rel3.tests.main;

import org.junit.After;
import org.junit.Test;

import ca.mb.armchair.rel3.tests.BaseOfTest;

public class TestRelvar7 extends BaseOfTest {

	@Test
	public void testRelvar7() {
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
		        "INSERT testvar RELATION {TUPLE {y 2.3, z \"glub\", x 8}};" +
			"END;" +
			"testvar";
		testEquals("RELATION {x INTEGER, y RATIONAL, z CHARACTER} {\n\tTUPLE {x 1, y 4.5, z \"test\"},\n\tTUPLE {x 2, y 2.5, z \"test\"},\n\tTUPLE {x 3, y 3.2, z \"glub\"},\n\tTUPLE {x 4, y 4.5, z \"glub\"},\n\tTUPLE {x 5, y 5.2, z \"test\"},\n\tTUPLE {x 6, y 3.1, z \"zot\"},\n\tTUPLE {x 7, y 4.5, z \"test\"},\n\tTUPLE {x 8, y 2.3, z \"glub\"}\n}", src);			
	}
	
	@After
	public void testRelvar8() {
		String src = 
			"BEGIN;\n" +
				"DROP VAR testvar;" +
			"END;\n" +
			"true";
		testEquals("true", src);
	}

}
