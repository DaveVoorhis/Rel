package org.reldb.rel.v0.storage.relvars.external.accdb;

import java.io.File;
import java.io.IOException;

import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.generator.SelectAttributes;
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

import com.healthmarketscience.jackcess.Column;
import com.healthmarketscience.jackcess.Database;
import com.healthmarketscience.jackcess.DatabaseBuilder;
import com.healthmarketscience.jackcess.Table;

public class RelvarACCDBMetadata extends RelvarCustomMetadata {
	public static final long serialVersionUID = 0;

	private String connectionString;
	private String fileSpec; 		// "c:\\users\\me\\mydb.accdb"
	private String table; 			// "mytable"

	// var myvar external accdb "c:\\users\\me\\mydb.accdb,mytable";
	
	private DuplicateHandling duplicates;
	
	public static RelvarHeading getHeading(RelDatabase database, String spec, DuplicateHandling duplicates) {
		String[] values = CSVLineParse.parseTrimmed(spec);	
		if (values.length != 2)
			throw new ExceptionSemantic("RS0472: Invalid arguments. Expected: FILE, TABLE but got " + spec);
		String fileSpec = values[0];
		String table = values[1];
		Database db = null;
		try {
			db = DatabaseBuilder.open(new File(fileSpec));
			Table tableData = db.getTable(table);
			Heading heading = new Heading();
			if (duplicates == DuplicateHandling.DUP_COUNT)
				heading.add("_DUP_COUNT", TypeInteger.getInstance());
			else if (duplicates == DuplicateHandling.AUTOKEY)
				heading.add("_AUTOKEY", TypeInteger.getInstance());
			for (Column column: tableData.getColumns())
				heading.add(ColumnName.cleanName(column.getName()), TypeCharacter.getInstance());
			RelvarHeading relvarHeading = new RelvarHeading(heading);
			if (duplicates == DuplicateHandling.AUTOKEY) {
				SelectAttributes keyAttribute = new SelectAttributes();
				keyAttribute.add("_AUTOKEY");
				relvarHeading.addKey(keyAttribute);
			}
			return relvarHeading;
		} catch (IOException e) {
			throw new ExceptionSemantic("RS0473: Unable to open " + fileSpec + " table " + table + ": " + e.toString());
		} finally {
			if (db != null)
				try {
					db.close();
				} catch (IOException e) {
				}
		}
	}

	@Override
	public String getSourceDefinition() {
		return "EXTERNAL ACCDB \"" + fileSpec + ", " + table + "\" " + duplicates;
	}

	public RelvarACCDBMetadata(RelDatabase database, String owner, String spec, DuplicateHandling duplicates) {
		super(database, getHeading(database, spec, duplicates), owner);		
		String[] values = CSVLineParse.parseTrimmed(spec);
		fileSpec = values[0];
		table = values[1];
		this.duplicates = duplicates;
		connectionString = spec;
	}

	public void checkTableExistence() {
		Database db = null;
		try {
			db = DatabaseBuilder.open(new File(fileSpec));
			db.getTable(table);
		} catch (IOException e) {
			throw new ExceptionSemantic("RS0474: Table " + table + " no longer exists.");
		} finally {
			if (db != null)
				try {
					db.close();
				} catch (IOException e) {
				}
		}		
	}
	
	@Override
	public RelvarGlobal getRelvar(String name, RelDatabase database) {
		checkTableExistence();
		return new RelvarExternal(name, database, new Generator(database, System.out), this, duplicates);
	}

	@Override
	public void dropRelvar(RelDatabase database) {
	}

	public String getConnectionString() {
		return connectionString;
	}
	
	public String getFileSpec() {
		return fileSpec;
	}

	public String getTable() {
		return table;
	}

	@Override
	public String tableClassName() {
		return "TableACCDB";
	}

	@Override
	public String getType() {
		return "ACCDB";
	}
}
