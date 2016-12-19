package org.reldb.rel.v0.storage.relvars;

import java.lang.reflect.InvocationTargetException;

import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.storage.RelDatabase;
import org.reldb.rel.v0.storage.tables.TableCustom;
import org.reldb.rel.v0.storage.tables.TableExternal;
import org.reldb.rel.v0.storage.tables.TableExternal.DuplicateHandling;
import org.reldb.rel.v0.values.RelTupleFilter;
import org.reldb.rel.v0.values.RelTupleMap;
import org.reldb.rel.v0.values.TupleFilter;
import org.reldb.rel.v0.values.TupleIterator;
import org.reldb.rel.v0.values.ValueRelation;
import org.reldb.rel.v0.values.ValueTuple;
import org.reldb.rel.v0.version.Version;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Operator;

public class RelvarExternal extends RelvarGlobal {
	
	private TableExternal table;
	
	public RelvarExternal(String name, RelDatabase database, Generator generator, RelvarCustomMetadata metadata, DuplicateHandling duplicates) {
		super(name, database);
		String tableName = metadata.tableClassName();
		String type = metadata.getType();
		try {
			table = (TableCustom)Class.forName("org.reldb.rel.v" + Version.getDatabaseVersion() + ".storage.relvars.external." + type.toLowerCase() + "." + tableName).getConstructors()[0].newInstance(name, metadata, generator, duplicates);
		} catch (ClassNotFoundException e) {
			throw new ExceptionSemantic("RS0223: VAR " + name + " of type " + type + " could not be found");
		} catch (ClassCastException e) {
			throw new ExceptionSemantic("RS0224: VAR " + name + " of type " + type + " does not extend TableCustom");
		} catch (InvocationTargetException e) {
			throw new ExceptionSemantic("RS0455: VAR " + name + " of type " + type + " could not be loaded due to " + e.getCause());
		} catch (Exception e) {
			throw new ExceptionSemantic("RS0456: VAR " + name + " of type " + type + " could not be loaded due to " + e);
		}
	}
	
	public long getCardinality(Generator generator) {
		return table.getCardinality();
	}
	
	public boolean contains(Generator generator, ValueTuple tuple) {
		return table.contains(generator, tuple);
	}

	public ValueTuple getTupleForKey(Generator generator, ValueTuple tuple) {
		return table.getTupleForKey(generator, tuple);
	}
	
	public void setValue(ValueRelation relation) {
		table.setValue(this, relation);
	}
	
	public long insert(Generator generator, ValueTuple tuple) {
		return table.insert(generator, tuple);
	}
	
	public long insert(Generator generator, ValueRelation relation) {
		return table.insert(generator, relation);
	}
	
	public long insertNoDuplicates(Generator generator, ValueRelation relation) {
		return table.insertNoDuplicates(generator, relation);
	}
	
	// Delete all tuples
	public void purge() {
		table.purge();
	}

	// Delete given tuple
	public void delete(Generator generator, ValueTuple tuple) {
		table.delete(generator, tuple);
	}
	
	// Delete selected tuples
	public long delete(Context context, Operator whereTupleOperator) {
		return table.delete(context.getGenerator(), new RelTupleFilter(context, whereTupleOperator));
	}
	
	// Delete selected tuples
	public long delete(Generator generator, TupleFilter filter) {
		return table.delete(generator, filter);
	}

	// Delete specified tuples.  If there are tuplesToDelete not found in this Relvar, and errorIfNotIncluded is true, throw an error.	
	public long delete(Context context, ValueRelation tuplesToDelete, boolean errorIfNotIncluded) {
		return table.delete(context, tuplesToDelete, errorIfNotIncluded);
	}
	
	// Update all tuples using a given update operator
	public long update(Context context, Operator updateTupleOperator) {
		return table.update(context.getGenerator(), new RelTupleMap(context, updateTupleOperator));
	}
	
	// Update selected tuples using a given update operator
	public long update(Context context, Operator whereTupleOperator, Operator updateTupleOperator) {
		return table.update(context.getGenerator(), new RelTupleFilter(context, whereTupleOperator), new RelTupleMap(context, updateTupleOperator));
	}

	// Get a TupleIterator
	public TupleIterator iterator(Generator generator) {
		return table.iterator(generator);
	}
	
}
