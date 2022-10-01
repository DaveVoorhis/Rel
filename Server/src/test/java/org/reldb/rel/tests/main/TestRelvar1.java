package org.reldb.rel.tests.main;

import org.junit.After;
import org.junit.Test;
import org.reldb.rel.tests.helpers.BaseOfTest;

public class TestRelvar1 extends BaseOfTest {

	@Test
	public void testRelvar1() {
		String src = 
			"BEGIN;\n" +
				"VAR blah REAL RELATION {x INTEGER, y BOOLEAN, z RATIONAL} KEY {x} KEY {z};\n" +
			"END;\n" +
			"true";
		testEquals("true", src);
	}

	@After
	public void testRelvar2() {
		String src = 
			"BEGIN;\n" +
				"DROP VAR blah;" +
			"END;\n" +
			"true";
		testEquals("true", src);
	}

}
