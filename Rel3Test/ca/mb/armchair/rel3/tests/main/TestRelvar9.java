package ca.mb.armchair.rel3.tests.main;

import org.junit.After;
import org.junit.Test;

import ca.mb.armchair.rel3.tests.BaseOfTest;

public class TestRelvar9 extends BaseOfTest {

	@Test
	public void testRelvar9() {
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
		        "DELETE testvar;" +
			"END;" +
			"testvar";
		testEquals("RELATION {x INTEGER, y RATIONAL, z CHARACTER} {\n}", src);			
	}
	
	@After
	public void testRelvar10() {
		String src = 
			"BEGIN;\n" +
				"DROP VAR testvar;" +
			"END;\n" +
			"true";
		testEquals("true", src);
	}

}
