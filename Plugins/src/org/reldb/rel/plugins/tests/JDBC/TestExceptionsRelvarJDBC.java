package org.reldb.rel.plugins.tests.JDBC;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.plugins.tests.TestMySQLJDBCSettings;
import org.reldb.rel.v0.interpreter.ClassPathHack;

public class TestExceptionsRelvarJDBC extends TestMySQLJDBCSettings {

	@Before
	public void testJDBC1() {
		try {
			ClassPathHack.addFile(driverLocation);
			Class.forName(driver);
			Connection connect = DriverManager.getConnection(address, user, password);
			Statement statement = connect.createStatement();
			String command = "create database " + database + ";";
			statement.executeUpdate(command);
			command = "use " + database;
			statement.executeQuery(command);
			command = "CREATE TABLE " + table + " (A INT, B INT, C INT);";
			try {
				statement.executeUpdate(command);
			} catch (SQLException e) {
			}

		} catch (SQLException e) {
		} catch (ClassNotFoundException e) {
		} catch (IOException e) {
		}

		String src = "BEGIN;\n" + "var myvar external jdbc \"" + absolutePath + "\" dup_remove;" + "END;\n" + "true";
		testEquals("true", src);
	}

	@Test(expected = ExceptionSemantic.class)
	public void testJDBC2() { // Calling relvar after manually deleting database
		try {
			ClassPathHack.addFile(driverLocation);
			Class.forName(driver);
			Connection connect = DriverManager.getConnection(address, user, password);
			Statement statement = connect.createStatement();
			;
			statement.executeUpdate("drop database " + database);
		} catch (SQLException e) {
		} catch (ClassNotFoundException e) {
		} catch (IOException e) {
		}

		String src = "myvar";
		testEvaluate(src);
	}

	@Test(expected = ExceptionSemantic.class)
	public void testJDBC3() { // Creating relvar from non-existing
		// database.table
		String src = "BEGIN;\n" + "var brokenVAR external jdbc \"" + wrongAbsolutePath + "\" dup_remove;" + "END;\n";
		testEvaluate(src);
	}

	@Test(expected = ExceptionSemantic.class)
	public void testJDBC4() { // Creating relvar with non-identified duplicate
		// handling method
		String src = "BEGIN;\n" + "var brokenVAR external jdbc \"" + absolutePath + "\" something;" + "END;\n";
		testEvaluate(src);
	}

	@After
	public void testJDBC5() { // Drop relvar and database
		try {
			ClassPathHack.addFile(driverLocation);
			Class.forName(driver);
			Connection connect = DriverManager.getConnection(address, user, password);
			Statement statement = connect.createStatement();
			statement.executeUpdate("drop database " + database);
		} catch (SQLException e) {
		} catch (ClassNotFoundException e) {
		} catch (IOException e) {
		}

		String src = "BEGIN;\n" + "drop var myvar;" + "END;\n" + "true";
		testEquals("true", src);
	}
}
