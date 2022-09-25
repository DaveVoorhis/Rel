package org.reldb.rel.v0.values;

import java.io.PrintStream;

import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.types.Type;
import org.reldb.rel.v0.types.TypeAlpha;
import org.reldb.rel.v0.types.builtin.TypeRational;
import org.reldb.rel.v0.vm.Context;

public class ValueRational extends ValueAlpha implements Comparable<Value> {
	
	private static final long serialVersionUID = 0;

	private double internalValue;
	
	public static ValueRational select(Generator generator, double x) {
		return (ValueRational)generator.selectValue(TypeRational.getInstance(), new ValueRational(generator, x));
	}
	
	private ValueRational(Generator generator, double b) {
		super(generator, TypeRational.getInstance(), new Value[1], 0);
		internalValue = b;
	}
	
	public Value getComponentValue(int offsetInValue) {
		return select(getGenerator(), internalValue);
	}

	public void setComponentValue(int offsetInValue, Value value) {
		internalValue = value.doubleValue();
	}
	
	public void toStream(Context context, Type contextualType, PrintStream p, int depth) {
		Generator generator = getGenerator();
		TypeAlpha type = (TypeAlpha) getType(context.getVirtualMachine().getRelDatabase());
		setMST(generator.findMST(type, new ValueRational(generator, internalValue)));
		String typeSignature = getType(generator.getDatabase()).getSignature(); 
		if (typeSignature.equals(TypeRational.Name))
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
		return (long)internalValue;
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
		return Double.valueOf(internalValue).hashCode();
	}
	
	public int compareTo(Value v) {
		if (internalValue == v.doubleValue())
			return 0;
		else if (internalValue > v.doubleValue())
			return 1;
		else
			return -1;
	}
	
	public String toString() {
		return "" + internalValue;
	}

}
