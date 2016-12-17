package org.reldb.rel.v0.values;

import java.io.PrintStream;
import java.util.Vector;

import org.reldb.rel.exceptions.ExceptionFatal;
import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.types.*;
import org.reldb.rel.v0.vm.Context;

public class ValueTuple extends ValueAbstract implements Projectable {

	private static final long serialVersionUID = 0;

	private Value[] values;
	
	/** Create a new tuple with default attribute values. */
	public ValueTuple(Generator generator, TypeTuple type) {
		super(generator);
		Heading heading = type.getHeading();
		this.values = new Value[heading.getDegree()];
		int i = 0;
		for (Attribute attribute: heading.getAttributes())
			this.values[i++] = attribute.getType().getDefaultValue(generator);
		loaded(generator);
	}
	
	/** Create a new tuple given an array of tuple attribute values. */
	public ValueTuple(Generator generator, Value[] values) {
		super(generator);
		this.values = values;
		loaded(generator);
	}
	
	public void loaded(Generator generator) {
		super.loaded(generator);
		for (Value value: values)
			if (value != null)
				value.loaded(generator);
	}

	/** Obtain a serializable clone of this value. */
	public Value getSerializableClone() {
		Value[] newValues = new Value[values.length];
		for (int i=0; i<values.length; i++)
			newValues[i] = values[i].getSerializableClone();
		return new ValueTuple(getGenerator(), newValues);
	}
	
	public static ValueTuple getEmptyTuple(Generator generator) {
		return new ValueTuple(generator, TypeTuple.getEmptyTupleType());
	}
	
	/** Return the array of ValueS in this tuple. */
	public Value[] getValues() {
		return values;
	}

	/** Assign ValueS to this tuple from the source ValueTuple according to an AttributeMap. */
	public void assign(AttributeMap map, ValueTuple source) {
		map.assign(this.values, source);
	}
	
	/** Create a new tuple by projecting this ValueTuple according to an AttributeMap. */
	public Value project(AttributeMap map) {
		return map.project(getGenerator(), this);
	}
	
	/** Create a new tuple by a disjoint join of this and another ValueTuple. */
	public ValueTuple joinDisjoint(ValueTuple right) {
		Value[] valueArray = new Value[values.length + right.values.length];
		System.arraycopy(values, 0, valueArray, 0, values.length);
		System.arraycopy(right.values, 0, valueArray, values.length, right.values.length);
		return new ValueTuple(getGenerator(), valueArray);
	}
	
	/** Create a new tuple by a join of this and another ValueTuple.  Assume that common attributes
	 * have matching values, as (perhaps) determined by isJoinable(). */
	public ValueTuple join(JoinMap map, ValueTuple right) {
		return map.join(getGenerator(), this, right);
	}
		
	/** Create a new tuple by a join of this and another ValueTuple.  Throw an exception if
	 * common attributes do not have matching values. */
	public ValueTuple joinChecked(JoinMap map, ValueTuple right) {
		return map.joinChecked(getGenerator(), this, right);
	}

	/** Create a new tuple by removing an attribute specified by a given zero-based index. */
	public ValueTuple shrink(int attributeIndex) {
		Value[] valueArray = new Value[values.length - 1];
		System.arraycopy(values, 0, valueArray, 0, attributeIndex);
		System.arraycopy(values, attributeIndex + 1, valueArray, attributeIndex, values.length - attributeIndex - 1);
		return new ValueTuple(getGenerator(), valueArray);
	}
	
	public String getTypeName() {
		return "TUPLE";
	}

	/** Output this Value to a PrintStream. */
	public void toStream(Context context, Type type, PrintStream p, int depth) {
		p.print("TUPLE {");
		Vector<Attribute> attributes = ((TypeTuple)type).getHeading().getAttributes();
		if (values.length != attributes.size())
			throw new ExceptionFatal("RS0452: Bad tuple. Heading " + type + " says degree = " + attributes.size() + " but tuple " + toString() + " says degree = " + values.length + ".");
		int i = 0;
		for (Attribute attribute: attributes) {
			if (i > 0)
				p.print(", ");
			p.print(attribute.getName() + " ");
			values[i].toStream(context, attribute.getType(), p, depth + 1);
			i++;
		}
		p.print("}");
	}
	
	public int hashCode() {
		int code = 0;
		for (Value value: values)
			code += value.hashCode();
		return code;
	}
	
	public int compareTo(Value v) {
		int comparison = 0;
		for (int i=0; i<values.length; i++) {
			comparison = values[i].compareTo(((ValueTuple)v).values[i]);
			if (comparison != 0)
				break;
		}
		return comparison;
	}
	
	public String toString() {
		String out = null;
		for (Value value: values)
			out = ((out == null) ? "" : out + ", ") + ((value == null) ? "null" : value.toString());
		return "TUPLE {" + ((out == null) ? "" : out) + "}";
	}
	
	public String toCSV() {
		StringBuffer str = new StringBuffer("");
		for (Value value: values) {
			str.append(value.toString() + ",");
		}
		str.deleteCharAt(str.length()-1);
		return str.toString().trim();
	}
}
