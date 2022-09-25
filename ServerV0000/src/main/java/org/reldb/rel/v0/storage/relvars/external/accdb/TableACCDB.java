package org.reldb.rel.v0.storage.relvars.external.accdb;

import java.io.File;
import java.io.IOException;

import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.storage.RelDatabase;
import org.reldb.rel.v0.storage.relvars.RelvarExternal;
import org.reldb.rel.v0.storage.relvars.RelvarExternalMetadata;
import org.reldb.rel.v0.storage.relvars.RelvarHeading;
import org.reldb.rel.v0.storage.tables.TableCustom;
import org.reldb.rel.v0.types.Heading;
import org.reldb.rel.v0.values.RelTupleFilter;
import org.reldb.rel.v0.values.RelTupleMap;
import org.reldb.rel.v0.values.TupleFilter;
import org.reldb.rel.v0.values.TupleIterator;
import org.reldb.rel.v0.values.TupleIteratorAutokey;
import org.reldb.rel.v0.values.TupleIteratorCount;
import org.reldb.rel.v0.values.TupleIteratorUnique;
import org.reldb.rel.v0.values.Value;
import org.reldb.rel.v0.values.ValueCharacter;
import org.reldb.rel.v0.values.ValueRelation;
import org.reldb.rel.v0.values.ValueTuple;
import org.reldb.rel.v0.vm.Context;

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Cursor;
import com.healthmarketscience.jackcess.CursorBuilder;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Row;
import com.healthmarketscience.jackcess.Table;

public class TableACCDB extends TableCustom {
	private RelvarACCDBMetadata meta;
	private Generator generator;
	private DuplicateHandling duplicates;
	private Heading fileHeading;

	public TableACCDB(String Name, RelvarExternalMetadata metadata, Generator generator, DuplicateHandling duplicates) {
		meta = (RelvarACCDBMetadata) metadata;
		this.generator = generator;
		this.duplicates = duplicates;
		RelDatabase database = generator.getDatabase();
		RelvarHeading heading = meta.getHeadingDefinition(database);
		Heading storedHeading = heading.getHeading();
		fileHeading = RelvarACCDBMetadata.getHeading(database, meta.getConnectionString(), duplicates).getHeading();
		if (storedHeading.toString().compareTo(fileHeading.toString()) != 0)
			throw new ExceptionSemantic("RS0476: Stored ACCDB metadata is " + storedHeading + " but table metadata is " + fileHeading + ". Has the table structure changed?");
		meta.checkTableExistence();
	}

	@Override
	public TupleIterator iterator() {
		try {
			switch (duplicates) {
				case DUP_REMOVE: return new TupleIteratorUnique(iteratorRaw());
				case DUP_COUNT: return new TupleIteratorUnique(new TupleIteratorCount(iteratorRaw(), generator));
				case AUTOKEY: return new TupleIteratorAutokey(iteratorRaw(), generator);
				default: throw new ExceptionSemantic("RS0477: Duplicate handling method " + duplicates.toString() + " not supported by ACCDB.");
			}
		} catch (IOException ioe) {
			throw new ExceptionSemantic("RS0478: Error accessing table " + meta.getTable() + " in " + meta.getFileSpec() + " due to: " + ioe);
		}
	}

	@Override
	public long getCardinality() {
		long count = 0;
		TupleIterator iterator = iterator();
		try {
			while (iterator.hasNext()) {
				count++;
				iterator.next();
			}
		} finally {
			iterator.close();
		}
		return count;
	}

	@Override
	public TupleIterator iterator(Generator generator) {
		return iterator();
	}

	private static void notImplemented(String what) {
		throw new ExceptionSemantic("RS0475: ACCDB relvars do not yet support " + what + ".");
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
		notImplemented("INSERT");
		return 0;
	}

	@Override
	public long insert(Generator generator, ValueTuple tuple) {
		notImplemented("INSERT");
		return 0;
	}

	@Override
	public long insertNoDuplicates(Generator generator, ValueRelation relation) {
		notImplemented("INSERT");
		return 0;
	}

	@Override
	public void purge() {
		notImplemented("DELETE");
	}

	@Override
	public void delete(Generator generator, ValueTuple tuple) {
		notImplemented("DELETE");
	}

	@Override
	public long delete(Generator generator, RelTupleFilter relTupleFilter) {
		notImplemented("DELETE");
		return 0;
	}

	@Override
	public long delete(Generator generator, TupleFilter filter) {
		notImplemented("DELETE");
		return 0;
	}

	@Override
	public long delete(Context context, ValueRelation tuplesToDelete, boolean errorIfNotIncluded) {
		notImplemented("DELETE");
		return 0;
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

	private ValueTuple toTuple(Table table, Row row) {
		Value[] rowData = new Value[table.getColumnCount()];
		int columnIndex = 0;
		for (Column column: table.getColumns()) {
			String columnName = column.getName();
		    Object value = row.get(columnName);
		    rowData[columnIndex++] = ValueCharacter.select(generator, (value == null) ? "" : value.toString());
		}
		return new ValueTuple(generator, rowData);
	}
	
	private TupleIterator iteratorRaw() throws IOException {
		return new TupleIterator() {
			Database db = DatabaseBuilder.open(new File(meta.getFileSpec()));
			Table table = db.getTable(meta.getTable());
			Cursor cursor = CursorBuilder.createCursor(table);
			Row currentLine = null;
			
			@Override
			public boolean hasNext() {
				if (currentLine != null)
					return true;
				try {
					if (cursor.moveToNextRow()) {
						currentLine = cursor.getCurrentRow();
						if (currentLine == null)
							return false;
						return true;
					}
				} catch (IOException ioe) {
					System.out.println("TableACCDB[1]: error " + ioe);					
				}
				return false;
			}

			@Override
			public ValueTuple next() {
				if (hasNext())
					try {
						return toTuple(table, currentLine);
					} finally {
						currentLine = null;
					}
				else
					return null;
			}

			@Override
			public void close() {
				try {
					db.close();
				} catch (IOException e) {
					System.out.println("TableACCDB[2]: error " + e);
				}
			}
		};		
	}
}
