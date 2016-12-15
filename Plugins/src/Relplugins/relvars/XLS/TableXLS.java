package Relplugins.relvars.XLS;

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
import org.reldb.rel.v0.storage.tables.TableCustom;
import org.reldb.rel.v0.values.RelTupleFilter;
import org.reldb.rel.v0.values.RelTupleMap;
import org.reldb.rel.v0.values.TupleFilter;
import org.reldb.rel.v0.values.TupleIterator;
import org.reldb.rel.v0.values.TupleIteratorCount;
import org.reldb.rel.v0.values.TupleIteratorUnique;
import org.reldb.rel.v0.values.Value;
import org.reldb.rel.v0.values.ValueBoolean;
import org.reldb.rel.v0.values.ValueCharacter;
import org.reldb.rel.v0.values.ValueInteger;
import org.reldb.rel.v0.values.ValueRational;
import org.reldb.rel.v0.values.ValueRelation;
import org.reldb.rel.v0.values.ValueTuple;
import org.reldb.rel.v0.vm.Context;

public class TableXLS extends TableCustom {

	private File file;
	private DuplicateHandling duplicates;
	private Generator generator;
	private List<Integer> typeList;

	public TableXLS(String Name, RelvarExternalMetadata metadata, Generator generator, DuplicateHandling duplicates) {
		this.generator = generator;
		this.duplicates = duplicates;
		RelvarXLSMetadata meta = (RelvarXLSMetadata) metadata;
		typeList = meta.getTypesList();
		file = new File(meta.getPath());
	}

	private ValueTuple toTuple(String line) {
		String[] rawValues = line.split(",");
		Value[] values = new Value[rawValues.length];
		int startAt = 0;
		if (duplicates == DuplicateHandling.AUTOKEY) {
			values[0] = ValueInteger.select(generator, Integer.parseInt(rawValues[0]));
			startAt = 1;
		}

		int index = startAt;
		for (Integer type : typeList) {
			switch (type) {
			case Cell.CELL_TYPE_BLANK:
				values[index] = ValueCharacter.select(generator, "BLANK");
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				values[index] = ValueBoolean.select(generator, Boolean.parseBoolean(rawValues[index]));
				break;
			case Cell.CELL_TYPE_ERROR:
				values[index] = ValueCharacter.select(generator, "ERROR");
				break;
			case Cell.CELL_TYPE_FORMULA:
				values[index] = ValueRational.select(generator, Float.parseFloat(rawValues[index]));
				break;
			case Cell.CELL_TYPE_NUMERIC:
				values[index] = ValueRational.select(generator, Float.parseFloat(rawValues[index]));
				break;
			case Cell.CELL_TYPE_STRING:
				values[index] = ValueCharacter.select(generator, rawValues[index]);
				break;
			}
			index++;
		}
		return new ValueTuple(generator, values);
	}

	private String readLine(Iterator<Cell> cellIterator) {
		StringBuffer currentLine = new StringBuffer();
		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			switch (cell.getCellType()) {
			case Cell.CELL_TYPE_BLANK:
				break;
			case Cell.CELL_TYPE_BOOLEAN:
				currentLine.append(cell.getBooleanCellValue() + ",");
				break;
			case Cell.CELL_TYPE_ERROR:
				break;
			case Cell.CELL_TYPE_FORMULA:
				currentLine.append(cell.getNumericCellValue() + ",");
				break;
			case Cell.CELL_TYPE_NUMERIC:
				currentLine.append(cell.getNumericCellValue() + ",");
				break;
			case Cell.CELL_TYPE_STRING:
				currentLine.append(cell.getStringCellValue() + ",");
				break;
			}
		}
		return currentLine.substring(0, currentLine.length() - 1).toString();
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
					return toTuple(readLine(cellIterator));
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
					return toTuple(readLine(cellIterator));
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
				return toTuple(readLine(cellIterator));
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
				return toTuple(readLine(cellIterator));
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
					return toTuple(autokey + "," + readLine(cellIterator));
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
					return toTuple(autokey + "," + readLine(cellIterator));
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