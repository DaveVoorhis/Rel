package plugins.relvars.Hadoop;

import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.client.HBaseAdmin;

import ca.mb.armchair.rel3.exceptions.ExceptionSemantic;
import ca.mb.armchair.rel3.generator.Generator;
import ca.mb.armchair.rel3.storage.RelDatabase;
import ca.mb.armchair.rel3.storage.relvars.RelvarCustomMetadata;
import ca.mb.armchair.rel3.storage.relvars.RelvarExternal;
import ca.mb.armchair.rel3.storage.relvars.RelvarGlobal;
import ca.mb.armchair.rel3.storage.relvars.RelvarHeading;
import ca.mb.armchair.rel3.storage.tables.TableExternal.DuplicateHandling;
import ca.mb.armchair.rel3.types.Heading;
import ca.mb.armchair.rel3.types.builtin.TypeCharacter;
import ca.mb.armchair.rel3.types.builtin.TypeInteger;

public class RelvarHADOOPMetadata extends RelvarCustomMetadata {
	private static final long serialVersionUID = 0;

	private String sourceCode;
	private DuplicateHandling duplicates;
	private String tableName;
	private String[] columnNames;

	private static RelvarHeading getHeading(String spec, DuplicateHandling duplicates) {
		Heading heading = new Heading();
		String firstLine = null;
		String tableName = spec;

		if (duplicates == DuplicateHandling.DUP_COUNT)
			heading.add("DUP_COUNT", TypeInteger.getInstance());
		else if (duplicates == DuplicateHandling.AUTOKEY)
			heading.add("AUTO_KEY", TypeInteger.getInstance());
		heading.add("ROW_NAME", TypeCharacter.getInstance());
		firstLine = getColumnNames(tableName);
		String[] columns = null;
		try {
			columns = firstLine.toString().split(",");
			for (String column : columns)
				heading.add(column, TypeCharacter.getInstance());
		} catch (NullPointerException e) {
		}
		return new RelvarHeading(heading);
	}

	@SuppressWarnings("rawtypes")
	private static String getColumnNames(String tableName) {
		try {
			Configuration config = HBaseConfiguration.create();
			HBaseAdmin admin = new HBaseAdmin(config);
			StringBuffer result = new StringBuffer();
			Iterator iterator = admin.getTableDescriptor(tableName.getBytes()).getFamilies().iterator();
			while (iterator.hasNext())
				result.append(((HColumnDescriptor) iterator.next()).getNameAsString() + ",");
			result.deleteCharAt(result.length() - 1);
			admin.close();
			return result.toString();
		} catch (Exception e) {
			throw new ExceptionSemantic("EX0011: Error reading table: " + e.toString());
		}
	}

	public RelvarHADOOPMetadata(RelDatabase database, String owner, String spec, DuplicateHandling duplicates) {
		super(database, getHeading(spec, duplicates), owner);
		this.duplicates = duplicates;
		tableName = spec;
		columnNames = getColumnNames(tableName).split(",");
		sourceCode = "spec";
	}

	@Override
	public RelvarGlobal getRelvar(String name, RelDatabase database) {
		return new RelvarExternal(name, database, new Generator(database, System.out), this, duplicates);
	}

	@Override
	public void dropRelvar(RelDatabase database) {
	}

	public String getTable() {
		return tableName;
	}

	public String[] getColumnNames() {
		return columnNames;
	}

	@Override
	public String tableClassName() {
		return "TableHadoop";
	}

	@Override
	public String getSourceDefinition() {
		return "EXTERNAL HADOOP " + sourceCode;
	}

	@Override
	public String getType() {
		return "Hadoop";
	}
}
