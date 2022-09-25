package org.reldb.rel.v0.values;
 
import java.io.PrintStream;

import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.types.*;
import org.reldb.rel.v0.vm.Context;

public abstract class ValueAbstract implements Value {

	private static final long serialVersionUID = 1L;
	
	private transient Generator generator;

	public ValueAbstract(Generator generator) {
		this.generator = generator;
	}
	
	public void loaded(Generator generator) {
		this.generator = generator;
	}
	
	protected Generator getGenerator() {
		return generator;
	}
	
	/** Output this Value to a PrintStream. */
	public void toStream(Context context, Type type, PrintStream p, int depth) {
		if (depth > 0)
			p.print(toParsableString(type));
		else
			p.print(toString(type));
	}
	
	public boolean equals(Object v) {
		return (compareTo((Value)v) == 0);
	}
	
	public String toParsableString(Type type) {
		return toString();
	}
	
	public String toString(Type type) {
		return toString();
	}
	
	/** Obtain a serializable clone of this value. */
	public Value getSerializableClone() {
		return this;
	}

	/** Convert this to a primitive boolean. */
	public boolean booleanValue() {
		throw new ExceptionSemantic("RS0265: Cannot convert " + getTypeName() + " to BOOLEAN.");
	}

	/** Convert this to a primitive long. */
	public long longValue() {
		throw new ExceptionSemantic("RS0266: Cannot convert " + getTypeName() + " to INTEGER.");
	}

	/** Convert this to a primitive double. */
	public double doubleValue() {
		throw new ExceptionSemantic("RS0267: Cannot convert " + getTypeName() + " to RATIONAL.");
	}

	/** Convert this to a primitive string. */
	public String stringValue() {
		throw new ExceptionSemantic("RS0268: Cannot convert " + getTypeName() + " to CHARACTER.");
	}
}
