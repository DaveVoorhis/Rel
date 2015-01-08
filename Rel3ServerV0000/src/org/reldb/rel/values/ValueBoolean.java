package org.reldb.rel.values;

import java.io.PrintStream;

import org.reldb.rel.generator.Generator;
import org.reldb.rel.types.Type;
import org.reldb.rel.types.TypeAlpha;
import org.reldb.rel.types.builtin.TypeBoolean;
import org.reldb.rel.vm.Context;

public class ValueBoolean extends ValueAlpha implements Comparable<Value> {

	private static final long serialVersionUID = 0;
	
	private boolean internalValue;
	
	public static ValueBoolean select(Generator generator, boolean x) {
		return (ValueBoolean)generator.selectValue(TypeBoolean.getInstance(), new ValueBoolean(generator, x));
	}
	
	private ValueBoolean(Generator generator, boolean b) {
		super(generator, TypeBoolean.getInstance(), new Value[1], 0);
		internalValue = b;
	}
 
	public Value getComponentValue(int offsetInValue) {
		return select(getGenerator(), internalValue);
	}

	public void setComponentValue(int offsetInValue, Value value) {
		internalValue = value.booleanValue();
	}
	
	public void toStream(Context context, Type contextualType, PrintStream p, int depth) {
		Generator generator = getGenerator();
		TypeAlpha type = (TypeAlpha) getType(context.getVirtualMachine().getRelDatabase());
		setMST(generator.findMST(type, new ValueBoolean(generator, internalValue)));
		String typeSignature = getType(generator.getDatabase()).getSignature(); 
		if (typeSignature.equals(TypeBoolean.Name))
			p.print(internalValue);
		else
			p.print(typeSignature + "(" + internalValue + ")");
	}
	
	/** Convert this to a primitive boolean. */
	public boolean booleanValue() {
		return internalValue;
	}
	
	/** Convert this to a primitive long. */
	public long longValue() {
		return (internalValue) ? 1 : 0;
	}
	
	/** Convert this to a primitive double. */
	public double doubleValue() {
		return (internalValue) ? 1 : 0;
	}
	
	/** Convert this to a primitive string. */
	public String stringValue() {
		return (internalValue) ? "true" : "false";
	}

	public int hashCode() {
		return Boolean.valueOf(internalValue).hashCode();
	}
	
	public int compareTo(Value v) {
		if (internalValue == v.booleanValue())
			return 0;
		else if (internalValue)
			return 1;
		else
			return -1;
	}
	
	public String toString() {
		return "" + internalValue;
	}

}
