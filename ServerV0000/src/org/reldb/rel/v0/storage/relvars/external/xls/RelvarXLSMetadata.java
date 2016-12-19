package org.reldb.rel.v0.storage.relvars.external.xls;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.storage.RelDatabase;
import org.reldb.rel.v0.storage.relvars.RelvarCustomMetadata;
import org.reldb.rel.v0.storage.relvars.RelvarExternal;
import org.reldb.rel.v0.storage.relvars.RelvarGlobal;
import org.reldb.rel.v0.storage.relvars.RelvarHeading;
import org.reldb.rel.v0.storage.relvars.external.csv.ColumnName;
import org.reldb.rel.v0.storage.tables.TableExternal.DuplicateHandling;
import org.reldb.rel.v0.types.Heading;
import org.reldb.rel.v0.types.builtin.TypeCharacter;
import org.reldb.rel.v0.types.builtin.TypeInteger;

public class RelvarXLSMetadata extends RelvarCustomMetadata {
	public static final long serialVersionUID = 0;

	private String path;
	private DuplicateHandling duplicates;

	public static RelvarHeading getHeadingFromXLS(String path, DuplicateHandling duplicates) {
		Heading heading = new Heading();
		
		if (duplicates == DuplicateHandling.DUP_COUNT)
			heading.add("DUP_COUNT", TypeInteger.getInstance());
		else if (duplicates == DuplicateHandling.AUTOKEY)
			heading.add("AUTO_KEY", TypeInteger.getInstance());
		
		File f = new File(path);
		if (!f.exists())
			throw new ExceptionSemantic("RS0461: File " + path + " doesn't exist.");
		FileInputStream reader = null;
		Iterator<Row> rowIterator;
		try {
			reader = new FileInputStream(f);
			if (path.toLowerCase().endsWith("xls")) {
				HSSFWorkbook workbook = new HSSFWorkbook(reader);
				HSSFSheet sheet = workbook.getSheetAt(0);
				rowIterator = sheet.iterator();
			} else if (path.toLowerCase().endsWith("xlsx")) {
				XSSFWorkbook workbook = new XSSFWorkbook(reader);
				XSSFSheet sheet = workbook.getSheetAt(0);
				rowIterator = sheet.iterator();
			} else {
				try {
					reader.close();
				} catch (IOException e) {}
				throw new ExceptionSemantic("RS0462: Unrecognised file type. It should be .XLS or .XLSX.");
			}
		} catch (IOException ioe) {
			throw new ExceptionSemantic("RS0463: Unable to read " + path + " due to " + ioe);
		}
		Row row = rowIterator.next();
		Iterator<Cell> cellIterator = row.cellIterator();
        int blankCount = 0;
		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();			
			String columnName = ColumnName.cleanName(cell.getStringCellValue());
			if (columnName.length() == 0)
				columnName = "BLANK" + ++blankCount;
			heading.add(ColumnName.cleanName(columnName), TypeCharacter.getInstance());
		}
		try {
			reader.close();
		} catch (IOException e) {}
		
		return new RelvarHeading(heading);
	}

	public RelvarXLSMetadata(RelDatabase database, String owner, String path, DuplicateHandling duplicates) {
		super(database, getHeadingFromXLS(path, duplicates), owner);
		this.path = path;
		this.duplicates = duplicates;
	}

	@Override
	public String getSourceDefinition() {
		return "EXTERNAL XLS " + "\"" + path + "\" " + duplicates;
	}

	@Override
	public RelvarGlobal getRelvar(String name, RelDatabase database) {
		File file = new File(path);
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
