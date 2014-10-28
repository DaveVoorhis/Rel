package ca.mb.armchair.rel3.tests.main;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ca.mb.armchair.rel3.tests.BaseOfTest;

public class TestIDelete0 extends BaseOfTest {

	@BeforeClass
	public static void testIDeleteBefore() {
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
	public void testIDelete01() {
		String src =
			"BEGIN;" +
			"   I_DELETE myvar RELATION {TUPLE {y 'zip', x 3}, TUPLE {x 5, y 'cap'}};" +
			"END;" +
			"myvar";
		String expected = 
				"RELATION {x INTEGER, y CHARACTER} {\n" +
					"\tTUPLE {x 1, y \"zot\"},\n" +
					"\tTUPLE {x 2, y \"zap\"},\n" +
					"\tTUPLE {x 4, y \"bap\"}\n" +
				"}";
		testEquals(expected, src);
	}
	
	@AfterClass
	public static void testIDeleteAfter() {
		String src =
			"BEGIN;" +
			"   DROP VAR myvar;" +
			"END;" +
			"true";
		String expected = "true";
		testEquals(expected, src);
	}

}
