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
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.storage.relvars.RelvarExternal;
import org.reldb.rel.v0.storage.relvars.RelvarExternalMetadata;
import org.reldb.rel.v0.storage.relvars.RelvarHeading;
import org.reldb.rel.v0.storage.relvars.external.xls.RelvarXLSMetadata.SheetSpec;
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

public class TableXLS extends TableCustom {
	private File file;
	private DuplicateHandling duplicates;
	private Generator generator;
	private Heading fileHeading;
	private int sheetIndex = 0;
	private boolean hasHeading = true;

	public TableXLS(String Name, RelvarExternalMetadata metadata, Generator generator, DuplicateHandling duplicates) {
		this.generator = generator;
		this.duplicates = duplicates;
		RelvarXLSMetadata meta = (RelvarXLSMetadata) metadata;
		SheetSpec spec = RelvarXLSMetadata.obtainSheetSpec(meta.getPath());
		file = new File(spec.filePath);
		sheetIndex = spec.sheetIndex;
		hasHeading = spec.hasHeading;
		RelvarHeading heading = meta.getHeadingDefinition(generator.getDatabase());
		Heading storedHeading = heading.getHeading();
		fileHeading = RelvarXLSMetadata.getHeadingFromXLS(meta.getPath(), duplicates).getHeading();
		if (storedHeading.toString().compareTo(fileHeading.toString()) != 0)
			throw new ExceptionSemantic("RS0464: Stored XLS metadata is " + storedHeading + " but file metadata is " + fileHeading + ". Has the file structure changed?");
	}
	
	private ValueTuple toTuple(Iterator<Cell> cellIterator) {
		Value[] values = new Value[fileHeading.getDegree() - ((duplicates == DuplicateHandling.DUP_COUNT || duplicates == DuplicateHandling.AUTOKEY) ? 1 : 0)];
		int index = 0;
		DataFormatter formatter = new DataFormatter();
		while (cellIterator.hasNext() && index < values.length) {
			Cell cell = cellIterator.next();
			values[index] = ValueCharacter.select(generator, formatter.formatCellValue(cell));
			index++;
		}
		for (; index < values.length; index++)
			values[index] = ValueCharacter.select(generator, "");
		return new ValueTuple(generator, values);
	}

	@Override
	public TupleIterator iterator() {
		try {
			if (file.getName().toLowerCase().endsWith("xls")) {
				switch (duplicates) {
					case DUP_REMOVE: return new TupleIteratorUnique(iteratorRawXLS());
					case DUP_COUNT: return new TupleIteratorUnique(new TupleIteratorCount(iteratorRawXLS(), generator));
					case AUTOKEY: return new TupleIteratorAutokey(iteratorRawXLS(), generator);
					default: throw new ExceptionSemantic("RS0467: Duplicate handling method " + duplicates.toString() + " is not supported by XLS.");
				}
			} else if (file.getName().toLowerCase().endsWith("xlsx")) {
				switch (duplicates) {
					case DUP_REMOVE: return new TupleIteratorUnique(iteratorRawXLSX());
					case DUP_COUNT: return new TupleIteratorUnique(new TupleIteratorCount(iteratorRawXLSX(), generator));
					case AUTOKEY: return new TupleIteratorAutokey(iteratorRawXLSX(), generator);
					default: throw new ExceptionSemantic("RS0468: Duplicate handling method " + duplicates.toString() + " is not supported by XLS.");
				}
			} else
				throw new ExceptionSemantic("EX0028: Type should be XLS or XLSX.");
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

	private static void notImplemented(String what) {
		throw new ExceptionSemantic("EX0030: XLS relvars do not yet support " + what);
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
	
	private Row obtainFirstRow(Iterator<Row> rowIterator) {
		try {
			return (hasHeading) ? rowIterator.next() : null; // skip heading row?
		} catch (NoSuchElementException nsee) {
			return null;
		}
	}
		
	private abstract class SpreadsheetTupleIterator extends TupleIterator {
		private Iterator<Row> rowIterator;
		private Row row;
		
		public SpreadsheetTupleIterator(Iterator<Row> rowIterator) {
			this.rowIterator = rowIterator;
			row = obtainFirstRow(rowIterator);
		}
		
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
	}
	
	private TupleIterator iteratorRawXLS() throws IOException {
		FileInputStream reader = new FileInputStream(file);
		HSSFWorkbook workbook = new HSSFWorkbook(reader);
		HSSFSheet sheet = workbook.getSheetAt(sheetIndex);
		return new SpreadsheetTupleIterator(sheet.iterator()) {
			@Override
			public void close() {
				try {
					workbook.close();
				} catch (IOException e1) {
				}
				try {
					reader.close();
				} catch (IOException e) {
				}
			}			
		};
	}
	
	private TupleIterator iteratorRawXLSX() throws IOException {
		FileInputStream reader = new FileInputStream(file);
		XSSFWorkbook workbook = new XSSFWorkbook(reader);
		XSSFSheet sheet = workbook.getSheetAt(sheetIndex);
		return new SpreadsheetTupleIterator(sheet.iterator()) {
			@Override
			public void close() {
				try {
					workbook.close();
				} catch (IOException e1) {
				}
				try {
					reader.close();
				} catch (IOException e) {
				}
			}			
		};
	}
}