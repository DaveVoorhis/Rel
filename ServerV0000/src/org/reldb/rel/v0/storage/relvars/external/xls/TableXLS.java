package org.reldb.rel.v0.storage.relvars.external.xls;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

public class TableXLS extends TableCustom {
	private File file;
	private DuplicateHandling duplicates;
	private Generator generator;
	private Heading fileHeading;

	public TableXLS(String Name, RelvarExternalMetadata metadata, Generator generator, DuplicateHandling duplicates) {
		this.generator = generator;
		this.duplicates = duplicates;
		RelvarXLSMetadata meta = (RelvarXLSMetadata) metadata;
		file = new File(meta.getPath());
		RelvarHeading heading = meta.getHeadingDefinition(generator.getDatabase());
		Heading storedHeading = heading.getHeading();
		fileHeading = RelvarXLSMetadata.getHeadingFromXLS(meta.getPath(), duplicates).getHeading();
		if (storedHeading.toString().compareTo(fileHeading.toString()) != 0)
			throw new ExceptionSemantic("RS0464: Stored XLS metadata is " + storedHeading + " but file metadata is " + fileHeading + ". Has the file structure changed?");
	}

	private ValueTuple toTuple(long autokey, Iterator<Cell> cellIterator) {
		Value[] values = new Value[fileHeading.getDegree()];
		int index = 0;		
		if (duplicates == DuplicateHandling.AUTOKEY) {
			values[0] = ValueInteger.select(generator, autokey);
			index = 1;
		}
		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			values[index] = ValueCharacter.select(generator,  cell.getStringCellValue());
			index++;
			if (index >= values.length)
				break;
		}
		for (; index < values.length; index++)
			values[index] = ValueCharacter.select(generator, "");
		return new ValueTuple(generator, values);
	}
	
	private ValueTuple toTuple(Iterator<Cell> cellIterator) {
		Value[] values = new Value[fileHeading.getDegree()];
		int index = 0;
		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			values[index] = ValueCharacter.select(generator,  cell.getStringCellValue());
			index++;
			if (index > values.length)
				break;
		}
		for (; index < values.length; index++)
			values[index] = ValueCharacter.select(generator, "");
		return new ValueTuple(generator, values);
	}

	@Override
	public TupleIterator iterator() {
		try {
			if (file.getName().toLowerCase().endsWith("xls")) {
				if (duplicates == DuplicateHandling.DUP_REMOVE)
					return dupremoveIteratorXLS();
				else if (duplicates == DuplicateHandling.DUP_COUNT)
					return dupcountIteratorXLS();
				else if (duplicates == DuplicateHandling.AUTOKEY)
					return autokeyIteratorXLS();
			} else if (file.getName().toLowerCase().endsWith("xlsx")) {
				if (duplicates == DuplicateHandling.DUP_REMOVE)
					return dupremoveIteratorXLSX();
				else if (duplicates == DuplicateHandling.DUP_COUNT)
					return dupcountIteratorXLSX();
				else if (duplicates == DuplicateHandling.AUTOKEY)
					return autokeyIteratorXLSX();
			} else
				throw new ExceptionSemantic("EX0028: Type should be XLS or XLSX.");
			return null;
		} catch (IOException e) {
			throw new ExceptionSemantic("EX0029: Failed to read the file." + e);
		}
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
		throw new ExceptionSemantic("EX0030: XLS relvars do not yet support " + what);
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
	public void setValue(RelvarExternal relvarXLS, ValueRelation relation) {
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
		notImplemented("INSERT");
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
		notImplemented("DELETE");
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

	private TupleIterator dupremoveIteratorXLS() throws IOException {
		try {
			return new TupleIteratorUnique(new TupleIterator() {
				FileInputStream reader = new FileInputStream(file);
				HSSFWorkbook workbook = new HSSFWorkbook(reader);
				HSSFSheet sheet = workbook.getSheetAt(0);
				Iterator<Row> rowIterator = sheet.iterator();
				Row row = rowIterator.next();

				@Override
				public boolean hasNext() {
					return rowIterator.hasNext();
				}

				@Override
				public ValueTuple next() {
					row = rowIterator.next();
					Iterator<Cell> cellIterator = row.cellIterator();
					return toTuple(cellIterator);
				}

				@Override
				public void close() {
					try {
						reader.close();
					} catch (IOException e) {
					}
				}
			});
		} catch (NoSuchElementException e) {
			return new TupleIterator() {
				@Override
				public boolean hasNext() {
					return false;
				}

				@Override
				public ValueTuple next() {
					return null;
				}

				@Override
				public void close() {

				}
			};
		}
	}

	private TupleIterator dupremoveIteratorXLSX() throws IOException {
		try {
			return new TupleIteratorUnique(new TupleIterator() {
				FileInputStream reader = new FileInputStream(file);
				XSSFWorkbook workbook = new XSSFWorkbook(reader);
				XSSFSheet sheet = workbook.getSheetAt(0);
				Iterator<Row> rowIterator = sheet.iterator();
				Row row = rowIterator.next(); // skip first line

				@Override
				public boolean hasNext() {
					return rowIterator.hasNext();
				}

				@Override
				public ValueTuple next() {
					row = rowIterator.next();
					Iterator<Cell> cellIterator = row.cellIterator();
					return toTuple(cellIterator);
				}

				@Override
				public void close() {
					try {
						reader.close();
					} catch (IOException e) {
					}
				}
			});
		} catch (NoSuchElementException e) {
			return new TupleIterator() {
				@Override
				public boolean hasNext() {
					return false;
				}

				@Override
				public ValueTuple next() {
					return null;
				}

				@Override
				public void close() {

				}
			};
		}
	}

	private TupleIterator dupcountIteratorXLS() throws IOException {
		return new TupleIteratorUnique(new TupleIteratorCount(new TupleIterator() {
			FileInputStream reader = new FileInputStream(file);
			HSSFWorkbook workbook = new HSSFWorkbook(reader);
			HSSFSheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			Row row = rowIterator.next();

			@Override
			public boolean hasNext() {
				return rowIterator.hasNext();
			}

			@Override
			public ValueTuple next() {
				row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				return toTuple(cellIterator);
			}

			@Override
			public void close() {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}, generator));
	}

	private TupleIterator dupcountIteratorXLSX() throws IOException {
		return new TupleIteratorUnique(new TupleIteratorCount(new TupleIterator() {
			FileInputStream reader = new FileInputStream(file);
			XSSFWorkbook workbook = new XSSFWorkbook(reader);
			XSSFSheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			Row row = rowIterator.next();

			@Override
			public boolean hasNext() {
				return rowIterator.hasNext();
			}

			@Override
			public ValueTuple next() {
				row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				return toTuple(cellIterator);
			}

			@Override
			public void close() {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		}, generator));
	}

	private TupleIterator autokeyIteratorXLS() throws IOException {
		return new TupleIterator() {
			long autokey = 1;
			FileInputStream reader = new FileInputStream(file);
			HSSFWorkbook workbook = new HSSFWorkbook(reader);
			HSSFSheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			Row row = rowIterator.next(); // skip first line

			@Override
			public boolean hasNext() {
				return rowIterator.hasNext();
			}

			@Override
			public ValueTuple next() {
				row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				try {
					return toTuple(autokey, cellIterator);
				} finally {
					autokey++;
				}
			}

			@Override
			public void close() {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		};
	}

	private TupleIterator autokeyIteratorXLSX() throws IOException {
		return new TupleIterator() {
			long autokey = 1;
			FileInputStream reader = new FileInputStream(file);
			XSSFWorkbook workbook = new XSSFWorkbook(reader);
			XSSFSheet sheet = workbook.getSheetAt(0);
			Iterator<Row> rowIterator = sheet.iterator();
			Row row = rowIterator.next(); // skip first line

			@Override
			public boolean hasNext() {
				return rowIterator.hasNext();
			}

			@Override
			public ValueTuple next() {
				row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();
				try {
					return toTuple(autokey, cellIterator);
				} finally {
					autokey++;
				}
			}

			@Override
			public void close() {
				try {
					reader.close();
				} catch (IOException e) {
				}
			}
		};
	}
}