package plugins.relvars.Hadoop;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.reldb.rel.exceptions.ExceptionFatal;
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
import org.reldb.rel.v0.values.ValueCharacter;
import org.reldb.rel.v0.values.ValueInteger;
import org.reldb.rel.v0.values.ValueRelation;
import org.reldb.rel.v0.values.ValueTuple;
import org.reldb.rel.v0.vm.Context;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TableHADOOP extends TableCustom {

	private DuplicateHandling duplicates;
	private Generator generator;
	private String tableName;

	public TableHADOOP(String Name, RelvarExternalMetadata metadata, Generator generator, DuplicateHandling duplicates) {
		this.generator = generator;
		this.duplicates = duplicates;
		RelvarHADOOPMetadata meta = (RelvarHADOOPMetadata) metadata;
		tableName = meta.getTable();
	}

	private static void notImplemented(String what) {
		throw new ExceptionSemantic("EX0012: HADOOP relvars do not yet support " + what);
	}

	private ValueTuple toTuple(String line) {
		String[] rawValues = line.split(",");
		Value[] values = new Value[rawValues.length + 1];

		int startAt = 1;
		if (duplicates == DuplicateHandling.AUTOKEY) {
			values[0] = ValueInteger.select(generator, Integer.parseInt(rawValues[0]));
			startAt = 2;
		}
		values[startAt - 1] = ValueCharacter.select(generator, rawValues[startAt - 1]);
		for (int i = startAt; i < rawValues.length; i++)
			values[i] = ValueCharacter.select(generator, rawValues[i]);
		return new ValueTuple(generator, values);
	}

	@Override
	public TupleIterator iterator() {
		if (duplicates == DuplicateHandling.DUP_REMOVE)
			return dupremoveIterator();
		else if (duplicates == DuplicateHandling.DUP_COUNT)
			return dupcountIterator();
		else if (duplicates == DuplicateHandling.AUTOKEY)
			return autokeyIterator();
		else
			throw new ExceptionSemantic("EX0013: Non-Identified duplicate handling method: " + duplicates.toString());
	}

	@Override
	public TupleIterator iterator(Generator generator) {
		return iterator();
	}

	@Override
	public long getCardinality() {
		return 0;
	}

	@Override
	public boolean contains(Generator generator, ValueTuple tuple) {
		return false;
	}

	@Override
	public ValueTuple getTupleForKey(Generator generator, ValueTuple tuple) {
		return null;
	}

	@Override
	public void setValue(RelvarExternal relvarCSV, ValueRelation relation) {
		notImplemented("ASSIGNMENT");
	}

	@Override
	public long insert(Generator generator, ValueRelation relation) {
		notImplemented("INSERT");
		return 0;
	}

	@Override
	public long insert(Generator generator, ValueTuple tuple) {
		notImplemented("INSERT");
		return 0;
	}

	@Override
	public long insertNoDuplicates(Generator generator, ValueRelation relation) {
		notImplemented("INSERT");
		return 0;
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
		notImplemented("DELETE");
		return 0;
	}

	@Override
	public long delete(Generator generator, TupleFilter filter) {
		notImplemented("DELETE");
		return 0;
	}

	@Override
	public long delete(Context context, ValueRelation tuplesToDelete, boolean errorIfNotIncluded) {
		notImplemented("DELETE");
		return 0;
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

	private String toXML(ArrayList<String> columnsCellsValues) {
		StringBuffer xmlString = new StringBuffer();
		ArrayList<String> columnNames = new ArrayList<String>();
		for (String line : columnsCellsValues)
			columnNames.add(line.split(",")[0]);
		for (String column : columnNames)
			try {
				DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

				Document doc = docBuilder.newDocument();
				Element rootElement = doc.createElement(tableName);
				doc.appendChild(rootElement);

				for (String cellValue : columnsCellsValues) {
					String columnname = cellValue.split(",")[0];
					if (columnname.equals(column)) {
						System.out.println(column);
						String cell = cellValue.split(",")[1];
						String value = cellValue.split(",")[2];

						Element columnXML = doc.createElement(column);
						rootElement.appendChild(columnXML);

						Attr attr = doc.createAttribute("Cell");
						attr.setValue(cell);
						columnXML.setAttributeNode(attr);

						Element firstname = doc.createElement("Value");
						firstname.appendChild(doc.createTextNode(value));
						columnXML.appendChild(firstname);
					}
				}

				Transformer transformer = TransformerFactory.newInstance().newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				StreamResult result = new StreamResult(new StringWriter());
				DOMSource source = new DOMSource(doc);
				transformer.transform(source, result);

				String resultXML = result.getWriter().toString();
				if (!xmlString.toString().contains(resultXML))
					xmlString.append(resultXML + ",");

			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			}
		if (xmlString != null)
			xmlString.deleteCharAt(xmlString.length() - 1);

		return xmlString.toString();
	}

	private TupleIterator autokeyIterator() {
		try {
			return new TupleIterator() {
				org.apache.hadoop.conf.Configuration config = HBaseConfiguration.create();
				HBaseAdmin admin = new HBaseAdmin(config);
				HTable table = new HTable(config, tableName);
				Scan s = new Scan();
				ResultScanner ss = table.getScanner(s);
				Iterator<Result> resultIterator = ss.iterator();
				long autokey = 1;

				@Override
				public boolean hasNext() {
					return resultIterator.hasNext();
				}

				@Override
				public ValueTuple next() {
					Result result = resultIterator.next();
					String rowName = null;
					ArrayList<String> ColumnCellValue = new ArrayList<String>();
					for (KeyValue kv : result.raw()) {
						rowName = new String(kv.getRow());
						ColumnCellValue.add(new String(kv.getFamily()) + "," + new String(kv.getQualifier()) + "," + new String(kv.getValue()));
					}
					try {
						return toTuple(autokey + "," + rowName + "," + toXML(ColumnCellValue));
					} finally {
						autokey++;
					}
				}

				@Override
				public void close() {
					try {
						admin.close();
						table.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
		} catch (MasterNotRunningException e) {
			throw new ExceptionFatal("EX0031");
		} catch (ZooKeeperConnectionException e) {
			throw new ExceptionFatal("EX0032");
		} catch (IOException e) {
			throw new ExceptionFatal("EX0033: Failed to connect to database: " + e.toString());
		}
	}

	private TupleIterator dupremoveIterator() {
		try {
			return new TupleIteratorUnique(new TupleIterator() {
				Configuration config = HBaseConfiguration.create();
				HBaseAdmin admin = new HBaseAdmin(config);
				HTable table = new HTable(config, tableName);
				Scan s = new Scan();
				ResultScanner ss = table.getScanner(s);
				Iterator<Result> resultIterator = ss.iterator();

				@Override
				public boolean hasNext() {
					return resultIterator.hasNext();
				}

				@Override
				public ValueTuple next() {
					Result result = resultIterator.next();
					String rowName = null;
					ArrayList<String> ColumnCellValue = new ArrayList<String>();
					for (org.apache.hadoop.hbase.KeyValue kv : result.raw()) {
						rowName = new String(kv.getRow());
						ColumnCellValue.add(new String(kv.getFamily()) + "," + new String(kv.getQualifier()) + "," + new String(kv.getValue()));
					}
					return toTuple(rowName + "," + toXML(ColumnCellValue));
				}

				@Override
				public void close() {
					try {
						admin.close();
						table.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (MasterNotRunningException e) {
			throw new ExceptionFatal("EX0034");
		} catch (ZooKeeperConnectionException e) {
			throw new ExceptionFatal("EX0035");
		} catch (IOException e) {
			throw new ExceptionFatal("EX0036: Failed to connect to database: " + e.toString());
		}
	}

	private TupleIterator dupcountIterator() {
		return new TupleIteratorUnique(new TupleIteratorCount(new TupleIterator() {
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
		}, generator));
	}
}
