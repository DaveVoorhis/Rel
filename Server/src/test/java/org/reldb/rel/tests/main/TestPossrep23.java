package org.reldb.rel.tests.main;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reldb.rel.tests.helpers.BaseOfTest;

public class TestPossrep23 extends BaseOfTest {
  	
  	@BeforeClass
  	public static void testPossrep23() {
  		// Recursive tagged union
  		String src = 
  			"BEGIN;" +
  			"TYPE StringTree UNION;" +
  			"TYPE node IS {StringTree POSSREP {string CHAR, leftTree StringTree, rightTree StringTree}};" +
  			"TYPE nothing IS {StringTree POSSREP nothing {}};" +
  			"END; true";
  		String expected = "true";
  		testEquals(expected, src);
  	}
  	
  	@Test
  	public void testPossrep24() {
  		String src = "node('x', node('y', nothing(), nothing()), nothing())";
  		String expected = "node(\"x\", node(\"y\", nothing(), nothing()), nothing())";
  		testEquals(expected, src);
  	}

  	@Test
  	public void testPossrep25() {
  		String src = "node('x', nothing(), nothing()) = node('x', nothing(), nothing())";
  		String expected = "true";
  		testEquals(expected, src);
  	}

  	@Test
  	public void testPossrep26() {
  		String src = "node('x', nothing(), nothing()) = node('x', nothing(), nothing())";
  		String expected = "true";
  		testEquals(expected, src);
  	}

  	@Test
  	public void testPossrep27() {
  		String src = "node('y', nothing(), nothing()) <> node('x', nothing(), nothing())";
  		String expected = "true";
  		testEquals(expected, src);
  	}

  	@Test
  	public void testPossrep28() {
  		String src = "node('y', node('x', nothing(), nothing()), nothing()) <> node('x', nothing(), nothing())";
  		String expected = "true";
  		testEquals(expected, src);
  	}

  	@Test
  	public void testPossrep30() {
  		String src = "nothing() = nothing()";
  		String expected = "true";
  		testEquals(expected, src);
  	}

  	@AfterClass
  	public static void testPossrep31() {
  		String src =
  			"BEGIN;" +
  			"DROP TYPE nothing;" +
  			"DROP TYPE node;" +
  			"DROP TYPE StringTree;" +
  			"END; true";
  		String expected = "true";
  		testEquals(expected, src);
  	}

}
