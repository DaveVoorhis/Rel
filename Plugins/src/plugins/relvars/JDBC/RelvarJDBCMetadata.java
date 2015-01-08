package plugins.relvars.JDBC;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.interpreter.ClassPathHack;
import org.reldb.rel.v0.storage.RelDatabase;
import org.reldb.rel.v0.storage.relvars.RelvarCustomMetadata;
import org.reldb.rel.v0.storage.relvars.RelvarExternal;
import org.reldb.rel.v0.storage.relvars.RelvarGlobal;
import org.reldb.rel.v0.storage.relvars.RelvarHeading;
import org.reldb.rel.v0.storage.tables.TableExternal.DuplicateHandling;
import org.reldb.rel.v0.types.Heading;
import org.reldb.rel.v0.types.builtin.TypeBoolean;
import org.reldb.rel.v0.types.builtin.TypeCharacter;
import org.reldb.rel.v0.types.builtin.TypeInteger;
import org.reldb.rel.v0.types.builtin.TypeRational;

public class RelvarJDBCMetadata extends RelvarCustomMetadata {
	public static final long serialVersionUID = 0;

	private String sourceCode;
	private String address; // "jdbc:mysql://localhost"
	private String user; // "sqluser"
	private String password; // "sqluserpw"
	private String table; // "FEEDBACK.RELVAR"
	private String driverLocation; // "/mysql-connector-java-5.1.25-bin.jar";
	private String driver; // "com.mysql.jdbc.Driver"
	private DuplicateHandling duplicates;

	// var myvar external jdbc
	// "jdbc:mysql://localhost,sqluser,sqluserpw,FEEDBACK.RELVAR,/mysql-connector-java-5.1.25-bin.jar,com.mysql.jdbc.Driver";

	private static RelvarHeading getHeading(String spec, DuplicateHandling duplicates) {
		String[] values = spec.split(",");
		if (values.length > 6)
			throw new ExceptionSemantic("EX0014: Too many arguments. Expected only: URL,USER,PASSWORD,DATABASE.TABLE,DRIVER_LOCATION,DRIVER");
		String address = null;
		String user = null;
		String password = null;
		String table = null;
		String driverLocation = null;
		String driver = null;
		try {
			address = values[0];
			user = values[1];
			password = values[2];
			table = values[3];
			driverLocation = values[4];
			driver = values[5];
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new ExceptionSemantic("EX0015: Expected: URL,USER,PASSWORD,DATABASE.TABLE,DRIVER_LOCATION,DRIVER. Found: " + spec);
		}
		try {
			ClassPathHack.addFile(driverLocation);
			Class.forName(driver);
			Connection connect = DriverManager.getConnection(address, user, password);
			Statement statement = connect.createStatement();
			ResultSet resultSet = statement.executeQuery("select * from " + table);
			Heading heading = new Heading();
			if (duplicates == DuplicateHandling.DUP_COUNT)
				heading.add("DUP_COUNT", TypeInteger.getInstance());
			else if (duplicates == DuplicateHandling.AUTOKEY)
				heading.add("AUTO_KEY", TypeInteger.getInstance());
			for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++)
				switch (resultSet.getMetaData().getColumnType(i)) {
				case Types.BIT:
					// heading.add(resultSet.getMetaData().getColumnName(i),
					// Type????.getInstance());
					break;
				case Types.TINYINT:
					heading.add(resultSet.getMetaData().getColumnName(i), TypeInteger.getInstance());
					break;
				case Types.SMALLINT:
					heading.add(resultSet.getMetaData().getColumnName(i), TypeInteger.getInstance());
					break;
				case Types.INTEGER:
					heading.add(resultSet.getMetaData().getColumnName(i), TypeInteger.getInstance());
					break;
				case Types.BIGINT:
					heading.add(resultSet.getMetaData().getColumnName(i), TypeInteger.getInstance());
					break;
				case Types.FLOAT:
					heading.add(resultSet.getMetaData().getColumnName(i), TypeRational.getInstance());
					break;
				case Types.REAL:
					heading.add(resultSet.getMetaData().getColumnName(i), TypeRational.getInstance());
					break;
				case Types.DOUBLE:
					heading.add(resultSet.getMetaData().getColumnName(i), TypeRational.getInstance());
					break;
				case Types.NUMERIC:
					heading.add(resultSet.getMetaData().getColumnName(i), TypeInteger.getInstance());
					break;
				case Types.DECIMAL:
					heading.add(resultSet.getMetaData().getColumnName(i), TypeInteger.getInstance());
					break;
				case Types.CHAR:
					heading.add(resultSet.getMetaData().getColumnName(i), TypeCharacter.getInstance());
					break;
				case Types.VARCHAR:
					heading.add(resultSet.getMetaData().getColumnName(i), TypeCharacter.getInstance());
					break;
				case Types.LONGVARCHAR:
					heading.add(resultSet.getMetaData().getColumnName(i), TypeCharacter.getInstance());
					break;
				case Types.DATE:
					// heading.add(resultSet.getMetaData().getColumnName(i),
					// Type????.getInstance());
					break;
				case Types.TIME:
					// heading.add(resultSet.getMetaData().getColumnName(i),
					// Type????.getInstance());
					break;
				case Types.TIMESTAMP:
					// heading.add(resultSet.getMetaData().getColumnName(i),
					// Type????.getInstance());
					break;
				case Types.BINARY:
					// heading.add(resultSet.getMetaData().getColumnName(i),
					// Type????.getInstance());
					break;
				case Types.VARBINARY:
					// heading.add(resultSet.getMetaData().getColumnName(i),
					// Type????.getInstance());
					break;
				case Types.LONGVARBINARY:
					// heading.add(resultSet.getMetaData().getColumnName(i),
					// Type????.getInstance());
					break;
				case Types.NULL:
					// heading.add(resultSet.getMetaData().getColumnName(i),
					// Type????.getInstance());
					break;
				case Types.OTHER:
					// heading.add(resultSet.getMetaData().getColumnName(i),
					// Type????.getInstance());
					break;
				case Types.JAVA_OBJECT:
					// heading.add(resultSet.getMetaData().getColumnName(i),
					// Type????.getInstance());
					break;
				case Types.DISTINCT:
					// heading.add(resultSet.getMetaData().getColumnName(i),
					// Type????.getInstance());
					break;
				case Types.STRUCT:
					// heading.add(resultSet.getMetaData().getColumnName(i),
					// Type????.getInstance());
					break;
				case Types.ARRAY:
					// heading.add(resultSet.getMetaData().getColumnName(i),
					// Type????.getInstance());
					break;
				case Types.BLOB:
					// heading.add(resultSet.getMetaData().getColumnName(i),
					// Type????.getInstance());
					break;
				case Types.CLOB:
					// heading.add(resultSet.getMetaData().getColumnName(i),
					// Type????.getInstance());
					break;
				case Types.REF:
					// heading.add(resultSet.getMetaData().getColumnName(i),
					// Type????.getInstance());
					break;
				case Types.DATALINK:
					// heading.add(resultSet.getMetaData().getColumnName(i),
					// Type????.getInstance());
					break;
				case Types.BOOLEAN:
					heading.add(resultSet.getMetaData().getColumnName(i), TypeBoolean.getInstance());
					break;
				case Types.ROWID:
					// heading.add(resultSet.getMetaData().getColumnName(i),
					// Type????.getInstance());
					break;
				case Types.NCHAR:
					heading.add(resultSet.getMetaData().getColumnName(i), TypeCharacter.getInstance());
					break;
				case Types.NVARCHAR:
					heading.add(resultSet.getMetaData().getColumnName(i), TypeCharacter.getInstance());
					break;
				case Types.NCLOB:
					// heading.add(resultSet.getMetaData().getColumnName(i),
					// Type????.getInstance());
					break;
				case Types.SQLXML:
					// heading.add(resultSet.getMetaData().getColumnName(i),
					// Type????.getInstance());
					break;
				}
			return new RelvarHeading(heading);
		} catch (SQLException e) {
			throw new ExceptionSemantic("EX0016: " + e.toString());
		} catch (ClassNotFoundException e) {
			throw new ExceptionSemantic("EX0017: " + e.toString());
		} catch (IOException e) {
			throw new ExceptionSemantic("EX0018: " + e.toString());
		}
	}

	@Override
	public String getSourceDefinition() {
		return "EXTERNAL JDBC " + sourceCode;
	}

	public RelvarJDBCMetadata(RelDatabase database, String owner, String spec, DuplicateHandling duplicates) {
		super(database, getHeading(spec, duplicates), owner);
		String[] values = spec.split(",");
		address = values[0];
		user = values[1];
		password = values[2];
		table = values[3];
		driverLocation = values[4];
		driver = values[5];
		this.duplicates = duplicates;
		sourceCode = "\" " + address + "," + user + "," + password + "," + table + "," + driverLocation + "," + driver + "\" " + duplicates;
	}

	@Override
	public RelvarGlobal getRelvar(String name, RelDatabase database) {
		try {
			ClassPathHack.addFile(driverLocation);
			Class.forName(driver);
			Connection connect = DriverManager.getConnection(address, user, password);
			Statement statement = connect.createStatement();
			statement.executeQuery("select * from " + table);
		} catch (SQLException e) {
			throw new ExceptionSemantic("EX0019: Table " + table + " doesn't exist anymore.");
		} catch (IOException e) {
			throw new ExceptionSemantic("EX0020: Driver not found at: " + driverLocation);
		} catch (ClassNotFoundException e) {
		}
		return new RelvarExternal(name, database, new Generator(database, System.out), this, duplicates);
	}

	@Override
	public void dropRelvar(RelDatabase database) {
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

	public List<Integer> getTypesList() {
		List<Integer> list = new ArrayList<Integer>();
		try {
			ClassPathHack.addFile(driverLocation);
			Class.forName(driver);
			Connection connect = DriverManager.getConnection(address, user, password);
			Statement statement = connect.createStatement();
			ResultSet resultSet = statement.executeQuery("select * from " + table);
			for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++)
				list.add(resultSet.getMetaData().getColumnType(i));
			return list;
		} catch (SQLException e) {
		} catch (ClassNotFoundException e) {
		} catch (IOException e) {
		}
		return null;
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
