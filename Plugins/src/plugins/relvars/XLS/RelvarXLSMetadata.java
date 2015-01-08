package plugins.relvars.XLS;

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

public class RelvarXLSMetadata extends RelvarCustomMetadata {
	public static final long serialVersionUID = 0;

	private String sourceCode;
	private String path;
	private DuplicateHandling duplicates;

	private static String readFirstLineOfXLS(String path) {
		File f = new File(path);
		if (f.exists()) {
			FileInputStream reader = null;
			try {
				if (path.toLowerCase().endsWith("xls")) {
					StringBuffer sCurrentLine = new StringBuffer();
					reader = new FileInputStream(f);
					HSSFWorkbook workbook = new HSSFWorkbook(reader);
					HSSFSheet sheet = workbook.getSheetAt(0);
					Iterator<Row> rowIterator = sheet.iterator();
					Row row = rowIterator.next();
					Iterator<Cell> cellIterator = row.cellIterator();
					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						sCurrentLine.append(cell.getStringCellValue() + ",");
					}
					reader.close();
					return sCurrentLine.toString();
				} else if (path.toLowerCase().endsWith("xlsx")) {
					StringBuffer sCurrentLine = new StringBuffer();
					reader = new FileInputStream(f);
					XSSFWorkbook workbook = new XSSFWorkbook(reader);
					XSSFSheet sheet = workbook.getSheetAt(0);
					Iterator<Row> rowIterator = sheet.iterator();
					Row row = rowIterator.next();
					Iterator<Cell> cellIterator = row.cellIterator();
					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						sCurrentLine.append(cell.getStringCellValue() + ",");
					}
					reader.close();
					return sCurrentLine.toString();
				}
			} catch (IOException e) {
			} catch (NoSuchElementException e) {
				return null;
			}
		}
		return null;
	}

	private static RelvarHeading getHeadingFromXLS(String path, DuplicateHandling duplicates) {
		Heading heading = new Heading();
		String firstLine = readFirstLineOfXLS(path);
		String[] columns = null;
		try {
			columns = firstLine.toString().split(",");
		} catch (NullPointerException e) {
		}
		List<Integer> list = getTypesList(path, duplicates);
		if (duplicates == DuplicateHandling.DUP_COUNT)
			heading.add("DUP_COUNT", TypeInteger.getInstance());
		else if (duplicates == DuplicateHandling.AUTOKEY)
			heading.add("AUTO_KEY", TypeInteger.getInstance());

		int index = 0;
		if (columns != null)
			for (int i = 0; i < list.size(); i++) {
				switch (list.get(index)) {
				case Cell.CELL_TYPE_BLANK:
					heading.add(columns[i], TypeCharacter.getInstance());
					break;
				case Cell.CELL_TYPE_BOOLEAN:
					heading.add(columns[i], TypeBoolean.getInstance());
					break;
				case Cell.CELL_TYPE_ERROR:
					heading.add(columns[i], TypeCharacter.getInstance());
					break;
				case Cell.CELL_TYPE_FORMULA:
					heading.add(columns[i], TypeRational.getInstance());
					break;
				case Cell.CELL_TYPE_NUMERIC:
					heading.add(columns[i], TypeRational.getInstance());
					break;
				case Cell.CELL_TYPE_STRING:
					heading.add(columns[i], TypeCharacter.getInstance());
					break;
				}
				index++;
			}
		return new RelvarHeading(heading);
	}

	@Override
	public String getSourceDefinition() {
		return "EXTERNAL XLS " + sourceCode;
	}

	public RelvarXLSMetadata(RelDatabase database, String owner, String path, DuplicateHandling duplicates) {
		super(database, getHeadingFromXLS(path, duplicates), owner);
		this.path = path;
		this.duplicates = duplicates;
		sourceCode = "\" " + path + "\" " + duplicates;
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

	public List<Integer> getTypesList() {
		return getTypesList(path, duplicates);
	}

	private static List<Integer> getTypesList(String path, DuplicateHandling duplicates) {
		File f = new File(path);
		if (f.exists()) {
			FileInputStream reader = null;
			try {
				if (path.toLowerCase().endsWith("xls")) {
					reader = new FileInputStream(f);
					HSSFWorkbook workbook = new HSSFWorkbook(reader);
					HSSFSheet sheet = workbook.getSheetAt(0);
					Iterator<Row> rowIterator = sheet.iterator();
					Row row = rowIterator.next();
					row = rowIterator.next();
					Iterator<Cell> cellIterator = row.cellIterator();
					List<Integer> list = new ArrayList<Integer>();
					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						list.add(cell.getCellType());
					}
					reader.close();
					return list;
				} else if (path.toLowerCase().endsWith("xlsx")) {
					reader = new FileInputStream(f);
					XSSFWorkbook workbook = new XSSFWorkbook(reader);
					XSSFSheet sheet = workbook.getSheetAt(0);
					Iterator<Row> rowIterator = sheet.iterator();
					Row row = rowIterator.next();
					row = rowIterator.next();
					Iterator<Cell> cellIterator = row.cellIterator();
					List<Integer> list = new ArrayList<Integer>();
					while (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();
						list.add(cell.getCellType());
					}
					reader.close();
					return list;
				}
			} catch (IOException e) {
			} catch (NoSuchElementException e) {
				return null;
			}
		}
		return null;
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
