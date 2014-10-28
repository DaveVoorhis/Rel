package ca.mb.armchair.rel3.values;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import ca.mb.armchair.rel3.exceptions.ExceptionSemantic;
import ca.mb.armchair.rel3.generator.Generator;
import ca.mb.armchair.rel3.storage.relvars.Relvar;
import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Operator;

/** Lightweight literal relation.  Implements Relvar semantics so that relation-valued attributes can be updated. */
public class ValueRelationLiteral extends ValueRelation implements Relvar {

	private static final long serialVersionUID = 0;

	private int hashCode;
	
	private HashSet<ValueTuple> lookupTuples;
	private LinkedList<ValueTuple> iterationTuples;
	
	// TODO - MEM - modify ValueRelationLiteral to avoid out-of-memory on high-cardinality relations.
	public ValueRelationLiteral(Generator generator) {
		super(generator);
		purge();
	}
	
	/** Duplicates are rejected and return false. */
	public boolean insert(ValueTuple tuple) {
		if (lookupTuples.add(tuple)) {
			iterationTuples.add(tuple);
			hashCode += tuple.hashCode();
			return true;
		}
		return false;
	}
	
	public void remove(ValueTuple tuple) {
		if (lookupTuples.remove(tuple)) {
			iterationTuples.remove(tuple);
			hashCode -= tuple.hashCode();
		}
	}
	
	@Override
	public int hashCode() {
		return hashCode;
	}
	
	@Override
	public TupleIterator newIterator() {
		return new TupleIterator() {
			ValueTuple currentTuple;
			Iterator<ValueTuple> i = iterationTuples.iterator();
			public boolean hasNext() {
				return i.hasNext();
			}
			public ValueTuple next() {
				currentTuple = i.next();
				currentTuple.loaded(getGenerator());
				return currentTuple;
			}
			public void remove() {
				i.remove();
				iterationTuples.remove(currentTuple);
			}
			public void close() {
			}
		};
	}
	
	@Override
	public long getCardinality() {
		return lookupTuples.size();
	}

	@Override
	public boolean contains(ValueTuple tuple) {
		return lookupTuples.contains(tuple);
	}

	@Override
	public TupleIterator iterator(Generator generator) {
		return iterator();
	}

	@Override
	public long getCardinality(Generator generator) {
		return getCardinality();
	}

	@Override
	public boolean contains(Generator generator, ValueTuple tuple) {
		return contains(tuple);
	}

	@Override
	public void setValue(ValueRelation relation) {
		purge();
		insert(getGenerator(), relation);
	}

	@Override
	public long insert(Generator generator, ValueTuple tuple) {
		if (insert(tuple))
			return 1;
		return 0;
	}

	@Override
	public long insert(Generator generator, ValueRelation relation) {
		long insertCount = 0;
		TupleIterator tuples = relation.iterator();
		try {
			while (tuples.hasNext())
				insertCount += insert(generator, tuples.next());
		} finally {
			tuples.close();
		}
		return insertCount;
	}

	@Override
	public long insertNoDuplicates(Generator generator, ValueRelation relation) {
		long insertCount = 0;
		TupleIterator tuples = relation.iterator();
		try {
			while (tuples.hasNext()) {
				if (insert(generator, tuples.next()) == 0)
					throw new ExceptionSemantic("RS0394: inserting tuple would attempt to insert a duplicate.");
				insertCount++;
			}
		} finally {
			tuples.close();
		}
		return insertCount;
	}

	@Override
	public void purge() {
		lookupTuples = new HashSet<ValueTuple>();
		iterationTuples = new LinkedList<ValueTuple>();
		hashCode = 0;
	}
	
	@Override
	// TODO - note that this is essentially identical to a function in Table
	public long delete(Context context, Operator whereTupleOperator) {
		return delete(context.getGenerator(), new RelTupleFilter(context, whereTupleOperator));
	}
	
	// Update all tuples using a given TupleMap
	// TODO - note that this is essentially identical to a function in Table
	public long update(final Generator generator, final TupleMap map) {
		return update(generator, new TupleFilter() {
			public boolean filter(ValueTuple tuple) {
				return true;
			}
		}, map);
	}

	@Override
	// TODO - note that this is essentially identical to a function in Table
	public long update(Context context, Operator updateTupleOperator) {
		return update(context.getGenerator(), new RelTupleMap(context, updateTupleOperator));
	}

	@Override
	// TODO - note that this is essentially identical to a function in Table
	public long update(Context context, Operator whereTupleOperator, Operator updateTupleOperator) {
		return update(context.getGenerator(), new RelTupleFilter(context, whereTupleOperator), new RelTupleMap(context, updateTupleOperator));
	}

	public long update(Generator generator, TupleFilter whereFilter, TupleMap updateMap) {
		ValueRelationLiteral temp = new ValueRelationLiteral(generator);
		long updateCount = 0;
		TupleIterator iterator = iterator();
		try {
			while (iterator.hasNext()) {
				ValueTuple tuple = iterator.next();
				if (whereFilter.filter(tuple)) {
					ValueTuple newTuple = updateMap.map(tuple);
					iterator.remove();
					ValueTuple data = (ValueTuple)newTuple.getSerializableClone();
					temp.insert(data);
					updateCount++;
				}
			}
		} finally {
			iterator.close();
		}		
		iterator = temp.iterator();
		try {
			while (iterator.hasNext())
				insert(iterator.next());
		} finally {
			iterator.close();
		}
		return updateCount;
	}

	@Override
	public long delete(Generator generator, TupleFilter filter) {
		long deleteCount = 0;
		TupleIterator iterator = iterator();
		try {
			while (iterator.hasNext()) {
				ValueTuple tuple = iterator.next();
				if (filter.filter(tuple)) {
					iterator.remove();
					deleteCount++;
				}
			}
		} finally {
			iterator.close();
		}
		return deleteCount;
	}

	@Override
	// TODO - note that this is identical to a function in Table
	public long delete(Context context, ValueRelation tuplesToDelete, boolean errorIfNotIncluded) {
		final HashMap<ValueTuple, Boolean> toDelete = new HashMap<ValueTuple, Boolean>();
		Generator generator = context.getGenerator();
		// index the tuplesToDelete into the toDelete index, which uses each tupleToDelete as a key and a Boolean as the value.
		TupleIterator iterator = tuplesToDelete.iterator();
		try {
			while (iterator.hasNext())
				toDelete.put(iterator.next(), Boolean.FALSE);
		} finally {
			iterator.close();
		}
		if (errorIfNotIncluded) {
			// make sure every tuple in toDelete is found in this relvar (table) at least once.
			TupleIterator relvarTupleIterator = iterator(generator);
			try {
				while (relvarTupleIterator.hasNext()) {
					ValueTuple keyTuple = relvarTupleIterator.next();
					if (toDelete.containsKey(keyTuple))
						toDelete.put(keyTuple, Boolean.TRUE);
				}
			} finally {
				relvarTupleIterator.close();
			}
			// make sure every entry in index is TRUE, i.e., has been found at least once
			Collection<Boolean>values = toDelete.values();
			Iterator<Boolean> valueIterator = values.iterator();
			while (valueIterator.hasNext())
				if (!valueIterator.next().booleanValue())
					throw new ExceptionSemantic("RS0395: In I_DELETE, one or more specified tuples are not included in the relation-valued attribute.");
		}
		return delete(generator, new TupleFilter() {
			public boolean filter(ValueTuple tuple) {
				return toDelete.containsKey(tuple);
			}
		});
	}

}
