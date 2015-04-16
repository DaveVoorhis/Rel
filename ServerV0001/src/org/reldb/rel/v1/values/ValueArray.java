package org.reldb.rel.v1.values;

import java.io.PrintStream;
import java.util.*;

import org.reldb.rel.exceptions.*;
import org.reldb.rel.v1.generator.Generator;
import org.reldb.rel.v1.types.*;
import org.reldb.rel.v1.vm.Context;

public class ValueArray extends ValueAbstract {

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
		if (depth == 0) {
			Heading heading = ((TypeHeading)elementType).getHeading();
			p.print("ARRAY " + heading + " {");
		} else
			p.print("ARRAY {");
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

}
