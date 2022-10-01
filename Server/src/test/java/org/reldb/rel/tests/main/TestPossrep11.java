package org.reldb.rel.tests.main;

import org.junit.After;
import org.junit.Test;
import org.reldb.rel.tests.helpers.BaseOfTest;

public class TestPossrep11 extends BaseOfTest {

  	@Test
  	public void testPossrep11() {
		String src =
			"BEGIN;" +
			"TYPE baseType UNION;" +
			"TYPE derivedType1 IS {" +
			"  baseType" +
			"  POSSREP {blah INTEGER}" +
			"};" +
			"TYPE derivedType2 IS {" +
			"  baseType" +
			"  POSSREP zot {blah CHAR}" +
			"};" +
			"END; true";
		String expected = "true";
		testEquals(expected, src);  		
  	}
  	
  	@After
  	public void testPossrep12() {
		String src =
			"BEGIN;" +
			"DROP TYPE derivedType1;" +
			"DROP TYPE derivedType2;" +
			"DROP TYPE baseType;" +
			"END; true";
		String expected = "true";
		testEquals(expected, src);  		
  	}

}
