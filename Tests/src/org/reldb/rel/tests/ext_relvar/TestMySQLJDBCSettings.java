package org.reldb.rel.tests.ext_relvar;



import org.reldb.rel.tests.BaseOfTest;

public class TestMySQLJDBCSettings extends BaseOfTest {

	public static final String user = "root";
	public static final String password = "fozzle";

	public static final String address = "jdbc:mysql://localhost";
	public static final String driverLocation = "lib/mysql-connector-java-5.1.25-bin.jar";
	public static final String driver = "com.mysql.jdbc.Driver";
	public static final String database = "EXCEPTION";
	public static final String table = "TEST";
	public static final String wrongTable = "TEZT";
	public static final String absolutePath = address + "," + user + "," + password + "," + database + "." + table + "," + driverLocation + "," + driver;
	public static final String wrongAbsolutePath = address + "," + user + "," + password + "," + database + "." + wrongTable + "," + driverLocation + "," + driver;
}
