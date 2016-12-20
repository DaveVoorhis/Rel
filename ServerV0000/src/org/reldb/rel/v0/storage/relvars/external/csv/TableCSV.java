package org.reldb.rel.v0.storage.relvars.external.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.storage.relvars.RelvarExternal;
import org.reldb.rel.v0.storage.relvars.RelvarExternalMetadata;
import org.reldb.rel.v0.storage.relvars.RelvarHeading;
import org.reldb.rel.v0.storage.tables.TableCustom;
import org.reldb.rel.v0.types.Heading;
import org.reldb.rel.v0.values.RelTupleFilter;
import org.reldb.rel.v0.values.RelTupleMap;
import org.reldb.rel.v0.values.TupleFilter;
import org.reldb.rel.v0.values.TupleIterator;
import org.reldb.rel.v0.values.TupleIteratorCount;
import org.reldb.rel.v0.values.TupleIteratorUnique;
import org.reldb.rel.v0.values.Value;
import org.reldb.rel.v0.values.ValueCharacter;
import org.reldb.rel.v0.values.ValueInteger;
import org.reldb.rel.v0.values.ValueRelation;
import org.reldb.rel.v0.values.ValueTuple;
import org.reldb.rel.v0.vm.Context;

public class TableCSV extends TableCustom {
	private File file;
	private DuplicateHandling duplicates;
	private Generator generator;
	private Heading fileHeading;

	public TableCSV(String Name, RelvarExternalMetadata metadata, Generator generator, DuplicateHandling duplicates) {
		this.generator = generator;
		this.duplicates = duplicates;
		RelvarCSVMetadata meta = (RelvarCSVMetadata) metadata;
		file = new File(meta.getPath());
		RelvarHeading heading = meta.getHeadingDefinition(generator.getDatabase());
		Heading storedHeading = heading.getHeading();
		fileHeading = RelvarCSVMetadata.getHeadingFromCSV(meta.getPath(), duplicates).getHeading();
		if (storedHeading.toString().compareTo(fileHeading.toString()) != 0)
			throw new ExceptionSemantic("RS0453: Stored CSV metadata is " + storedHeading + " but file metadata is " + fileHeading + ". Has the file structure changed?");
	}

	private ValueTuple toTuple(String line) {
		String[] rawValues = CSVLineParse.parse(line);
		Value[] values = new Value[rawValues.length];
		int startAt = 0;
		if (duplicates == DuplicateHandling.AUTOKEY) {
			values[0] = ValueInteger.select(generator, Integer.parseInt(rawValues[0]));
			startAt = 1;
		}
		if (values.length != fileHeading.getDegree() - ((duplicates == DuplicateHandling.DUP_COUNT) ? 1 : 0))
			throw new ExceptionSemantic("RS0457: CSV file " + file.getAbsolutePath() + " with heading " + fileHeading + " has malformed line: " + line);
		for (int i = startAt; i < rawValues.length; i++) {
			String rawValue = rawValues[i].trim();
			if (rawValue.startsWith("\"") && rawValue.endsWith("\""))
				values[i] = ValueCharacter.select(generator, ValueCharacter.stripDelimitedString(rawValue));
			else
				values[i] = ValueCharacter.select(generator, rawValue);
		}
		return new ValueTuple(generator, values);
	}

	@Override
	public TupleIterator iterator() {
		if (duplicates == DuplicateHandling.DUP_REMOVE)
			return dupremoveIterator();
		else if (duplicates == DuplicateHandling.DUP_COUNT)
			return dupcountIterator();
		else if (duplicates == DuplicateHandling.AUTOKEY)
			return autokeyIterator();
		else
			throw new ExceptionSemantic("EX0003: Duplicate handling method " + duplicates.toString() + " is not supported by CSV.");
	}

	@Override
	public TupleIterator iterator(Generator generator) {
		return iterator();
	}

	@Override
	public long getCardinality() {
		long count = 0;
		TupleIterator iterator = iterator();
		while (iterator.hasNext()) {
			count++;
			iterator.next();
		}
		return count;
	}

	private static void notImplemented(String what) {
		throw new ExceptionSemantic("EX0004: CSV relvars do not yet support " + what + ".");
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
	public void setValue(RelvarExternal relvarCSV, ValueRelation relation) {
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
			FileWriter fw = new FileWriter(file.getAbsolutePath(), true);
			fw.write("\n" + tuple.toCSV());
			fw.close();
			return 1;
		} catch (IOException e) {
		}
		return 0;
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
		BufferedReader br = null;
		try {
			String heading;
			br = new BufferedReader(new FileReader(file));
			heading = br.readLine();
			br.close();
			FileWriter fw = new FileWriter(file.getAbsolutePath());
			fw.write(heading);
			fw.close();
		} catch (IOException e) {
		}
	}

	@Override
	public void delete(Generator generator, ValueTuple tuple) {
		notImplemented("DELETE");
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

	private TupleIterator dupremoveIterator() {
		return new TupleIteratorUnique(new TupleIterator() {
			String currentLine = null;
			BufferedReader reader = null;

			@Override
			public boolean hasNext() {
				if (currentLine != null)
					return true;
				if (reader == null)
					try {
						reader = new BufferedReader(new FileReader(file));
						reader.readLine(); // skip first line
					} catch (IOException ioe) {
						throw new ExceptionSemantic("EX0005: Unable to read CSV file '" + file.getPath() + "': " + ioe);
					}
				try {
					currentLine = reader.readLine();
					if (currentLine == null)
						return false;
					return true;
				} catch (IOException e) {
					throw new ExceptionSemantic("EX0006: Unable to read CSV file '" + file.getPath() + "': " + e);
				}
			}

			@Override
			public ValueTuple next() {
				if (hasNext())
					try {
						// replaceAll to filter out Byte Order Mark (BOM), if present
						return toTuple(currentLine.replaceAll("\ufeff", " "));
					} finally {
						currentLine = null;
					}
				else
					return null;
			}

			@Override
			public void close() {
				if (reader != null)
					try {
						reader.close();
					} catch (IOException ioe2) {
					}
			}
		});
	}

	private TupleIterator dupcountIterator() {
		return new TupleIteratorUnique(new TupleIteratorCount(new TupleIterator() {
			String currentLine = null;
			BufferedReader reader = null;

			@Override
			public boolean hasNext() {
				if (currentLine != null)
					return true;
				if (reader == null)
					try {
						reader = new BufferedReader(new FileReader(file));
						reader.readLine(); // skip first line
					} catch (IOException ioe) {
						throw new ExceptionSemantic("EX0007: Unable to read CSV file '" + file.getPath() + "': " + ioe);
					}
				try {
					currentLine = reader.readLine();
					if (currentLine == null)
						return false;
					return true;
				} catch (IOException e) {
					throw new ExceptionSemantic("EX0008: Unable to read CSV file '" + file.getPath() + "': " + e);
				}
			}

			@Override
			public ValueTuple next() {
				if (hasNext())
					try {
						// replaceAll to filter out Byte Order Mark (BOM), if present
						return toTuple(currentLine.replaceAll("\ufeff", " "));
					} finally {
						currentLine = null;
					}
				else
					return null;
			}

			@Override
			public void close() {
				if (reader != null)
					try {
						reader.close();
					} catch (IOException ioe2) {
					}
			}
		}, generator));
	}

	private TupleIterator autokeyIterator() {
		return new TupleIterator() {
			long autokey = 1;
			String currentLine = null;
			BufferedReader reader = null;

			@Override
			public boolean hasNext() {
				if (currentLine != null)
					return true;
				if (reader == null)
					try {
						reader = new BufferedReader(new FileReader(file));
						reader.readLine(); // skip first line
					} catch (IOException ioe) {
						throw new ExceptionSemantic("EX0009: Unable to read CSV file '" + file.getPath() + "': " + ioe);
					}
				try {
					currentLine = reader.readLine();
					if (currentLine == null)
						return false;
					return true;
				} catch (IOException e) {
					throw new ExceptionSemantic("EX0010: Unable to read CSV file '" + file.getPath() + "': " + e);
				}
			}

			@Override
			public ValueTuple next() {
				if (hasNext())
					try {
						// replaceAll to filter out Byte Order Mark (BOM), if present
						return toTuple(autokey + "," + currentLine.replaceAll("\ufeff", " "));
					} finally {
						autokey++;
						currentLine = null;
					}
				else
					return null;
			}

			@Override
			public void close() {
				if (reader != null)
					try {
						reader.close();
					} catch (IOException ioe2) {
					}
			}
		};
	}
}
