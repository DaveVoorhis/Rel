package org.reldb.rel.v0.storage.relvars.external.xls;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.v0.generator.Generator;
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

public class RelvarXLSMetadata extends RelvarCustomMetadata {
	public static final long serialVersionUID = 0;

	private String path;
	private DuplicateHandling duplicates;

	public static class SheetSpec {
		String filePath;
		int sheetIndex = 0;
		boolean hasHeading = true;
	}
	
	public static SheetSpec obtainSheetSpec(String path) {
		String[] splitPath = CSVLineParse.parseTrimmed(path);
		SheetSpec spec = new SheetSpec();
		spec.filePath = splitPath[0].trim();
		for (int index = 1; index < splitPath.length; index++) {
			String part = splitPath[index].trim();
			if (part.compareToIgnoreCase("NOHEADING") == 0)
				spec.hasHeading = false;
			else
				try {
					spec.sheetIndex = Integer.parseInt(part);
				} catch (NumberFormatException nfe) {
					throw new ExceptionSemantic("RS0465: Invalid spreadsheet specification '" + part + "'.");
				}
		}
		return spec;
	}
	
	private static RelvarHeading buildHeadingFromColumnsInFirstRow(DuplicateHandling duplicates, boolean hasHeadingRow, Iterator<Row> rowIterator) {
		Heading heading = new Heading();		
		if (duplicates == DuplicateHandling.DUP_COUNT)
			heading.add("_DUP_COUNT", TypeInteger.getInstance());
		else if (duplicates == DuplicateHandling.AUTOKEY)
			heading.add("_AUTOKEY", TypeInteger.getInstance());		
		Row row;
		try {
			row = rowIterator.next();
		} catch (NoSuchElementException nsee) {
			return new RelvarHeading(heading);
		}
		Iterator<Cell> cellIterator = row.cellIterator();
        int blankCount = 0;
		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			String columnName;
			if (hasHeadingRow) {
				columnName = ColumnName.cleanName(cell.toString());
				if (columnName.length() == 0)
					columnName = "BLANK" + ++blankCount;
			} else
				columnName = "COLUMN" + ++blankCount;
			heading.add(ColumnName.cleanName(columnName), TypeCharacter.getInstance());
		}		
		return new RelvarHeading(heading);
	}
	
	public static RelvarHeading getHeadingFromXLS(String path, DuplicateHandling duplicates) {
		SheetSpec spec = obtainSheetSpec(path);
		File f = new File(spec.filePath);
		if (!f.exists())
			throw new ExceptionSemantic("RS0461: File " + path + " doesn't exist.");
		try (FileInputStream reader = new FileInputStream(f)) {
			if (spec.filePath.toLowerCase().endsWith("xls")) {
				try (HSSFWorkbook workbook = new HSSFWorkbook(reader)) {
					return buildHeadingFromColumnsInFirstRow(duplicates, spec.hasHeading, workbook.getSheetAt(spec.sheetIndex).iterator());
				}
			} else if (spec.filePath.toLowerCase().endsWith("xlsx")) {
				try (XSSFWorkbook workbook = new XSSFWorkbook(reader)) {
					return buildHeadingFromColumnsInFirstRow(duplicates, spec.hasHeading, workbook.getSheetAt(spec.sheetIndex).iterator());
				}
			} else {
				throw new ExceptionSemantic("RS0462: Unrecognised file type. It should be .XLS or .XLSX.");
			}
		} catch (IOException ioe) {
			throw new ExceptionSemantic("RS0463: Unable to read " + path + " due to " + ioe);
		}
	}

	public RelvarXLSMetadata(RelDatabase database, String owner, String path, DuplicateHandling duplicates) {
		super(database, getHeadingFromXLS(path, duplicates), owner);
		this.path = path;
		this.duplicates = duplicates;
	}

	@Override
	public String getSourceDefinition() {
		return "EXTERNAL XLS \"" + path + "\" " + duplicates;
	}

	@Override
	public RelvarGlobal getRelvar(String name, RelDatabase database) {
		SheetSpec spec = obtainSheetSpec(path);
		File file = new File(spec.filePath);
		if (!file.exists())
			throw new ExceptionSemantic("EX0027: File at " + path + " not found");
		return new RelvarExternal(name, database, new Generator(database, System.out), this, duplicates);
	}

	@Override
	public void dropRelvar(RelDatabase database) {
	}

	public String getPath() {
		return path;
	}

	@Override
	public String tableClassName() {
		return "TableXLS";
	}

	@Override
	public String getType() {
		return "XLS";
	}
}
