package org.reldb.rel.v0.storage.relvars.external.jdbc;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;

import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.interpreter.ClassPathHack;
import org.reldb.rel.v0.storage.RelDatabase;
import org.reldb.rel.v0.storage.relvars.RelvarCustomMetadata;
import org.reldb.rel.v0.storage.relvars.RelvarExternal;
import org.reldb.rel.v0.storage.relvars.RelvarGlobal;
import org.reldb.rel.v0.storage.relvars.RelvarHeading;
import org.reldb.rel.v0.storage.relvars.external.CSVLineParse;
import org.reldb.rel.v0.storage.relvars.external.ColumnName;
import org.reldb.rel.v0.storage.tables.TableExternal.DuplicateHandling;
import org.reldb.rel.v0.types.Heading;
import org.reldb.rel.v0.types.builtin.TypeCharacter;
import org.reldb.rel.v0.types.builtin.TypeInteger;

public class RelvarJDBCMetadata extends RelvarCustomMetadata {
	public static final long serialVersionUID = 0;

	private String connectionString;
	private String address; 		// "jdbc:mysql://localhost"
	private String user; 			// "sqluser"
	private String password; 		// "sqluserpw"
	private String table; 			// "FEEDBACK.RELVAR"
	private String driverLocation;	// "/mysql-connector-java-5.1.25-bin.jar";
	private String driver; 			// "com.mysql.jdbc.Driver"

	// var myvar external jdbc
	// "jdbc:mysql://localhost,sqluser,sqluserpw,FEEDBACK.RELVAR,/mysql-connector-java-5.1.25-bin.jar,com.mysql.jdbc.Driver";
	
	private DuplicateHandling duplicates;

	private static String obtainDriverList() {
		String list = "";
		Enumeration<Driver> drivers = DriverManager.getDrivers();
		while (drivers.hasMoreElements()) {
			Driver driver = drivers.nextElement();
			if (!list.isEmpty())
				list += ", ";
			list += driver.getClass().getName();
		}
		return (list.isEmpty()) ? "<none>" : list;
	}
	
	public static RelvarHeading getHeading(String spec, DuplicateHandling duplicates) {
		String[] values = CSVLineParse.parse(spec);	
		if (values.length != 6)
			throw new ExceptionSemantic("EX0014: Invalid arguments. Expected: URL, USER, PASSWORD, DATABASE.TABLE, DRIVER_LOCATION, DRIVER but got " + spec);
		String address = values[0];
		String user = values[1];
		String password = values[2];
		String table = values[3];
		String driverLocation = values[4];
		String driver = values[5];
		try {
			if (!ClassPathHack.isInOSGI()) {
				ClassPathHack.addFile(driverLocation);
				Class.forName(driver);
			}
			Connection connect = DriverManager.getConnection(address, user, password);
			Statement statement = connect.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM " + table);
			Heading heading = new Heading();
			if (duplicates == DuplicateHandling.DUP_COUNT)
				heading.add("DUP_COUNT", TypeInteger.getInstance());
			else if (duplicates == DuplicateHandling.AUTOKEY)
				heading.add("AUTO_KEY", TypeInteger.getInstance());
			for (int column = 1; column <= resultSet.getMetaData().getColumnCount(); column++)
				heading.add(ColumnName.cleanName(resultSet.getMetaData().getColumnName(column)), TypeCharacter.getInstance());
			return new RelvarHeading(heading);
		} catch (SQLException e) {
			throw new ExceptionSemantic("EX0016: " + e.toString() + ". Drivers available: " + obtainDriverList());
		} catch (ClassNotFoundException e) {
			throw new ExceptionSemantic("EX0017: " + e.toString());
		} catch (IOException e) {
			throw new ExceptionSemantic("EX0018: " + e.toString());
		}
	}

	@Override
	public String getSourceDefinition() {
		return "EXTERNAL JDBC " + address + ", " + user + ", " + password + ", " + table + ", " + driverLocation + ", " + driver + "\" " + duplicates;
	}

	public RelvarJDBCMetadata(RelDatabase database, String owner, String spec, DuplicateHandling duplicates) {
		super(database, getHeading(spec, duplicates), owner);		
		String[] values = CSVLineParse.parse(spec);
		address = values[0];
		user = values[1];
		password = values[2];
		table = values[3];
		driverLocation = values[4];
		driver = values[5];
		this.duplicates = duplicates;
		connectionString = spec;
	}

	@Override
	public RelvarGlobal getRelvar(String name, RelDatabase database) {
		try {
			if (!ClassPathHack.isInOSGI()) {
				ClassPathHack.addFile(driverLocation);
				Class.forName(driver);
			}
			Connection connect = DriverManager.getConnection(address, user, password);
			Statement statement = connect.createStatement();
			statement.executeQuery("select * from " + table);
		} catch (SQLException e) {
			throw new ExceptionSemantic("EX0019: Table " + table + " no longer exists.");
		} catch (IOException e) {
			throw new ExceptionSemantic("EX0020: Driver not found at: " + driverLocation);
		} catch (ClassNotFoundException e) {
		}
		return new RelvarExternal(name, database, new Generator(database, System.out), this, duplicates);
	}

	@Override
	public void dropRelvar(RelDatabase database) {
	}

	public String getConnectionString() {
		return connectionString;
	}
	
	public String getPath() {
		return address;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public String getTable() {
		return table;
	}

	public String getDriverLocation() {
		return driverLocation;
	}

	public String getDriver() {
		return driver;
	}

	@Override
	public String tableClassName() {
		return "TableJDBC";
	}

	@Override
	public String getType() {
		return "JDBC";
	}
}
