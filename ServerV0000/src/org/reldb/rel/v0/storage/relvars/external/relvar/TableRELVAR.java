package org.reldb.rel.v0.storage.relvars.external.relvar;

import org.reldb.rel.client.connection.string.ClientNetwork;
import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.interpreter.Evaluation;
import org.reldb.rel.v0.interpreter.Interpreter;
import org.reldb.rel.v0.storage.RelDatabase;
import org.reldb.rel.v0.storage.relvars.RelvarExternal;
import org.reldb.rel.v0.storage.relvars.RelvarExternalMetadata;
import org.reldb.rel.v0.storage.relvars.RelvarHeading;
import org.reldb.rel.v0.storage.tables.TableCustom;
import org.reldb.rel.v0.types.Heading;
import org.reldb.rel.v0.types.TypeTuple;
import org.reldb.rel.v0.values.RelTupleFilter;
import org.reldb.rel.v0.values.RelTupleMap;
import org.reldb.rel.v0.values.TupleFilter;
import org.reldb.rel.v0.values.TupleIterator;
import org.reldb.rel.v0.values.ValueRelation;
import org.reldb.rel.v0.values.ValueTuple;
import org.reldb.rel.v0.vm.Context;

public class TableRELVAR extends TableCustom {

	private RelvarRELVARMetadata meta;
	private Generator generator;
	private ClientNetwork connection;
	private Heading fileHeading;

	public TableRELVAR(String Name, RelvarExternalMetadata metadata, Generator generator, DuplicateHandling duplicates) {
		meta = (RelvarRELVARMetadata) metadata;
		this.generator = generator;
		RelDatabase database = generator.getDatabase();
		RelvarHeading heading = meta.getHeadingDefinition(database);
		Heading storedHeading = heading.getHeading();
		fileHeading = RelvarRELVARMetadata.getHeading(database, meta.getConnectionString(), duplicates).getHeading();
		if (storedHeading.toString().compareTo(fileHeading.toString()) != 0)
			throw new ExceptionSemantic("RS0487: Stored Rel metadata is " + storedHeading + " but relvar metadata is " + fileHeading + ". Has the relvar structure changed?");
	}

	@Override
	public TupleIterator iterator() {
		return eval(meta.getRelvar()).iterator();
	}

	@Override
	public long getCardinality() {
		String response = evaluate("COUNT(" + meta.getRelvar() + ")");
		try {
			return Long.parseLong(response);
		} catch (NumberFormatException nfe) {
			throw new ExceptionSemantic("RS0498: Unable to determine cardinality of remote Rel relvar.");
		}
	}

	@Override
	public TupleIterator iterator(Generator generator) {
		return iterator();
	}

	private static void notImplemented(String what) {
		throw new ExceptionSemantic("RS0490: External Rel relvars do not yet support " + what + ".");
	}

	@Override
	public boolean contains(Generator generator, ValueTuple tuple) {
		String tupleText = tuple.toString(new TypeTuple(fileHeading));
		String query = tupleText + " IN " + meta.getRelvar();
		String response = evaluate(query);
		return response.equalsIgnoreCase("true");
	}

	@Override
	public ValueTuple getTupleForKey(Generator generator, ValueTuple tuple) {
		return null;
	}

	@Override
	public void setValue(RelvarExternal relvarJDBC, ValueRelation relation) {
		notImplemented("assignment");
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

	private String evaluate(String query) {
		try {
			connection = new ClientNetwork(meta.getHost(), meta.getPort());
			connection.sendEvaluate(meta.getRelvar());
			String response = "";
			String line;
			while ((line = connection.receive()) != null)
				response += line;
			return response;
		} catch (Throwable e) {
			throw new ExceptionSemantic("RS0496: Unable to connect to remote Rel DBMS due to: " + e);
		}		
	}
	
	private ValueRelation eval(String query) {
		try {
			String response = evaluate(query);
			Interpreter interpreter = new Interpreter(generator.getDatabase(), System.out);
			Evaluation result = interpreter.evaluate(response);
			return (ValueRelation)result.getValue();
		} catch (Throwable e) {
			throw new ExceptionSemantic("RS0497: Unable to process result from remote Rel DBMS due to: " + e);
		}
	}
}
