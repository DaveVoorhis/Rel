package org.reldb.rel.plugins.tests.JDBC;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.reldb.rel.plugins.tests.TestMySQLJDBCSettings;
import org.reldb.rel.v0.interpreter.ClassPathHack;

public class TestExternalRelvarJDBC1 extends TestMySQLJDBCSettings {

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
			statement.executeUpdate(command);
			command = "INSERT INTO " + table + " values (1, 2, 3);";
			statement.executeUpdate(command);
			command = "INSERT INTO " + table + " values (4, 5, 6);";
			statement.executeUpdate(command);
			command = "INSERT INTO " + table + " values (7, 8, 9);";
			statement.executeUpdate(command);

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String src = "BEGIN;\n" + "var myvar external jdbc \"" + absolutePath + "\" autokey;" + "END;\n" + "true";
		testEquals("true", src);
	}

	@Test
	public void testJDBC2() {
		String src = "myvar";
		testEquals("RELATION {AUTO_KEY INTEGER, A INTEGER, B INTEGER, C INTEGER} {" + "\n\tTUPLE {AUTO_KEY 1, A 1, B 2, C 3},"
				+ "\n\tTUPLE {AUTO_KEY 2, A 4, B 5, C 6}," + "\n\tTUPLE {AUTO_KEY 3, A 7, B 8, C 9}\n}", src);
	}

	@After
	public void testJDBC3() {
		String src = "BEGIN;\n" + "drop var myvar;" + "END;\n" + "true";
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
		testEquals("true", src);
	}
}
