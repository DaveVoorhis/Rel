package org.reldb.rel.tests.ext_relvar.jdbc;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.reldb.rel.exceptions.ExceptionSemantic;

public class TestExternalRelvarJDBCExceptions extends JDBCSettings {
	@Before
	public void before() {
		sqlExecIgnoreErrors("DROP TABLE " + table + ";");
		sqlExec("CREATE TABLE " + table + " (A INT, B INT, C INT);");
		String src = "BEGIN;\n" + "var myvar external jdbc \"" + absolutePath + "\" dup_remove;" + "END;\n" + "true";
		testEquals("true", src);
	}

	@Test(expected = ExceptionSemantic.class)
	public void testFailDropOnDeleted() { 		sqlExec("drop table " + table);
		String src = "myvar";
		testEvaluate(src);
	}

	@Test(expected = ExceptionSemantic.class)
	public void testFailCreateOnNonExistentTable() {
		String src = "BEGIN;\n" + "var brokenVAR external jdbc \"" + wrongAbsolutePath + "\" dup_remove;" + "END;\n";
		testEvaluate(src);
	}

	@Test(expected = ExceptionSemantic.class)
	public void testRelvarWithNonIdentifiedDuplicateHandlingMethod() {
		String src = "BEGIN;\n" + "var brokenVAR external jdbc \"" + absolutePath + "\" something;" + "END;\n";
		testEvaluate(src);
	}

	@After
	public void after() { // Drop relvar and database
		String src = "BEGIN;\n" + "drop var myvar;" + "END;\n" + "true";
		testEquals("true", src);
		sqlExec("drop table " + table);
	}
}
