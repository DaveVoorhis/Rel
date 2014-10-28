package ca.mb.armchair.rel3.tests.main;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.mb.armchair.rel3.tests.BaseOfTest;

public class TestPossrepTemperature01 extends BaseOfTest {
  	
  	@BeforeClass
  	public static void testPossrepTemperature01_start() {
  		String src =
  			"BEGIN;" +
  			"TYPE Temperature UNION;" +
  			"TYPE Temperature_Normal IS {Temperature POSSREP {t INTEGER}};" +
  			"TYPE Temperature_NoReading IS {Temperature POSSREP {}};" +
  			"TYPE Temperature_OutOfRange IS {Temperature POSSREP {}};" +
  			"VAR Readings REAL RELATION {timestamp INTEGER, temp Temperature} KEY {timestamp};" +
  			"Readings := RELATION {timestamp INTEGER, temp Temperature} {" +
  				"TUPLE {timestamp 12955, temp Temperature_OutOfRange()}," +
  				"TUPLE {timestamp 12956, temp Temperature_NoReading()}," +
  				"TUPLE {timestamp 12957, temp Temperature_Normal(33)}," +
  				"TUPLE {timestamp 12958, temp Temperature_Normal(44)}," +
  				"TUPLE {timestamp 12959, temp Temperature_Normal(58)}," +
  				"TUPLE {timestamp 12960, temp Temperature_Normal(66)}" +
  			"};" +
  			"END; true";
  		String expected = "true";
  		testEquals(expected, src);
  	}

  	@Test
  	public void testPossrepTemperature01_t01() {
  		String src = "Readings WHERE temp = Temperature_Normal(33)";
  		String expected = "RELATION {timestamp INTEGER, temp Temperature} {\n" +
  				"\tTUPLE {timestamp 12957, temp Temperature_Normal(33)}\n" +
  				"}";
  		testEquals(expected, src);
  	}

  	@Test
  	public void testPossrepTemperature01_t02() {
  		String src = "Readings WHERE temp = Temperature_Normal(30)";
  		String expected = "RELATION {timestamp INTEGER, temp Temperature} {\n" +
  							"}";
  		testEquals(expected, src);
  	}

  	@Test
  	public void testPossrepTemperature01_t03() {
  		String src = "Readings WHERE temp = Temperature_OutOfRange()";
  		String expected = "RELATION {timestamp INTEGER, temp Temperature} {\n" +
  				"\tTUPLE {timestamp 12955, temp Temperature_OutOfRange()}\n" +
  				"}";
  		testEquals(expected, src);
  	}

  	@Test
  	public void testPossrepTemperature01_t04() {
  		String src = "IS_Temperature(Temperature_Normal(33))";
  		String expected = "true";
  		testEquals(expected, src);
  	}

  	@Test
  	public void testPossrepTemperature01_t05() {
  		String src = "IS_Temperature(Temperature_OutOfRange())";
  		String expected = "true";
  		testEquals(expected, src);
  	}

  	@Test
  	public void testPossrepTemperature01_t06() {
  		String src = "TREAT_AS_Temperature(Temperature_OutOfRange()) = TREAT_AS_Temperature(Temperature_OutOfRange())";
  		String expected = "true";
  		testEquals(expected, src);
  	}
  	
  	@Test
  	public void testPossrepTemperature01_t07() {
  		String src = "TREAT_AS_Temperature(Temperature_Normal(33)) = TREAT_AS_Temperature(Temperature_Normal(33))";
  		String expected = "true";
  		testEquals(expected, src);
  	}
  	
  	@Test
  	public void testPossrepTemperature01_t08() {
  		String src = "TREAT_AS_Temperature(Temperature_Normal(33)) = TREAT_AS_Temperature(Temperature_Normal(34))";
  		String expected = "false";
  		testEquals(expected, src);
  	}
  	
  	@Test
  	public void testPossrepTemperature01_t09() {
  		String src = "TREAT_AS_Temperature(Temperature_Normal(33)) = TREAT_AS_Temperature(Temperature_OutOfRange())";
  		String expected = "false";
  		testEquals(expected, src);
  	}
  	
  	@Test
  	public void testPossrepTemperature01_t10() {
  		String src = "TREAT_AS_Temperature(Temperature_NoReading()) = TREAT_AS_Temperature(Temperature_OutOfRange())";
  		String expected = "false";
  		testEquals(expected, src);
  	}

  	@AfterClass
  	public static void testPossrepTemperature01_end() {
  		String src =
  			"BEGIN;" +
  			"DROP VAR Readings;" +
  			"DROP TYPE Temperature_OutOfRange;" +
  			"DROP TYPE Temperature_NoReading;" +
  			"DROP TYPE Temperature_Normal;" +
  			"DROP TYPE Temperature;" +
  			"END; true";
  		String expected = "true";
  		testEquals(expected, src);
  	}

}
