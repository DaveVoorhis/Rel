package ca.mb.armchair.rel3.tests.main;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.mb.armchair.rel3.tests.BaseOfTest;

public class TestPossrep42 extends BaseOfTest {

  	@BeforeClass
  	public static void testPossrep42() {
  		String src =
  			"BEGIN;" +
  			"TYPE TheBaseType" +
  			"  POSSREP {x INTEGER, y INTEGER};" +
  			"TYPE TheDerivedType1 IS {" +
  			"  TheBaseType" +
  			"  CONSTRAINT THE_x(TheBaseType) = 0 AND THE_y(TheBaseType) > 5" +
  			"  POSSREP {a = THE_y(TheBaseType)}" +
  			"};" +
  			"TYPE TheDerivedType2 IS {" +
  			"  TheBaseType" +
  			"  CONSTRAINT THE_x(TheBaseType) > 5 AND THE_y(TheBaseType) = 0" +
  			"  POSSREP {a = THE_x(TheBaseType)}" +
  			"};" +
  			"END; true";
  		String expected = "true";
  		testEquals(expected, src);
  		
  		src =
  			"BEGIN;" +
  			"OPERATOR blah(x TheBaseType, y INTEGER) RETURNS INTEGER;" +
  			"  RETURN THE_x(x) * y;" +
  			"END OPERATOR;" +
  			"OPERATOR blah(x TheDerivedType1, y INTEGER) RETURNS INTEGER;" +
  			"  RETURN THE_a(x) + y;" +
  			"END OPERATOR;" +
  			"OPERATOR blah(x TheDerivedType2, y INTEGER) RETURNS INTEGER;" +
  			"  RETURN THE_a(x) - y;" +
  			"END OPERATOR;" +
  			"END; true";
  		expected = "true";
  		testEquals(expected, src);
  		
  		src =
  			"BEGIN;" +
  			"VAR myvar REAL RELATION {x TheBaseType} KEY {x};" +
  			"INSERT myvar RELATION {" +
  			"  TUPLE {x TheBaseType(0, 7)}," +
  			"  TUPLE {x TheBaseType(7, 0)}," +
  			"  TUPLE {x TheBaseType(7, 7)}," +
  			"  TUPLE {x TheBaseType(2, 3)}" +
  			"};" +
  			"END; true";
  	  	expected = "true";
  	  	testEquals(expected, src);  		
  	}

  	@Test
  	public void testPossrep43() {
  		String src = "IS_TheDerivedType1(TheBaseType(0, 6))";
  		String expected = "true";
  		testEquals(expected, src);
  	}

  	@Test
  	public void testPossrep44() {
  		String src = "IS_TheDerivedType1(TheBaseType(0, 3))";
  		String expected = "false";
  		testEquals(expected, src);
  	}
  	
  	@Test
  	public void testPossrep46() {
  		String src = "blah(TheBaseType(5, 3), 6)";
  		String expected = "30";
  		testEquals(expected, src);
  	}

  	@Test
  	public void testPossrep47() {
  		String src = "blah(TheBaseType(7, 0), 3)";
  		String expected = "4";
  		testEquals(expected, src);
  	}
  	
  	@Test
  	public void testPossrep48() {
  		String src = "blah(TheBaseType(0, 7), 3)";
  		String expected = "10";
  		testEquals(expected, src);
  	}
  	
  	@AfterClass
  	public static void testPossrep51() {
  		String src =
  			"BEGIN;" +
  			"DROP VAR myvar;" +
  			"DROP OPERATOR blah(TheBaseType, INTEGER);" +
  			"DROP OPERATOR blah(TheDerivedType1, INTEGER);" +
  			"DROP OPERATOR blah(TheDerivedType2, INTEGER);" +
  			"DROP TYPE TheDerivedType1;" +
  			"DROP TYPE TheDerivedType2;" +
  			"DROP TYPE TheBaseType;" +
  			"END; true";
  		String expected = "true";
  		testEquals(expected, src);
  	}

}
