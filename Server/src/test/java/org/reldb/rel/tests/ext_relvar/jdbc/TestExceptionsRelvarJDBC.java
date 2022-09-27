package org.reldb.rel.tests.ext_relvar.jdbc;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.reldb.rel.exceptions.ExceptionSemantic;

@Ignore
public class TestExceptionsRelvarJDBC extends JDBCSettings {
	@Before
	public void testJDBC1() {
		sqlExec("CREATE TABLE " + table + " (A INT, B INT, C INT);");
//		String src = "BEGIN;\n" + "var myvar external jdbc \"" + absolutePath + "\" dup_remove;" + "END;\n" + "true";
//		testEquals("true", src);
	}

	@Test(expected = ExceptionSemantic.class)
	public void testJDBC2() { // Calling relvar after manually deleting database
		sqlExec("drop table " + table);
//		String src = "myvar";
//		testEvaluate(src);
	}

	@Test(expected = ExceptionSemantic.class)
	public void testJDBC3() { // Creating relvar from non-existing
		// database.table
//		String src = "BEGIN;\n" + "var brokenVAR external jdbc \"" + wrongAbsolutePath + "\" dup_remove;" + "END;\n";
//		testEvaluate(src);
	}

	@Test(expected = ExceptionSemantic.class)
	public void testJDBC4() { // Creating relvar with non-identified duplicate
		// handling method
//		String src = "BEGIN;\n" + "var brokenVAR external jdbc \"" + absolutePath + "\" something;" + "END;\n";
//		testEvaluate(src);
	}

	@After
	public void testJDBC5() { // Drop relvar and database
//		String src = "BEGIN;\n" + "drop var myvar;" + "END;\n" + "true";
//		testEquals("true", src);
		sqlExec("drop table " + table);
	}
}
