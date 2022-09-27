package org.reldb.rel.tests.ext_relvar.jdbc;



import org.reldb.rel.tests.BaseOfTest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCSettings extends BaseOfTest {
	protected static final String address = "jdbc:sqlite:target/test_sqlite_jdbc.db";
	protected static final String table = "TEST";
	protected static final String absolutePath = address + ",user,password," + table;
	protected static final String wrongAbsolutePath = address + ",user,password,nodb.TEZT,driverlocation,driver";

	protected void sqlExec(String query) {
		try (Connection connect = DriverManager.getConnection(address)) {
			try (Statement statement = connect.createStatement()) {
				statement.executeUpdate(query);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
