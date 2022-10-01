package org.reldb.rel.tests.inheritance;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reldb.rel.tests.helpers.BaseOfTest;

public class TestPossrepIntegerSubtype8 extends BaseOfTest {
  	
  	@BeforeClass
  	public static void testPossrepIntegerSubtype8() {
  		String src = 
  			"BEGIN;" +
  			"TYPE myInteger IS {" +
  			"  INTEGER" +
  			"  CONSTRAINT THE_VALUE(INTEGER) > 0 AND THE_VALUE(INTEGER) < 10" +
  			"};" +
  			"END; true"; 
  		String expected = "true";
  		testEquals(expected, src);
  	}

  	@Test
  	public void testPossrepIntegerBuiltin03() {
  		String src = "MAX(RELATION {TUPLE {x 1}, TUPLE {x 2}}, x)";
  		String expected = "myInteger(2)";
  		testEquals(expected, src);
  	}
  
  	@Test
  	public void testPossrepIntegerBuiltin04() {
  		String src = "SUM(RELATION {TUPLE {x 1}, TUPLE {x 2}}, x)";
  		String expected = "myInteger(3)";
  		testEquals(expected, src);
  	}
  	
  	@Test
  	public void testPossrepIntegerSubtype9() {
  		String src = "INTEGER(3)"; 
  		String expected = "myInteger(3)";
  		testEquals(expected, src);	
  	}
  	  	
  	@Test
  	public void testPossrepIntegerSubtype10() {
  		String src = "3"; 
  		String expected = "myInteger(3)";
  		testEquals(expected, src);	
  	}

  	@Test
  	public void testPossrepIntegerSubtype11() {
  		String src = "INTEGER(13)"; 
  		String expected = "13";
  		testEquals(expected, src);	
  	}
  	
  	@Test
  	public void testPossrepIntegerSubtype12() {
  		String src = "13"; 
  		String expected = "13";
  		testEquals(expected, src);	
  	}
  	
  	@AfterClass
  	public static void testPossrepIntegerSubtype13() {
  		String src =
  			"BEGIN;" +
  			"DROP TYPE myInteger;" +
  			"END; true";
  		String expected = "true";
  		testEquals(expected, src);  	
  	}

}
