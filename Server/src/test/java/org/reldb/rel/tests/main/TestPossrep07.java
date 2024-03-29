package org.reldb.rel.tests.main;

import org.junit.After;
import org.junit.Test;
import org.reldb.rel.tests.helpers.BaseOfTest;

public class TestPossrep07 extends BaseOfTest {
  	
  	@Test
  	public void testPossrep07() {
		String src =
			"BEGIN;" +
			"TYPE testType " + 
			"  POSSREP testType1 {blah INTEGER}" +
			"  POSSREP testType2 {zot CHAR, zap INTEGER}" +			
  			" INIT" +
  			"  testType1 (zot := 'x', zap := blah)" +
  			"  testType2 (blah := zap);" +
			"OUTPUT testType1(3);" +
			"END; true";
		String expected = "true";
		testEquals(expected, src);  		
  	}
  	
  	@After
  	public void testPossrep08() {
		String src =
			"BEGIN;" +
			"DROP TYPE testType;" +
			"END; true";
		String expected = "true";
		testEquals(expected, src);  		
  	}

}
