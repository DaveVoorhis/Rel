package org.reldb.rel.tests.main;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reldb.rel.tests.BaseOfTest;

public class TestPossrep36 extends BaseOfTest {
  	
  	@BeforeClass
  	public static void testPossrep36() {
  		String src =
  			"BEGIN;" +
  			"TYPE AnotherBaseType" +
  			"  POSSREP {x INTEGER, y INTEGER};" +
  			"TYPE AnotherDerivedType IS {" +
  			"  AnotherBaseType" +
  			"  POSSREP {a INTEGER, b INTEGER}" +
  			"};" +
  			"END; true";
  		String expected = "true";
  		testEquals(expected, src);
  	}

  	@Test
  	public void testPossrep37() {
  		String src = "IS_AnotherBaseType(AnotherBaseType(2, 3))";
  		String expected = "true";
  		testEquals(expected, src);
  	}
  	
  	@Test
  	public void testPossrep38() {
  		String src = "IS_AnotherBaseType(AnotherDerivedType(2, 3))";
  		String expected = "true";
  		testEquals(expected, src);
  	}
  	
  	@Test
  	public void testPossrep39() {
  		String src = "IS_AnotherDerivedType(AnotherBaseType(2, 3))";
  		String expected = "false";
  		testEquals(expected, src);
  	}
  	
  	@Test
  	public void testPossrep40() {
  		String src = "IS_AnotherDerivedType(AnotherDerivedType(2, 3))";
  		String expected = "true";
  		testEquals(expected, src);
  	}
  	
  	@AfterClass
  	public static void testPossrep41() {
  		String src =
  			"BEGIN;" +
  			"DROP TYPE AnotherDerivedType;" +
  			"DROP TYPE AnotherBaseType;" +
  			"END; true";
  		String expected = "true";
  		testEquals(expected, src);
  	}

}
