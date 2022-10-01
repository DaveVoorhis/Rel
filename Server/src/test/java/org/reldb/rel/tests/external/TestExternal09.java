package org.reldb.rel.tests.external;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reldb.rel.tests.helpers.BaseOfTest;

public class TestExternal09 extends BaseOfTest {

	@BeforeClass
	public static void testExternal08() {
		testEquals("true", "BEGIN; SET VerboseExternalCompilation On; END; true");
		String src =
			"BEGIN;" +
			"TYPE blah Java FOREIGN\n" +
			"private int n;\n" +
			"public blah(Generator generator) {super(generator);};\n" +
			"public int compareTo(Value v) {\n" +
			"   return new Long((long)n).compareTo(v.longValue());\n" +
			"}\n" +
		    "public blah(Generator generator, ValueInteger n) {\n" +
		    "  super(generator);\n" +
		    "  this.n = (int)n.longValue();\n" +
		    "}\n" +
			"public Value getDefaultValue(Generator generator) {\n" +
			"  return new blah(generator, ValueInteger.select(generator, 0));\n" +
			"}\n" +
			"public static ValueInteger N2(Generator generator, blah v) {\n" +
			"  return ValueInteger.select(generator, v.n);\n" +
			"}\n" +
			"public static ValueInteger N5(Generator generator) {\n" +
			"  return ValueInteger.select(generator, 0);\n" +
			"}\n" +
			"public static ValueInteger N4(Generator generator, blah v, ValueInteger x) {\n" +
			"  return ValueInteger.select(generator, v.n + x.longValue());\n" +
			"}\n" +
			"public ValueInteger N3(Generator generator, ValueInteger x) {\n" +
			"  return ValueInteger.select(generator, n + x.longValue());\n" +
			"}\n" +
			"public ValueInteger N1(Generator generator) {\n" +
			"  return ValueInteger.select(generator, n);\n" +
			"}\n" +
			"END TYPE;\n" +
			"END;" +
			"true";
		String expected = "true";
		testEquals(expected, src);
		
		src = "BEGIN; VAR test BASE RELATION {x blah} KEY {x}; END; test";
		expected = "RELATION {x blah} {\n}";
		testEquals(expected, src);
	}
	
	@Test
	public void testExternal09() {
		String src =
			"BEGIN; OUTPUT blah(3); END; true";
		String expected = "true";
		testEquals(expected, src);
	}

	@Test
	public void testExternal11() {
		String src = 
			"THE_N3(blah(2), 5)";
		String expected = "7";
		testEquals(expected, src);
	}
		
	@Test
	public void testExternal12() {
		String src = 
			"N4(blah(2), 5)";
		String expected = "7";
		testEquals(expected, src);
	}
		
	@AfterClass
	public static void testExternal13() {
		String src =
			"BEGIN;" +
			"DROP VAR test;" +
			"DROP TYPE blah;" +
			"END; true";
		String expected = "true";
		testEquals(expected, src);
	}

}
