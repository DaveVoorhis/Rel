package org.reldb.rel.tests.helpers;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class JDBCHelper extends BaseOfTest {
	private final static String user = "";
	private final static String password = "";

	protected static final String address = "jdbc:h2:./target/test_h2_jdbc.h2";
	protected static final String table = "TEST";
	protected static final String absolutePath = address + "," + user + "," + password + "," + table;
	protected static final String wrongAbsolutePath = address + ",user,password,nodb.TEZT,driverlocation,driver";

	protected void sqlExec(String query) {
		try (Connection connect = DriverManager.getConnection(address, user, password)) {
			try (Statement statement = connect.createStatement()) {
				statement.executeUpdate(query);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected void sqlExecIgnoreErrors(String query) {
		try (Connection connect = DriverManager.getConnection(address, user, password)) {
			try (Statement statement = connect.createStatement()) {
				statement.executeUpdate(query);
			}
		} catch (SQLException e) {
		}
	}
}
