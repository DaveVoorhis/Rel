package org.reldb.rel.tests.main;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.reldb.rel.tests.BaseOfTest;

public class TestDInsert0 extends BaseOfTest {

	@BeforeClass
	public static void testDInsertBefore() {
		String src =
				"BEGIN;" +
				"   VAR myvar REAL RELATION {x INTEGER, y CHAR} KEY {x};" +
				"   myvar := RELATION {" +
				"      TUPLE {x 1, y 'zot'}," +
				"      TUPLE {x 2, y 'zap'}," +
				"      TUPLE {x 3, y 'zip'}," +
				"      TUPLE {x 4, y 'bap'}," +
				"      TUPLE {x 5, y 'cap'}" +
				"   };" +
				"END;" +
				"true";
		String expected = "true";
		testEquals(expected, src);
	}
	
	@Test
	public void testDInsert01() {
		String src =
			"BEGIN;" +
			"   D_INSERT myvar RELATION {TUPLE {y 'zip', x 6}, TUPLE {x 7, y 'zaz'}};" +
			"END;" +
			"myvar";
		String expected = 
				"RELATION {x INTEGER, y CHARACTER} {\n" +
					"\tTUPLE {x 1, y \"zot\"},\n" +
					"\tTUPLE {x 2, y \"zap\"},\n" +
					"\tTUPLE {x 3, y \"zip\"},\n" +
					"\tTUPLE {x 4, y \"bap\"},\n" +
					"\tTUPLE {x 5, y \"cap\"},\n" +
					"\tTUPLE {x 6, y \"zip\"},\n" +
					"\tTUPLE {x 7, y \"zaz\"}\n" +
				"}";
		testEquals(expected, src);
	}
	
	@AfterClass
	public static void testDInsertAfter() {
		String src =
			"BEGIN;" +
			"   DROP VAR myvar;" +
			"END;" +
			"true";
		String expected = "true";
		testEquals(expected, src);
	}

}
