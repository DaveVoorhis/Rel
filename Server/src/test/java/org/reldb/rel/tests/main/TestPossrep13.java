package org.reldb.rel.tests.main;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reldb.rel.tests.helpers.BaseOfTest;

public class TestPossrep13 extends BaseOfTest {
  	
  	@BeforeClass
  	public static void testPossrep13() {
  		String src = 
  			"BEGIN;" +
  			"TYPE blah" +
  			"  POSSREP blah1 {x INTEGER, y INTEGER}" +
  			"  POSSREP blah2 {a INTEGER, b INTEGER}" +
  			" INIT" +
  			"  blah1 (a := x * 2, b := y * 2)" +
  			"  blah2 (x := a / 2, y := b / 2);" +
  			"END; true";
  		String expected = "true";
  		testEquals(expected, src);
  	}
  	
  	@Test
  	public void testPossrep14() {
  		String src = "THE_x(blah1(10, 12))";
  		String expected = "10";
  		testEquals(expected, src);
  	}
  	
  	@Test
  	public void testPossrep15() {
  		String src = "THE_y(blah1(10, 12))";
  		String expected = "12";
  		testEquals(expected, src);
  	}
  	
  	@Test
  	public void testPossrep16() {
  		String src = "THE_a(blah1(10, 12))";
  		String expected = "20";
  		testEquals(expected, src);
  	}

  	@Test
  	public void testPossrep17() {
  		String src = "THE_b(blah1(10, 12))";
  		String expected = "24";
  		testEquals(expected, src);
  	}
  	
  	@Test
  	public void testPossrep18() {
  		String src = "THE_x(blah2(10, 12))";
  		String expected = "5";
  		testEquals(expected, src);
  	}
  	
  	@Test
  	public void testPossrep19() {
  		String src = "THE_y(blah2(10, 12))";
  		String expected = "6";
  		testEquals(expected, src);
  	}
  	
  	@Test
  	public void testPossrep20() {
  		String src = "THE_a(blah2(10, 12))";
  		String expected = "10";
  		testEquals(expected, src);
  	}

  	@Test
  	public void testPossrep21() {
  		String src = "THE_b(blah2(10, 12))";
  		String expected = "12";
  		testEquals(expected, src);
  	}
	
  	@AfterClass
  	public static void testPossrep22() {
  		String src =
  			"BEGIN;" +
  			"DROP TYPE blah;" +
  			"END; true";
  		String expected = "true";
  		testEquals(expected, src);
  	}

}
