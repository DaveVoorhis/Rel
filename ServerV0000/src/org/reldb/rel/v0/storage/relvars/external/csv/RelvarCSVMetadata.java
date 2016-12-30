package org.reldb.rel.v0.storage.relvars.external.csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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

public class RelvarCSVMetadata extends RelvarCustomMetadata {
	public static final long serialVersionUID = 0;

	private String path;
	private DuplicateHandling duplicates;

	public static class CSVSpec {
		String filePath;
		boolean hasHeading = true;
	}
	
	public static CSVSpec obtainCSVSpec(String path) {
		String[] splitPath = CSVLineParse.parseTrimmed(path);
		CSVSpec spec = new CSVSpec();
		spec.filePath = splitPath[0].trim();
		for (int index = 1; index < splitPath.length; index++) {
			String part = splitPath[index].trim();
			if (part.compareToIgnoreCase("NOHEADING") == 0)
				spec.hasHeading = false;
			else
				throw new ExceptionSemantic("RS0469: Invalid CSV file specification '" + part + "'.");
		}
		return spec;
	}

	private static String readFirstLineOfCSV(String path) {
		File f = new File(path);
		if (f.exists()) {
			BufferedReader br = null;
			try {
				String sCurrentLine;
				br = new BufferedReader(new FileReader(f));
				sCurrentLine = br.readLine();
				br.close();
				// replaceAll to filter out Byte Order Mark (BOM), if present
				return (sCurrentLine != null) ? sCurrentLine.replaceAll("\ufeff", " ") : null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		} else
			throw new ExceptionSemantic("EX0001: File not found at: " + path);
	}
	
	public static RelvarHeading getHeadingFromCSV(String path, DuplicateHandling duplicates) {
		CSVSpec spec = obtainCSVSpec(path);
		Heading heading = new Heading();
		String firstLine = null;
		if (duplicates == DuplicateHandling.DUP_COUNT)
			heading.add("_DUP_COUNT", TypeInteger.getInstance());
		else if (duplicates == DuplicateHandling.AUTOKEY)
			heading.add("_AUTOKEY", TypeInteger.getInstance());
		firstLine = readFirstLineOfCSV(spec.filePath);
		String[] columns = null;
		if (firstLine != null) {
	        columns = CSVLineParse.parse(firstLine);
	        int blankCount = 0;
	        if (spec.hasHeading)
				for (String column: columns) {
					String columnName = ColumnName.cleanName(column);
					if (columnName.length() == 0)
						columnName = "BLANK" + ++blankCount;
					heading.add(columnName, TypeCharacter.getInstance());
				}
	        else
	        	for (int i=0; i<columns.length; i++)
	        		heading.add("COLUMN" + ++blankCount, TypeCharacter.getInstance());
		}
		return new RelvarHeading(heading);
	}

	public RelvarCSVMetadata(RelDatabase database, String owner, String path, DuplicateHandling duplicates) {
		super(database, getHeadingFromCSV(path, duplicates), owner);
		this.path = path;
		this.duplicates = duplicates;
	}

	@Override
	public String getSourceDefinition() {
		return "EXTERNAL CSV \"" + path + "\" " + duplicates;
	}

	@Override
	public RelvarGlobal getRelvar(String name, RelDatabase database) {
		CSVSpec spec = obtainCSVSpec(path);
		File file = new File(spec.filePath);
		if (!file.exists())
			throw new ExceptionSemantic("EX0002: File at " + path + " not found.");
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
		return "TableCSV";
	}

	@Override
	public String getType() {
		return "CSV";
	}
}
