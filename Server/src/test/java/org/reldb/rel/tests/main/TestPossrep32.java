package org.reldb.rel.tests.main;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.tests.BaseOfTest;

import static org.junit.Assert.assertEquals;

public class TestPossrep32 extends BaseOfTest {
  	
  	@BeforeClass
  	public static void testPossrep32() {
  		String src =
  			"BEGIN;" +
  			"TYPE posint" +
  			"  POSSREP {x INTEGER CONSTRAINT x > 0};" +
  			"END; true";
  		String expected = "true";
  		testEquals(expected, src);
  	} 
  	
  	@Test
  	public void testPossrep33() {
  		String src = "posint(1)";
  		String expected = "posint(1)";
  		testEquals(expected, src);
  	}

  	@Test
  	public void testPossrep34() {
  		String src = "posint(-1)";
  		String expected = 
  			"RS0037: Selector posint(INTEGER) violates POSSREP constraint in type 'posint'.\n" +
  			"Line 1, column 9 near '1'\n\n";
  		try {
  			testEquals(expected, src);
  		} catch (ExceptionSemantic es) {
  			assertEquals(expected, es.getMessage().substring(0, expected.length()));
  		}
  	}

  	@AfterClass
  	public static void testPossrep35() {
  		String src =
  			"BEGIN;" +
  			"DROP TYPE posint;" +
  			"END; true";
  		String expected = "true";
  		testEquals(expected, src);  		
  	}

}
