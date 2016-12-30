package org.reldb.rel.v0.storage.relvars.external.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.storage.RelDatabase;
import org.reldb.rel.v0.storage.relvars.RelvarExternal;
import org.reldb.rel.v0.storage.relvars.RelvarExternalMetadata;
import org.reldb.rel.v0.storage.relvars.RelvarHeading;
import org.reldb.rel.v0.storage.relvars.external.CSVLineParse;
import org.reldb.rel.v0.storage.tables.TableCustom;
import org.reldb.rel.v0.types.Heading;
import org.reldb.rel.v0.values.RelTupleFilter;
import org.reldb.rel.v0.values.RelTupleMap;
import org.reldb.rel.v0.values.TupleFilter;
import org.reldb.rel.v0.values.TupleIterator;
import org.reldb.rel.v0.values.TupleIteratorAutokey;
import org.reldb.rel.v0.values.Value;
import org.reldb.rel.v0.values.ValueCharacter;
import org.reldb.rel.v0.values.ValueRelation;
import org.reldb.rel.v0.values.ValueTuple;
import org.reldb.rel.v0.vm.Context;

public class TableJDBC extends TableCustom {

	private RelvarJDBCMetadata meta;
	private Generator generator;
	private DuplicateHandling duplicates;
	private Connection connect;
	private Statement statement;
	private Heading fileHeading;

	public TableJDBC(String Name, RelvarExternalMetadata metadata, Generator generator, DuplicateHandling duplicates) {
		meta = (RelvarJDBCMetadata) metadata;
		this.generator = generator;
		this.duplicates = duplicates;
		RelDatabase database = generator.getDatabase();
		RelvarHeading heading = meta.getHeadingDefinition(database);
		Heading storedHeading = heading.getHeading();
		fileHeading = RelvarJDBCMetadata.getHeading(database, meta.getConnectionString(), duplicates).getHeading();
		if (storedHeading.toString().compareTo(fileHeading.toString()) != 0)
			throw new ExceptionSemantic("RS0466: Stored JDBC metadata is " + storedHeading + " but table metadata is " + fileHeading + ". Has the table structure changed?");
		try {
			RelvarJDBCMetadata.loadDrivers(database);
			connect = DriverManager.getConnection(meta.getPath(), meta.getUser(), meta.getPassword());
			statement = connect.createStatement();
		} catch (SQLException e) {
			throw new ExceptionSemantic("EX0021: " + meta.getPath() + "' not found.");
		}
	}

	private String getAttributeList() {
		if (duplicates == DuplicateHandling.DUP_COUNT || duplicates == DuplicateHandling.AUTOKEY)
			return fileHeading.getNameList(1);
		return fileHeading.getNameList();
	}

	@Override
	public TupleIterator iterator() {
		String query = "";
		try {
			switch (duplicates) {
				case DUP_REMOVE:
					query = "SELECT DISTINCT * FROM " + meta.getTable();
					return SQLIterator(query);
				case DUP_COUNT: 
					query = "SELECT COUNT(*) AS DUP_COUNT, " + getAttributeList() + " FROM " + meta.getTable() + " GROUP BY " + getAttributeList();
					return SQLIterator(query);
				case AUTOKEY: 
					query = "SELECT * FROM " + meta.getTable();
					return new TupleIteratorAutokey(SQLIterator(query), generator);
				default: throw new ExceptionSemantic("EX0024: Duplicate handling method " + duplicates.toString() + " not supported by JDBC.");
			}
		} catch (SQLException e) {
			throw new ExceptionSemantic("EX0025: Failed to create iterator due to: " + e + ": in " + query);
		}
	}

	@Override
	public long getCardinality() {
		String query;
		switch (duplicates) {
			case DUP_REMOVE: query = "SELECT DISTINCT COUNT(*) FROM " + meta.getTable(); break;
			case DUP_COUNT: query = "SELECT COUNT(*) FROM (" + getAttributeList() + " FROM " + meta.getTable() + " GROUP BY " + getAttributeList() + ") AS _tmp"; break;
			case AUTOKEY: query = "SELECT COUNT(*) FROM " + meta.getTable(); break;
			default: throw new ExceptionSemantic("RS0470: Duplicate handling method " + duplicates.toString() + " not supported by JDBC.");
		}
		try {
			ResultSet resultSet = statement.executeQuery(query);
			resultSet.next();
			return resultSet.getLong(1);
		} catch (SQLException e) {
			System.out.println("TableJDBC[10]: error " + e);
		}
		return 0;
	}

	@Override
	public TupleIterator iterator(Generator generator) {
		return iterator();
	}

	private static void notImplemented(String what) {
		throw new ExceptionSemantic("EX0026: JDBC relvars do not yet support " + what + ".");
	}

	@Override
	public boolean contains(Generator generator, ValueTuple tuple) {
		TupleIterator iterator = iterator();
		try {
			while (iterator.hasNext())
				if (tuple.equals(iterator.next()))
					return true;
		} finally {
			iterator.close();
		}
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
			StringBuffer command = new StringBuffer("insert into " + meta.getTable() + " values (");
			for (int i = 0; i < values.length; i++)
				command.append("\'" + values[i].toString() + "\',");
			PreparedStatement preparedStatement = connect.prepareStatement(command.substring(0, command.length() - 1) + ");");
			preparedStatement.executeUpdate();
			return 1;
		} catch (SQLException e) {
			System.out.println("TableJDBC[1]: error " + e);
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
			preparedStatement = connect.prepareStatement("delete from " + meta.getTable());
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			System.out.println("TableJDBC[2]: error " + e);
		}
	}

	@Override
	public void delete(Generator generator, ValueTuple tuple) {
		PreparedStatement preparedStatement;
		String[] values = CSVLineParse.parseTrimmed(tuple.toCSV());
		StringBuffer line = new StringBuffer("delete from " + meta.getTable() + " where ");
		try {
			ResultSet resultSet = statement.executeQuery("select * from " + meta.getTable());
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
			System.out.println("TableJDBC[3]: error " + e);
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

	private Value[] getRow(ResultSet resultSet) throws SQLException {
		Value[] values = new Value[resultSet.getMetaData().getColumnCount()];
		for (int i = 1; i <= values.length; i++)
			values[i - 1] = ValueCharacter.select(generator, resultSet.getString(i));
		return values;
	}

	private TupleIterator SQLIterator(String SQLQuery) throws SQLException {
		return new TupleIterator() {
			Value[] currentLine = null;
			ResultSet resultSet = statement.executeQuery(SQLQuery);

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
					System.out.println("TableJDBC[4]: error " + e);
				}
				return false;
			}

			@Override
			public ValueTuple next() {
				if (hasNext())
					try {
						return new ValueTuple(generator, currentLine);
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
					System.out.println("TableJDBC[5]: error " + e);
				}
			}
		};		
	}
}
