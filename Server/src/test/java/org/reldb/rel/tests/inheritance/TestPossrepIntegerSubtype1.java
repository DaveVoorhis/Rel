package org.reldb.rel.tests.inheritance;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reldb.rel.tests.helpers.BaseOfTest;

public class TestPossrepIntegerSubtype1 extends BaseOfTest {
  	
  	@BeforeClass
  	public static void testPossrepIntegerSubtype1() {
  		String src = 
  			"BEGIN;" +
  			"TYPE myInteger IS {" +
  			"  INTEGER" +
  			"  CONSTRAINT THE_VALUE(INTEGER) > 0 AND THE_VALUE(INTEGER) < 10" +
  			"  POSSREP {a = THE_VALUE(INTEGER)}" +
  			"};" +
  			"END; true"; 
  		String expected = "true";
  		testEquals(expected, src);
  	}
  	
  	@Test
  	public void testPossrepIntegerSubtype2() {
  		String src = "INTEGER(3)"; 
  		String expected = "myInteger(3)";
  		testEquals(expected, src);	
  	}
  	  	
  	@Test
  	public void testPossrepIntegerSubtype3() {
  		String src = "3"; 
  		String expected = "myInteger(3)";
  		testEquals(expected, src);	
  	}

  	@Test
  	public void testPossrepIntegerSubtype4() {
  		String src = "INTEGER(13)"; 
  		String expected = "13";
  		testEquals(expected, src);	
  	}
  	
  	@Test
  	public void testPossrepIntegerSubtype5() {
  		String src = "13"; 
  		String expected = "13";
  		testEquals(expected, src);	
  	}
  	
  	@Test
  	public void testPossrepIntegerSubtype6() {
  		String src = "THE_a(7)";
  		String expected = "myInteger(7)";
  		testEquals(expected, src);	
  	}
  	
  	@AfterClass
  	public static void testPossrepIntegerSubtype7() {
  		String src =
  			"BEGIN;" +
  			"DROP TYPE myInteger;" +
  			"END; true";
  		String expected = "true";
  		testEquals(expected, src);  	
  	}

}
