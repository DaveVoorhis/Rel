package org.reldb.rel.tests.main;

import org.junit.After;
import org.junit.Test;
import org.reldb.rel.tests.BaseOfTest;

public class TestPossrep09 extends BaseOfTest {
  	
  	@Test
  	public void testPossrep09() {
		String src =
			"BEGIN;" +
			"TYPE testType POSSREP {blah INTEGER};" +
			"VAR x testType;" +
			"x := testType(3);" +
			"OUTPUT x;" +
			"END; true";
		String expected = "true";
		testEquals(expected, src);  		
  	}
  	
  	@After
  	public void testPossrep10() {
		String src =
			"BEGIN;" +
			"DROP TYPE testType;" +
			"END; true";
		String expected = "true";
		testEquals(expected, src);  		
  	}

}
