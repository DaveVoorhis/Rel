package org.reldb.rel.v1.values;
 
import java.io.PrintStream;

import org.reldb.rel.v1.generator.Generator;
import org.reldb.rel.v1.types.Type;
import org.reldb.rel.v1.types.TypeAlpha;
import org.reldb.rel.v1.types.builtin.TypeInteger;
import org.reldb.rel.v1.vm.Context;

public class ValueInteger extends ValueAlpha implements Comparable<Value> {
	
	private static final long serialVersionUID = 0;

	private long internalValue;
	
	public static ValueInteger select(Generator generator, long x) {
		return (ValueInteger)generator.selectValue(TypeInteger.getInstance(), new ValueInteger(generator, x));
	}
	
	private ValueInteger(Generator generator, long b) {
		super(generator, TypeInteger.getInstance(), new Value[1], 0);
		internalValue = b;
	}

	public Value getComponentValue(int offsetInValue) {
		return select(getGenerator(), internalValue);
	}

	public void setComponentValue(int offsetInValue, Value value) {
		internalValue = value.longValue();
	}
	
	public void toStream(Context context, Type contextualType, PrintStream p, int depth) {
		Generator generator = getGenerator();
		TypeAlpha type = (TypeAlpha) getType(context.getVirtualMachine().getRelDatabase());
		setMST(generator.findMST(type, new ValueInteger(generator, internalValue)));
		String typeSignature = getType(generator.getDatabase()).getSignature(); 
		if (typeSignature.equals(TypeInteger.Name))
			p.print(internalValue);
		else
			p.print(typeSignature + "(" + internalValue + ")");
	}

	/** Convert this to a primitive boolean. */
	public boolean booleanValue() {
		return (internalValue != 0) ? true : false;
	}
	
	/** Convert this to a primitive long. */
	public long longValue() {
		return internalValue;
	}
	
	/** Convert this to a primitive double. */
	public double doubleValue() {
		return (double)internalValue;
	}
	
	/** Convert this to a primitive String. */
	public String stringValue() {
		return "" + internalValue;
	}

	public int hashCode() {
		return Long.valueOf(internalValue).hashCode();
	}
	
	/** Compare this integer to another. */
	public int compareTo(Value v) {
		if (internalValue == v.longValue())
			return 0;
		else if (internalValue > v.longValue())
			return 1;
		else
			return -1;
	}
	
	public Value add(Generator generator, Value v) {
		return new ValueInteger(generator, internalValue + v.longValue());
	}
	
	public String toString() {
		return "" + internalValue;
	}

}
