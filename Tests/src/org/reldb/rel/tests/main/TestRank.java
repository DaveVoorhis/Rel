package org.reldb.rel.tests.main;

import org.junit.Test;
import org.reldb.rel.tests.BaseOfTest;

public class TestRank extends BaseOfTest {

	@Test
	public void testRank1() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {x INT, y INT, z RATIONAL} {\n" +
		        "}) KEY {ALL BUT};\n" +
			"END;" +
			"a RANK (ASC y, DESC z AS Ranking)";
		testEquals("RELATION {x INTEGER, y INTEGER, z RATIONAL, Ranking INTEGER} {\n}", src);
	}
	
	@Test
	public void testRank2() {
		String src =
			"BEGIN;" +
		        "VAR a PRIVATE INIT(relation {\n" +
				"  TUPLE {x 2, y 3, z 2.2},\n" +
				"  TUPLE {x 3, y 3, z 2.2},\n" +
				"  TUPLE {x 4, y 4, z 1.2},\n" +
				"  TUPLE {x 5, y 4, z 2.2}\n" +
		        "}) KEY {ALL BUT};\n" +
			"END;" +
			"a RANK (ASC y, DESC z AS Ranking)";
		testEquals("RELATION {x INTEGER, y INTEGER, z RATIONAL, Ranking INTEGER} {\n" +
				"\tTUPLE {x 2, y 3, z 2.2, Ranking 1},\n" +
				"\tTUPLE {x 3, y 3, z 2.2, Ranking 1},\n" +
				"\tTUPLE {x 5, y 4, z 2.2, Ranking 2},\n" +
				"\tTUPLE {x 4, y 4, z 1.2, Ranking 3}\n" +
				"}", src);
	}

}
