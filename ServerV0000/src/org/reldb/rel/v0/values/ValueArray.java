package org.reldb.rel.v0.values;

import java.io.PrintStream;
import java.util.*;

import org.reldb.rel.exceptions.*;
import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.types.*;
import org.reldb.rel.v0.vm.Context;

public class ValueArray extends ValueAbstract implements TupleIteratable {

	private static final long serialVersionUID = 0;

	private ArrayList<ValueTuple> values;
	private ValueRelation relation;

	/** Create a new ARRAY. */
	public ValueArray(Generator generator) {
		super(generator);
		values = new ArrayList<ValueTuple>();
		relation = null;
	}
	
	/** Create a new ARRAY given an ArrayList. */
	ValueArray(Generator generator, ArrayList<ValueTuple> values) {
		super(generator);
		this.values = values;
		relation = null;
	}
	
	/** Create a new ARRAY as a wrapper around a ValueRelation. */
	ValueArray(Generator generator, ValueRelation relation) {
		super(generator);
		this.values = null;
		this.relation = relation;
	}
	
	private void convertToArray() {
		TupleIterator iterator = relation.iterator();
		try {
			values = new ArrayList<ValueTuple>();
			while (iterator.hasNext())
				values.add(iterator.next());
			relation = null;
		} finally {
			iterator.close();
		}
	}
	
	public Value project(final AttributeMap map) {
		return new ValueArray(getGenerator()) {
			private final static long serialVersionUID = 0;

			public TupleIterator iterator() {
				return new TupleIterator() {
					TupleIterator tuples = ValueArray.this.iterator();

					public boolean hasNext() {
						return tuples.hasNext();
					}

					public ValueTuple next() {
						return (ValueTuple)tuples.next().project(map);
					}
					
					public void close() {
						tuples.close();
					}					
				};
			}
		};
	}

	public ValueRelation toRelation() {
		return new ValueRelation(getGenerator()) {
			private final static long serialVersionUID = 0;

			public int hashCode() {
				return 0;
			}

			public TupleIterator newIterator() {
				return new TupleIteratorUnique(ValueArray.this.iterator());
			}
		};
	}
	
	public String getTypeName() {
		return "ARRAY";
	}

	public long getCount() {
		if (values == null)
			return relation.getCardinality();
		else
			return values.size();
	}

	private void checkIndexOutOfBounds(int index) {
		if (index >= values.size())
			if (values.size() == 0)
				throw new ExceptionSemantic("RS0270: Array index " + index + " is out of bounds; array is empty.");				
			else
				throw new ExceptionSemantic("RS0271: Array index " + index + " is out of bounds; highest index is " + (values.size() - 1) + ".");
		else if (index < 0)
			throw new ExceptionSemantic("RS0272: Array index " + index + " is out of bounds; it's less than 0.");					
	}
	
	public Value get(int index) {
		if (values == null)
			convertToArray();
		checkIndexOutOfBounds(index);
		return values.get(index);
	}

	public void set(int index, ValueTuple value) {
		if (values == null)
			convertToArray();
		checkIndexOutOfBounds(index);
		values.set(index, value);
	}
	
	public void append(ValueTuple value) {
		if (values == null)
			convertToArray();
		values.add(value);
	}
	
	public TupleIterator iterator() {
		if (values == null)
			return relation.iterator();
		else
			return new TupleIterator() {
				Iterator<ValueTuple> iterator = values.iterator();
				public boolean hasNext() {
					return iterator.hasNext();
				}
				public ValueTuple next() {
					return (ValueTuple)iterator.next();
				}
				public void close() {
				}
			};
	}

	private void toStreamFromRelation(Context context, Type type, PrintStream p, int depth) {
		TypeTuple tupleType = ((TypeArray)type).getElementType();
		Heading heading = tupleType.getHeading();
		if (depth == 0)
			p.print("ARRAY " + heading + " {");
		else
			p.print("ARRAY {");
		long count = 0;
		TupleIterator iterator = relation.iterator();
		try {
			while (iterator.hasNext()) {
				ValueTuple tuple = iterator.next();
				if (count++ > 0)
					p.print(',');
				p.print("\n\t");
				tuple.toStream(context, tupleType, p, depth + 1);
			}
		} finally {
			iterator.close();
		}
		p.print("\n}");		
	}
	
	private void toStreamFromArray(Context context, Type type, PrintStream p, int depth) {
		TypeTuple elementType = ((TypeArray)type).getElementType();
		Heading heading = ((TypeHeading)elementType).getHeading();
		p.print("ARRAY " + heading + " {");
		long count = 0;
		for (Value value: values) {
			if (count++ > 0)
				p.print(',');
			p.print("\n\t");
			value.toStream(context, elementType, p, depth + 1);
		}
		p.print("\n}");		
	}
	
	/** Output this Value to a PrintStream. */
	public void toStream(Context context, Type type, PrintStream p, int depth) {
		if (values == null)
			toStreamFromRelation(context, type, p, depth);
		else
			toStreamFromArray(context, type, p, depth);
	}
		
	public int hashCode() {
		int code = 0;
		for (Value value: values)
			code += value.hashCode();
		return code;
	}
	
	public int compareTo(Value v) {
		throw new ExceptionSemantic("RS0273: ARRAY does not support comparison.");
	}

	public String toString() {
		String s = "";
		s += "ARRAY {";
		long count = 0;
		TupleIterator iterator = iterator();
		try {
			while (iterator.hasNext()) {
				ValueTuple tuple = iterator.next();
				if (count++ > 0)
					s += ", ";
				s += tuple.toString();
			}
		} finally {
			iterator.close();
		}
		s += "}";
		return s;
	}

	@Override
	public TupleIteratable map(TupleMap map) {
		return new ValueArray(getGenerator()) {
			private final static long serialVersionUID = 0;

			public int hashCode() {
				return 0;
			}

			public TupleIterator iterator() {
				return new TupleIterator() {
					TupleIterator iterator = ValueArray.this.iterator();

					public boolean hasNext() {
						return iterator.hasNext();
					}

					public ValueTuple next() {
						return map.map(iterator.next());
					}
					
					public void close() {
						iterator.close();
					}
				};
			}
		};
	}

	@Override
	public Value sort(OrderMap map) {
		if (map.getMap().length == 0)
			return this;
		else {
			// TODO - refactor to re-use code in ValueRelation's sort(OrderMap map)
			// TODO - MEM - fix so that high-cardinality relations don't run out of RAM
			final ArrayList<ValueTuple> array = new ArrayList<ValueTuple>();
	    	(new TupleIteration(iterator()) {
	    		public void process(ValueTuple tuple) {
	    			array.add(tuple);		
	    		}
	    	}).run();
			Collections.sort(array, new Sorter(map));
			return new ValueArray(getGenerator(), array);
		}
	}
	
	/** Aggregate operator */
	public Value sumInteger(final int attributeIndex) {
		TupleFold folder = new TupleFold(iterator(), attributeIndex) {
			public Value getIdentity() {
				return ValueInteger.select(getGenerator(), 0);
			}
			public Value fold(Value left, Value right) {
				return ValueInteger.select(getGenerator(), left.longValue() + right.longValue());
			}
		};
		folder.run();
		return folder.getResult();
	}

	/** Aggregate operator */
	public Value sumRational(final int attributeIndex) {
		TupleFold folder = new TupleFold(iterator(), attributeIndex) {
			public Value getIdentity() {
				return ValueRational.select(getGenerator(), 0);
			}
			public Value fold(Value left, Value right) {
				return ValueRational.select(getGenerator(), left.doubleValue() + right.doubleValue());
			}
		};
		folder.run();
		return folder.getResult();
	}

	/** Aggregate operator */
	public ValueRational avgInteger(int attributeIndex) {
		TupleFold folder = new TupleFold(iterator(), attributeIndex) {
			public Value getIdentity() {
				return ValueInteger.select(getGenerator(), 0);
			}
			public Value fold(Value left, Value right) {
				return ValueInteger.select(getGenerator(), left.longValue() + right.longValue());
			}
		};
		folder.run();
		Value sum = folder.getResult();
		if (folder.getCount() == 0)
			throw new ExceptionSemantic("RS0276: Result of AVG on no values is undefined.");
		else
			return (ValueRational)ValueRational.select(getGenerator(), sum.doubleValue() / (double)folder.getCount());
	}

	/** Aggregate operator */
	public ValueRational avgRational(int attributeIndex) {
		TupleFold folder = new TupleFold(iterator(), attributeIndex) {
			public Value getIdentity() {
				return ValueRational.select(getGenerator(), 0);
			}
			public Value fold(Value left, Value right) {
				return ValueRational.select(getGenerator(), left.doubleValue() + right.doubleValue());
			}
		};
		folder.run();
		Value sum = folder.getResult();
		if (folder.getCount() == 0)
			throw new ExceptionSemantic("RS0277: Result of AVG on no values is undefined.");
		else
			return (ValueRational)ValueRational.select(getGenerator(), sum.doubleValue() / (double)folder.getCount());
	}

	/** Aggregate operator */
	public Value max(int attributeIndex) {
		TupleFold folder = new TupleFoldFirstIsIdentity("Result of MAX on no values is undefined.", iterator(), attributeIndex) {
			public Value fold(Value left, Value right) {
				if (left.compareTo(right) > 0)
					return left;
				else
					return right;
			}
		};
		folder.run();
		return folder.getResult();
	}

	/** Aggregate operator */
	public Value min(int attributeIndex) {
		TupleFold folder = new TupleFoldFirstIsIdentity("Result of MIN on no values is undefined.", iterator(), attributeIndex) {
			public Value fold(Value left, Value right) {
				if (left.compareTo(right) < 0)
					return left;
				else
					return right;
			}
		};
		folder.run();
		return folder.getResult();
	}

	/** Aggregate operator */
	public ValueBoolean and(int attributeIndex) {
		TupleFold folder = new TupleFold(iterator(), attributeIndex) {
			public Value getIdentity() {
				return ValueBoolean.select(getGenerator(), true);
			}
			public Value fold(Value left, Value right) {
				return ValueBoolean.select(getGenerator(), left.booleanValue() & right.booleanValue());
			}
		};
		folder.run();
		return (ValueBoolean)folder.getResult();
	}

	/** Aggregate operator */
	public ValueBoolean or(int attributeIndex) {
		TupleFold folder = new TupleFold(iterator(), attributeIndex) {
			public Value getIdentity() {
				return ValueBoolean.select(getGenerator(), false);
			}
			public Value fold(Value left, Value right) {
				return ValueBoolean.select(getGenerator(), left.booleanValue() | right.booleanValue());
			}
		};
		folder.run();
		return (ValueBoolean)folder.getResult();
	}

	/** Aggregate operator */
	public ValueBoolean xor(int attributeIndex) {
		TupleFold folder = new TupleFold(iterator(), attributeIndex) {
			public Value getIdentity() {
				return ValueBoolean.select(getGenerator(), false);
			}
			public Value fold(Value left, Value right) {
				return ValueBoolean.select(getGenerator(), left.booleanValue() ^ right.booleanValue());
			}
		};
		folder.run();
		return (ValueBoolean)folder.getResult();
	}

	/** Aggregate operator */
	public Value equiv(int attributeIndex) {
		TupleFold folder = new TupleFold(iterator(), attributeIndex) {
			public Value getIdentity() {
				return ValueBoolean.select(getGenerator(), true);
			}
			public Value fold(Value left, Value right) {
				return ValueBoolean.select(getGenerator(), left.booleanValue() == right.booleanValue());
			}
		};
		folder.run();
		return (ValueBoolean)folder.getResult();
	}

	/** Aggregate operator */
	public ValueRelation union(int attributeIndex) {
		TupleFold folder = new TupleFold(iterator(), attributeIndex) {
			public Value fold(Value left, Value right) {
				return ((ValueRelation)left).union((ValueRelation)right);
			}
			@Override
			public Value getIdentity() {
				return new ValueRelationLiteral(getGenerator());
			}
		};
		folder.run();
		return (ValueRelation)folder.getResult();
	}

	/** Aggregate operator */
	public Value xunion(int attributeIndex) {
		TupleFold folder = new TupleFold(iterator(), attributeIndex) {
			public Value fold(Value left, Value right) {
				return ((ValueRelation)left).xunion((ValueRelation)right);
			}
			public Value getIdentity() {
				return new ValueRelationLiteral(getGenerator());
			}
		};
		folder.run();
		return (ValueRelation)folder.getResult();
	}

	/** Aggregate operator */
	public ValueRelation d_union(int attributeIndex) {
		TupleFold folder = new TupleFold(iterator(), attributeIndex) {
			public Value fold(Value left, Value right) {
				return ((ValueRelation)left).dunion((ValueRelation)right);
			}
			@Override
			public Value getIdentity() {
				return new ValueRelationLiteral(getGenerator());
			}
		};
		folder.run();
		return (ValueRelation)folder.getResult();
	}

	/** Aggregate operator */
	public ValueRelation intersect(int attributeIndex) {
		TupleFold folder = new TupleFoldFirstIsIdentity("Result of INTERSECT on no values is undefined.", iterator(), attributeIndex) {
			public Value fold(Value left, Value right) {
				return ((ValueRelation)left).intersect((ValueRelation)right);
			}
		};
		folder.run();
		return (ValueRelation)folder.getResult();
	}

}
