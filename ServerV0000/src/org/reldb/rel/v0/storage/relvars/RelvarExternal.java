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
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Operator;

public class RelvarExternal extends RelvarGlobal {
	
	private TableExternal table;
	
	public RelvarExternal(String name, RelDatabase database, Generator generator, RelvarCustomMetadata metadata, DuplicateHandling duplicates) {
		super(name, database);
			String tableName = metadata.tableClassName();
			String type = metadata.getType();
			try {
				table = (TableCustom)Class.forName("Relplugins.relvars." + type.toUpperCase() + "." + tableName).getConstructors()[0].newInstance(name, metadata, generator, duplicates);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				throw new ExceptionSemantic("RS0223: " + tableName + " could not be found");
			} catch(ClassCastException e) {
				throw new ExceptionSemantic("RS0224: " + tableName + " does not extend TableCustom");
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
