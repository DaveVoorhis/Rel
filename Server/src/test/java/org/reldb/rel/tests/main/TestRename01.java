package org.reldb.rel.tests.main;

import org.junit.Test;
import org.reldb.rel.tests.helpers.BaseOfTest;

public class TestRename01 extends BaseOfTest {
	
	@Test
	public void testRename0() {
		String src = "TUPLE {x 1, y 2.2} RENAME {x AS y, y AS x}";
		testEquals("TUPLE {y 1, x 2.2}", src);
	}
	
	@Test
	public void testRename1() {
		String src = "RELATION {TUPLE {x 1, y 2.2}} RENAME {x AS y, y AS x}";
		testEquals("RELATION {y INTEGER, x RATIONAL} {\n\tTUPLE {y 1, x 2.2}\n}", src);
	}
	
}
