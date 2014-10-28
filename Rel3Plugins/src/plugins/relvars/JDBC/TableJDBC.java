package plugins.relvars.JDBC;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import ca.mb.armchair.rel3.exceptions.ExceptionSemantic;
import ca.mb.armchair.rel3.generator.Generator;
import ca.mb.armchair.rel3.interpreter.ClassPathHack;
import ca.mb.armchair.rel3.storage.relvars.RelvarExternal;
import ca.mb.armchair.rel3.storage.relvars.RelvarExternalMetadata;
import ca.mb.armchair.rel3.storage.tables.TableCustom;
import ca.mb.armchair.rel3.values.RelTupleFilter;
import ca.mb.armchair.rel3.values.RelTupleMap;
import ca.mb.armchair.rel3.values.TupleFilter;
import ca.mb.armchair.rel3.values.TupleIterator;
import ca.mb.armchair.rel3.values.TupleIteratorCount;
import ca.mb.armchair.rel3.values.TupleIteratorUnique;
import ca.mb.armchair.rel3.values.Value;
import ca.mb.armchair.rel3.values.ValueBoolean;
import ca.mb.armchair.rel3.values.ValueCharacter;
import ca.mb.armchair.rel3.values.ValueInteger;
import ca.mb.armchair.rel3.values.ValueRational;
import ca.mb.armchair.rel3.values.ValueRelation;
import ca.mb.armchair.rel3.values.ValueTuple;
import ca.mb.armchair.rel3.vm.Context;

public class TableJDBC extends TableCustom {

	private Generator generator;
	private String address;
	private String user;
	private String password;
	private String table;
	private String driver;
	private DuplicateHandling duplicates;
	private Connection connect;
	private Statement statement;
	private List<Integer> typeList;

	public TableJDBC(String Name, RelvarExternalMetadata metadata, Generator generator, DuplicateHandling duplicates) {
		RelvarJDBCMetadata meta = (RelvarJDBCMetadata) metadata;
		this.generator = generator;
		address = meta.getPath();
		user = meta.getUser();
		password = meta.getPassword();
		table = meta.getTable();
		driver = meta.getDriver();
		typeList = meta.getTypesList();
		this.duplicates = duplicates;
		try {
			ClassPathHack.addFile(meta.getDriverLocation());
			Class.forName(driver);
			connect = DriverManager.getConnection(address, user, password);
			statement = connect.createStatement();
		} catch (SQLException e) {
			throw new ExceptionSemantic("EX0021: " + address + "' not found.");
		} catch (ClassNotFoundException e) {
			throw new ExceptionSemantic("EX0022: " + e.toString());
		} catch (IOException e) {
			throw new ExceptionSemantic("EX0023: " + e.toString());
		}
	}

	private ValueTuple toTuple(String line) {
		String[] rawValues = line.split(",");
		Value[] values = new Value[rawValues.length];

		int startAt = 0;
		if (duplicates == DuplicateHandling.AUTOKEY) {
			values[0] = ValueInteger.select(generator, Integer.parseInt(rawValues[0]));
			startAt = 1;
		}

		int i = startAt;
		for (Integer type : typeList) {
			try {
				switch (type) {
				case Types.BIT:
					break;
				case Types.TINYINT:
					values[i] = ValueInteger.select(generator, Integer.parseInt(rawValues[i]));
					break;
				case Types.SMALLINT:
					values[i] = ValueInteger.select(generator, Integer.parseInt(rawValues[i]));
					break;
				case Types.INTEGER:
					values[i] = ValueInteger.select(generator, Integer.parseInt(rawValues[i]));
					break;
				case Types.BIGINT:
					values[i] = ValueInteger.select(generator, Integer.parseInt(rawValues[i]));
					break;
				case Types.FLOAT:
					values[i] = ValueRational.select(generator, Float.parseFloat(rawValues[i]));
					break;
				case Types.REAL:
					values[i] = ValueRational.select(generator, Float.parseFloat(rawValues[i]));
					break;
				case Types.DOUBLE:
					values[i] = ValueRational.select(generator, Float.parseFloat(rawValues[i]));
					break;
				case Types.NUMERIC:
					values[i] = ValueInteger.select(generator, Integer.parseInt(rawValues[i]));
					break;
				case Types.DECIMAL:
					values[i] = ValueInteger.select(generator, Integer.parseInt(rawValues[i]));
					break;
				case Types.CHAR:
					values[i] = ValueCharacter.select(generator, rawValues[i]);
					break;
				case Types.VARCHAR:
					values[i] = ValueCharacter.select(generator, rawValues[i]);
					break;
				case Types.LONGVARCHAR:
					values[i] = ValueCharacter.select(generator, rawValues[i]);
					break;
				case Types.DATE:
					break;
				case Types.TIME:
					break;
				case Types.TIMESTAMP:
					break;
				case Types.BINARY:
					break;
				case Types.VARBINARY:
					break;
				case Types.LONGVARBINARY:
					break;
				case Types.NULL:
					break;
				case Types.OTHER:
					break;
				case Types.JAVA_OBJECT:
					break;
				case Types.DISTINCT:
					break;
				case Types.STRUCT:
					break;
				case Types.ARRAY:
					break;
				case Types.BLOB:
					break;
				case Types.CLOB:
					break;
				case Types.REF:
					break;
				case Types.DATALINK:
					break;
				case Types.BOOLEAN:
					values[i] = ValueBoolean.select(generator, Boolean.parseBoolean(rawValues[i]));
					break;
				case Types.ROWID:
					break;
				case Types.NCHAR:
					values[i] = ValueCharacter.select(generator, rawValues[i]);
					break;
				case Types.NVARCHAR:
					values[i] = ValueCharacter.select(generator, rawValues[i]);
					break;
				case Types.NCLOB:
					break;
				case Types.SQLXML:
					break;
				}
			} catch (NumberFormatException e) {
			}
			i++;
		}
		return new ValueTuple(generator, values);
	}

	private String getRow(ResultSet resultSet) throws SQLException {
		StringBuffer currentLine = new StringBuffer();
		for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++)
			currentLine.append(resultSet.getString(i) + ",");
		return currentLine.substring(0, currentLine.length() - 1).toString();
	}

	@Override
	public TupleIterator iterator() {
		try {
			if (duplicates == DuplicateHandling.DUP_REMOVE)
				return dupremoveIterator();
			else if (duplicates == DuplicateHandling.DUP_COUNT)
				return dupcountIterator();
			else if (duplicates == DuplicateHandling.AUTOKEY)
				return autokeyIterator();
			throw new ExceptionSemantic("EX0024: Non-Identified duplicate handling method: " + duplicates.toString());
		} catch (SQLException e) {
			throw new ExceptionSemantic("EX0025: Failed to create iterator.");
		}
	}

	@Override
	public TupleIterator iterator(Generator generator) {
		return iterator();
	}

	@Override
	public long getCardinality() {
		try {
			ResultSet resultSet = statement.executeQuery("select count(*) from " + table);
			resultSet.next();
			return resultSet.getInt(1);
		} catch (SQLException e) {
		}
		return 0;
	}

	private static void notImplemented(String what) {
		throw new ExceptionSemantic("EX0026: JDBC relvars do not yet support " + what);
	}

	@Override
	public boolean contains(Generator generator, ValueTuple tuple) {
		while (iterator().hasNext())
			if (tuple.equals(iterator().next()))
				return true;
		return false;
	}

	@Override
	public ValueTuple getTupleForKey(Generator generator, ValueTuple tuple) {
		return null;
	}

	@Override
	public void setValue(RelvarExternal relvarJDBC, ValueRelation relation) {
		notImplemented("assignment");
	}

	@Override
	public long insert(Generator generator, ValueRelation relation) {
		long count = 0;
		TupleIterator iterator = relation.iterator();
		while (iterator.hasNext())
			count += insert(generator, iterator.next());
		return count;
	}

	@Override
	public long insert(Generator generator, ValueTuple tuple) {
		try {
			Value[] values = tuple.getValues();
			StringBuffer command = new StringBuffer("insert into " + table + " values (");
			for (int i = 0; i < values.length; i++)
				command.append("\'" + values[i].toString() + "\',");
			PreparedStatement preparedStatement = connect.prepareStatement(command.substring(0, command.length() - 1) + ");");
			preparedStatement.executeUpdate();
			return 1;
		} catch (SQLException e) {
			return 0;
		}
	}

	@Override
	public long insertNoDuplicates(Generator generator, ValueRelation relation) {
		long count = 0;
		TupleIterator iterator = relation.iterator();
		while (iterator.hasNext()) {
			ValueTuple tuple = iterator.next();
			if (!contains(generator, tuple))
				count += insert(generator, tuple);
		}
		return count;
	}

	@Override
	public void purge() {
		PreparedStatement preparedStatement;
		try {
			preparedStatement = connect.prepareStatement("delete from " + table);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
		}
	}

	@Override
	public void delete(Generator generator, ValueTuple tuple) {
		PreparedStatement preparedStatement;
		String[] values = tuple.toCSV().split(",");
		StringBuffer line = new StringBuffer("delete from " + table + " where ");
		try {
			ResultSet resultSet = statement.executeQuery("select * from " + table);
			for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
				int type = resultSet.getMetaData().getColumnType(i);
				line.append(resultSet.getMetaData().getColumnName(i) + "=");
				if (type == Types.CHAR || type == Types.VARCHAR || type == Types.LONGVARCHAR || type == Types.NCHAR || type == Types.NVARCHAR)
					line.append("\'" + values[i - 1] + "\' AND ");
				else
					line.append(values[i - 1] + " AND ");
			}
			preparedStatement = connect.prepareStatement(line.substring(0, line.length() - 5) + ";");
			preparedStatement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public long delete(Generator generator, RelTupleFilter relTupleFilter) {
		long count = 0;
		TupleIterator iterator = this.iterator();
		ValueTuple tuple;
		List<ValueTuple> tuplesToDelete = new ArrayList<ValueTuple>();
		while (iterator.hasNext()) {
			tuple = iterator.next();
			if (relTupleFilter.filter(tuple))
				tuplesToDelete.add(tuple);
		}
		for (ValueTuple tuples : tuplesToDelete) {
			delete(generator, tuples);
			count++;
		}
		return count;
	}

	@Override
	public long delete(Generator generator, TupleFilter filter) {
		long count = 0;
		TupleIterator iterator = this.iterator();
		ValueTuple tuple;
		List<ValueTuple> tuplesToDelete = new ArrayList<ValueTuple>();
		while (iterator.hasNext()) {
			tuple = iterator.next();
			if (filter.filter(tuple))
				tuplesToDelete.add(tuple);
		}
		for (ValueTuple tuples : tuplesToDelete) {
			delete(generator, tuples);
			count++;
		}
		return count;
	}

	@Override
	public long delete(Context context, ValueRelation tuplesToDelete, boolean errorIfNotIncluded) {
		long count = 0;
		TupleIterator iterator = tuplesToDelete.iterator();
		while (iterator.hasNext()) {
			delete(generator, iterator.next());
			count++;
		}
		return count;
	}

	@Override
	public long update(Generator generator, RelTupleMap relTupleMap) {
		notImplemented("UPDATE");
		return 0;
	}

	@Override
	public long update(Generator generator, RelTupleFilter relTupleFilter, RelTupleMap relTupleMap) {
		notImplemented("UPDATE");
		return 0;
	}

	private TupleIterator dupremoveIterator() throws SQLException {
		return new TupleIteratorUnique(new TupleIterator() {
			String currentLine = null;
			ResultSet resultSet = statement.executeQuery("select * from " + table);

			@Override
			public boolean hasNext() {
				try {
					if (currentLine != null)
						return true;
					if (resultSet.next()) {
						currentLine = getRow(resultSet);
						if (currentLine == null)
							return false;
						return true;
					}
				} catch (SQLException e) {
				}
				return false;
			}

			@Override
			public ValueTuple next() {
				if (hasNext())
					try {
						return toTuple(currentLine);
					} finally {
						currentLine = null;
					}
				else
					return null;
			}

			@Override
			public void close() {
				try {
					if (resultSet != null)
						resultSet.close();
					if (statement != null)
						statement.close();
					if (connect != null)
						connect.close();
				} catch (SQLException e) {
				}
			}
		});
	}

	private TupleIterator dupcountIterator() throws SQLException {
		return new TupleIteratorUnique(new TupleIteratorCount(new TupleIterator() {
			String currentLine = null;
			ResultSet resultSet = statement.executeQuery("select * from " + table);

			@Override
			public boolean hasNext() {
				try {
					if (currentLine != null)
						return true;
					if (resultSet.next()) {
						currentLine = getRow(resultSet);
						if (currentLine == null)
							return false;
						return true;
					}
				} catch (SQLException e) {
				}
				return false;
			}

			@Override
			public ValueTuple next() {
				if (hasNext())
					try {
						return toTuple(currentLine);
					} finally {
						currentLine = null;
					}
				else
					return null;
			}

			@Override
			public void close() {
				try {
					if (resultSet != null)
						resultSet.close();
					if (statement != null)
						statement.close();
					if (connect != null)
						connect.close();
				} catch (SQLException e) {
				}
			}
		}, generator));
	}

	private TupleIterator autokeyIterator() throws SQLException {
		return new TupleIterator() {
			long autokey = 1;
			String currentLine = null;
			ResultSet resultSet = statement.executeQuery("select * from " + table);

			@Override
			public boolean hasNext() {
				if (currentLine != null)
					return true;
				try {
					if (resultSet.next()) {
						currentLine = getRow(resultSet);
						if (currentLine == null)
							return false;
						return true;
					}
				} catch (SQLException e) {
				}
				return false;
			}

			@Override
			public ValueTuple next() {
				if (hasNext())
					try {
						return toTuple(autokey + "," + currentLine);
					} finally {
						currentLine = null;
						autokey++;
					}
				else
					return null;
			}

			@Override
			public void close() {
				try {
					if (resultSet != null)
						resultSet.close();
					if (statement != null)
						statement.close();
					if (connect != null)
						connect.close();
				} catch (Exception e) {
				}
			}
		};
	}
}
