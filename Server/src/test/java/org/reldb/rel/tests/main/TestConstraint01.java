package org.reldb.rel.tests.main;

import org.junit.After;
import org.junit.Test;
import org.reldb.rel.tests.helpers.BaseOfTest;

public class TestConstraint01 extends BaseOfTest {
	
	@Test
	public void testConstraint01() {
		String src =
			"begin;" +
			"   CONSTRAINT testConstraint01 COUNT(sys.Catalog) > 0;" +
			"end;" +
			"true";
		String expected = "true";
		testEquals(expected, src);
	}
	
	@After
	public void testConstraint02() {
		String src =
			"begin;" +
			"   DROP CONSTRAINT testConstraint01;" +
			"end;" +
			"true";
		String expected = "true";
		testEquals(expected, src);
	}

}
