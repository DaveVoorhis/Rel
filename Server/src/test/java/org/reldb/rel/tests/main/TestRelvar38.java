package org.reldb.rel.tests.main;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reldb.rel.tests.BaseOfTest;

public class TestRelvar38 extends BaseOfTest {

	@BeforeClass
	public static void testBefore() {
		String src = 
			"BEGIN;\n" +
			"	VAR TEST5 REAL RELATION {ID INTEGER, RVA RELATION {RID INTEGER, STR CHAR}} KEY {ID};" +
			"	INSERT TEST5 RELATION {" +
			"		TUPLE {ID 1, RVA RELATION {TUPLE {RID 11, STR 'A'}, TUPLE {RID 22, STR 'E'}}}" +
			"	};" +
			"END;\n" +
			"true";
		testEquals("true", src);
	}

	@Test
	public void testRelvar38() {
		String src =
			"BEGIN;\n" +
			"	UPDATE TEST5 WHERE ID = 1: {UPDATE RVA WHERE RID = 22: {STR := 'blah'}};" +
			"	UPDATE TEST5 WHERE ID = 1: {UPDATE RVA WHERE RID = 22: {STR := 'blah'}};" +
			"END; TEST5\n";
		String expected = 
				"RELATION {ID INTEGER, RVA RELATION {RID INTEGER, STR CHARACTER}} {\n" + 
				"\tTUPLE {ID 1, RVA RELATION {RID INTEGER, STR CHARACTER} {\n" + 
				"\tTUPLE {RID 11, STR \"A\"},\n" + 
				"\tTUPLE {RID 22, STR \"blah\"}\n" + 
				"}}\n" + 
				"}";
		testEquals(expected, src);
	}
	
	@AfterClass
	public static void testAfter() {
		String src = 
			"BEGIN;\n" +
				"DROP VAR TEST5;" +
			"END;\n" +
			"true";
		String expected = "true";
		testEquals(expected, src);
	}

}
