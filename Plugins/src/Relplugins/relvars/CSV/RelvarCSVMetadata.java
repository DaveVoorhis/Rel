package Relplugins.relvars.CSV;

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
import org.reldb.rel.v0.storage.tables.TableExternal.DuplicateHandling;
import org.reldb.rel.v0.types.Heading;
import org.reldb.rel.v0.types.builtin.TypeCharacter;
import org.reldb.rel.v0.types.builtin.TypeInteger;

public class RelvarCSVMetadata extends RelvarCustomMetadata {
	public static final long serialVersionUID = 0;

	private String sourceCode;
	private String path;
	private DuplicateHandling duplicates;

	private static String readFirstLineOfCSV(String path) {
		File f = new File(path);
		if (f.exists()) {
			BufferedReader br = null;
			try {
				String sCurrentLine;
				br = new BufferedReader(new FileReader(f));
				sCurrentLine = br.readLine();
				br.close();
				return sCurrentLine;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		} else
			throw new ExceptionSemantic("EX0001: File not found at: " + path);
	}

	private static RelvarHeading getHeadingFromCSV(String path, DuplicateHandling duplicates) {
		Heading heading = new Heading();
		String firstLine = null;
		if (duplicates == DuplicateHandling.DUP_COUNT)
			heading.add("DUP_COUNT", TypeInteger.getInstance());
		else if (duplicates == DuplicateHandling.AUTOKEY)
			heading.add("AUTO_KEY", TypeInteger.getInstance());
		firstLine = readFirstLineOfCSV(path);
		String[] columns = null;
		try {
			columns = firstLine.toString().split(",");
			for (String column : columns)
				heading.add(column, TypeCharacter.getInstance());
		} catch (NullPointerException e) {
		}

		return new RelvarHeading(heading);
	}

	@Override
	public String getSourceDefinition() {
		return "EXTERNAL CSV " + sourceCode;
	}

	public RelvarCSVMetadata(RelDatabase database, String owner, String path, DuplicateHandling duplicates) {
		super(database, getHeadingFromCSV(path, duplicates), owner);
		this.path = path;
		this.duplicates = duplicates;
		sourceCode = "\" " + path + "\" " + duplicates;
	}

	@Override
	public RelvarGlobal getRelvar(String name, RelDatabase database) {
		File file = new File(path);
		if (!file.exists())
			throw new ExceptionSemantic("EX0002: File at " + path + " not found");
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
