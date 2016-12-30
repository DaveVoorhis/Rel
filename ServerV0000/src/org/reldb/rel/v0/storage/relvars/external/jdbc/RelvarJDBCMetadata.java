package org.reldb.rel.v0.storage.relvars.external.jdbc;

import java.net.MalformedURLException;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;

import org.reldb.rel.exceptions.ExceptionFatal;
import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.generator.SelectAttributes;
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
	private String address; 		// "jdbc:postgresql://localhost/database"
	private String user; 			// "sqluser"
	private String password; 		// "sqluserpw"
	private String table; 			// "FEEDBACK.RELVAR"

	// var myvar external jdbc
	// "jdbc:postgresql://localhost/database,sqluser,sqluserpw,FEEDBACK.RELVAR";
	
	private DuplicateHandling duplicates;

	private static boolean driversLoaded = false;
	
	public static void loadDrivers(RelDatabase database) {
		if (driversLoaded)
			return;
		if (!ClassPathHack.isInOSGI()) {
			ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
			ClassLoader newClassLoader;
			try {
				newClassLoader = new JDBCDriverLoader(database.getExtensionDirectory());
			    Thread.currentThread().setContextClassLoader(newClassLoader);
			} catch (MalformedURLException e) {
				throw new ExceptionFatal("RS0471: Malforumed URL in RelvarJDBCMetadata.loadDrivers(): " + e);
			} finally {
			    Thread.currentThread().setContextClassLoader(originalClassLoader);
			}
		}
		driversLoaded = true;
	}
	
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
	
	public static RelvarHeading getHeading(RelDatabase database, String spec, DuplicateHandling duplicates) {
		String[] values = CSVLineParse.parseTrimmed(spec);	
		if (values.length != 4)
			throw new ExceptionSemantic("EX0014: Invalid arguments. Expected: URL, USER, PASSWORD, DATABASE.TABLE but got " + spec);
		String address = values[0];
		String user = values[1];
		String password = values[2];
		String table = values[3];
		try {
			loadDrivers(database);
			Connection connect = DriverManager.getConnection(address, user, password);
			Statement statement = connect.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM " + table);
			Heading heading = new Heading();
			if (duplicates == DuplicateHandling.DUP_COUNT)
				heading.add("_DUP_COUNT", TypeInteger.getInstance());
			else if (duplicates == DuplicateHandling.AUTOKEY)
				heading.add("_AUTOKEY", TypeInteger.getInstance());
			for (int column = 1; column <= resultSet.getMetaData().getColumnCount(); column++)
				heading.add(ColumnName.cleanName(resultSet.getMetaData().getColumnName(column)), TypeCharacter.getInstance());
			RelvarHeading relvarHeading = new RelvarHeading(heading);
			if (duplicates == DuplicateHandling.AUTOKEY) {
				SelectAttributes keyAttribute = new SelectAttributes();
				keyAttribute.add("_AUTOKEY");
				relvarHeading.addKey(keyAttribute);
			}
			return relvarHeading;
		} catch (SQLException e) {
			throw new ExceptionSemantic("EX0016: " + e.toString() + ". Drivers available: " + obtainDriverList());
		}
	}

	@Override
	public String getSourceDefinition() {
		return "EXTERNAL JDBC \"" + address + ", " + user + ", " + password + ", " + table + "\" " + duplicates;
	}

	public RelvarJDBCMetadata(RelDatabase database, String owner, String spec, DuplicateHandling duplicates) {
		super(database, getHeading(database, spec, duplicates), owner);		
		String[] values = CSVLineParse.parseTrimmed(spec);
		address = values[0];
		user = values[1];
		password = values[2];
		table = values[3];
		this.duplicates = duplicates;
		connectionString = spec;
	}

	@Override
	public RelvarGlobal getRelvar(String name, RelDatabase database) {
		try {
			loadDrivers(database);
			Connection connect = DriverManager.getConnection(address, user, password);
			Statement statement = connect.createStatement();
			statement.executeQuery("select * from " + table);
		} catch (SQLException e) {
			throw new ExceptionSemantic("EX0019: Table " + table + " no longer exists.");
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

	@Override
	public String tableClassName() {
		return "TableJDBC";
	}

	@Override
	public String getType() {
		return "JDBC";
	}
}
