package org.reldb.rel.tests.main;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reldb.rel.tests.BaseOfTest;

public class TestPossrepDate1 extends BaseOfTest {
  	
  	@BeforeClass
  	public static void testPossrepDate1() {
  		String src =
  			"BEGIN;" +
  			"TYPE Date UNION;" +
  			"TYPE DateValid IS {Date POSSREP {year INTEGER, month INTEGER, day INTEGER}};" +
  			"TYPE DateNone IS {Date POSSREP {}};" +
  			"TYPE DateUnknown IS {Date POSSREP {}};" +
  			"TYPE DateUserRefusedToAnswer IS {Date POSSREP {reason CHAR}};" +
  			"END; true";
  		String expected = "true";
  		testEquals(expected, src);
  	}

  	@Test
  	public void testPossrepDate2() {
  		String src =
  			"DateValid(2010, 3, 27)";
  		String expected = "DateValid(2010, 3, 27)";
  		testEquals(expected, src);
  	}

  	@Test
  	public void testPossrepDate3() {
  		String src =
  			"DateUserRefusedToAnswer('Privacy violation')";
  		String expected = "DateUserRefusedToAnswer(\"Privacy violation\")";
  		testEquals(expected, src);
  	}

  	@AfterClass
  	public static void testPossrepDate99() {
  		String src =
  			"BEGIN;" +
  			"DROP TYPE DateUserRefusedToAnswer;" +
  			"DROP TYPE DateUnknown;" +
  			"DROP TYPE DateNone;" +
  			"DROP TYPE DateValid;" +
  			"DROP TYPE Date;" +
  			"END; true";
  		String expected = "true";
  		testEquals(expected, src);
  	}

}
