package org.reldb.rel.tests.ext_relvar.jdbc;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestExternalRelvarJDBCDupRemove extends JDBCSettings {
	@Before
	public void before() {
		sqlExecIgnoreErrors("DROP TABLE " + table + ";");
		sqlExec("CREATE TABLE " + table + " (A INT, B INT, C INT);");
		sqlExec("INSERT INTO " + table + " values (1, 2, 3);");
		sqlExec("INSERT INTO " + table + " values (4, 5, 6);");
		sqlExec("INSERT INTO " + table + " values (4, 5, 6);");
		sqlExec("INSERT INTO " + table + " values (1, 2, 3);");
		sqlExec("INSERT INTO " + table + " values (7, 8, 9);");
		sqlExec("INSERT INTO " + table + " values (7, 8, 9);");
		sqlExec("INSERT INTO " + table + " values (4, 5, 6);");
		String src = "BEGIN;\n" + "var myvar external jdbc \"" + absolutePath + "\" dup_remove;" + "END;\n" + "true";
		testEquals("true", src);
	}

	@Test
	public void testRelvarJDBCDupRemove() {
		testEquals("true", "true");
		String src = "myvar";
		testEquals("RELATION {A CHARACTER, B CHARACTER, C CHARACTER} {"
				+ "\n\tTUPLE {A \"1\", B \"2\", C \"3\"},"
				+ "\n\tTUPLE {A \"4\", B \"5\", C \"6\"},"
				+ "\n\tTUPLE {A \"7\", B \"8\", C \"9\"}\n}", src);
	}

	@After
	public void after() {
		String src = "BEGIN;\n" + "drop var myvar;" + "END;\n" + "true";
		testEquals("true", src);
		sqlExec("drop table " + table);
	}
}
