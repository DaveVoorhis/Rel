package org.reldb.rel.tests.main;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reldb.rel.tests.helpers.BaseOfTest;

public class TestRelvar34 extends BaseOfTest {

	@BeforeClass
	public static void testBefore() {
		String src = 
			"BEGIN;\n" +
			" VAR S BASE RELATION { SNO CHAR, SNAME CHAR, STATUS INTEGER, CITY CHAR } KEY {SNO};" +
			" INSERT S RELATION {TUPLE {SNO 'S2', SNAME 'Dave', STATUS 2, CITY 'Derby'}, TUPLE {SNO 'S3', SNAME 'Bob', STATUS 3, CITY 'London'}};" +
			" VAR SP BASE RELATION { SNO CHAR, PNO CHAR, QTY INTEGER } KEY {SNO, PNO};" +
			" INSERT SP RELATION {TUPLE {SNO 'S2', PNO 'P1', QTY 100}, TUPLE {SNO 'S3', PNO 'P2', QTY 200}};" +
			" VAR SPQ BASE INIT(EXTEND S: {PQ := RELATION {TUPLE {SNO SNO}} COMPOSE SP}) KEY{SNO};" +
			"END;\n" +
			"true";
		testEquals("true", src);
	}

	@Test
	public void test01() {
		String src = 
			"BEGIN;\n" +
			"  UPDATE SPQ WHERE SNO = 'S2': {INSERT PQ RELATION {TUPLE {PNO \"P5\", QTY 500}}};" +
		    "END;\n" +
			"SPQ";
		String expected = 
				"RELATION {SNO CHARACTER, SNAME CHARACTER, STATUS INTEGER, CITY CHARACTER, PQ RELATION {PNO CHARACTER, QTY INTEGER}} {\n" +
				"\tTUPLE {SNO \"S2\", SNAME \"Dave\", STATUS 2, CITY \"Derby\", PQ RELATION {PNO CHARACTER, QTY INTEGER} {\n" +
				"\tTUPLE {PNO \"P1\", QTY 100},\n" +
				"\tTUPLE {PNO \"P5\", QTY 500}\n" +
				"}},\n" +
				"\tTUPLE {SNO \"S3\", SNAME \"Bob\", STATUS 3, CITY \"London\", PQ RELATION {PNO CHARACTER, QTY INTEGER} {\n" +
				"\tTUPLE {PNO \"P2\", QTY 200}\n" +
				"}}\n" +
				"}";
		testEquals(expected, src);
	}
	
	@AfterClass
	public static void testRelvar37() {
		String src = 
			"BEGIN;\n" +
				"DROP VAR S;" +
				"DROP VAR SP;" +
				"DROP VAR SPQ;" +
			"END;\n" +
			"true";
		String expected = "true";
		testEquals(expected, src);
	}

}
