package org.reldb.rel.tests.main;

import org.junit.Test;
import org.reldb.rel.tests.BaseOfTest;

public class Test_ForceTestFailure extends BaseOfTest {

	@Test
	public void testTest() {
		// Can be used to force a test failure
		testEquals("1", "1");
	}

}
