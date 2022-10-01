package org.reldb.rel.tests.ext_relvar.jdbc;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.reldb.rel.tests.helpers.JDBCHelper;

public class TestExternalRelvarJDBCDupCount extends JDBCHelper {
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
		String src = "BEGIN;\n" + "var myvar external jdbc \"" + absolutePath + "\" dup_count;" + "END;\n" + "true";
		testEquals("true", src);
	}

	@Test
	public void testRelvarJDBCDupCount() {
		String src = "myvar";
		testEquals("RELATION {_DUP_COUNT INTEGER, A CHARACTER, B CHARACTER, C CHARACTER} {"
				+ "\n\tTUPLE {_DUP_COUNT 2, A \"1\", B \"2\", C \"3\"},"
				+ "\n\tTUPLE {_DUP_COUNT 3, A \"4\", B \"5\", C \"6\"},"
				+ "\n\tTUPLE {_DUP_COUNT 2, A \"7\", B \"8\", C \"9\"}\n}", src);
	}

	@After
	public void after() {
		String src = "BEGIN;\n" + "drop var myvar;" + "END;\n" + "true";
		testEquals("true", src);
		sqlExec("drop table " + table);
	}
}
