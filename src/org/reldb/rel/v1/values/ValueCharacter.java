package org.reldb.rel.v1.values;

import java.io.PrintStream;

import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.v1.generator.Generator;
import org.reldb.rel.v1.types.Type;
import org.reldb.rel.v1.types.TypeAlpha;
import org.reldb.rel.v1.types.builtin.TypeCharacter;
import org.reldb.rel.v1.vm.Context;

public class ValueCharacter extends ValueAlpha implements Comparable<Value> {
	
	private static final long serialVersionUID = 0;

	private String internalValue;
	
	/** Return a String given a quote-delimited source string.  Inverse of toParsableString(). */
	public static String stripDelimitedString(String b) {
		return StringUtils.unquote(b.substring(1, b.length() - 1));
	}
	
	/** Return a ValueCharacter given a quote-delimited source string. */
	public static Value stripDelimited(Generator generator, String b) {
		return new ValueCharacter(generator, stripDelimitedString(b));
	}
	
	public static ValueCharacter select(Generator generator, String x) {
		return (ValueCharacter)generator.selectValue(TypeCharacter.getInstance(), new ValueCharacter(generator, x));
	}
	
	private ValueCharacter(Generator generator, String b) {
		super(generator, TypeCharacter.getInstance(), new Value[1], 0);
		internalValue = b;
	}
 	
	public Value getComponentValue(int offsetInValue) {
		return select(getGenerator(), internalValue);
	}

	public void setComponentValue(int offsetInValue, Value value) {
		internalValue = value.stringValue();
	}
	
	public void toStream(Context context, Type contextualType, PrintStream p, int depth) {
		Generator generator = getGenerator();
		TypeAlpha type = (TypeAlpha)getType(context.getVirtualMachine().getRelDatabase());
		setMST(generator.findMST(type, new ValueCharacter(generator, internalValue)));
		String typeSignature = getType(generator.getDatabase()).getSignature(); 
		if (typeSignature.equals(TypeCharacter.Name))
			p.print((depth > 0) ? toParsableString(contextualType) : toString());
		else
			p.print(typeSignature + "(" + toParsableString(contextualType) + ")");
	}
	
	/** Convert this to a primitive boolean. */
	public boolean booleanValue() {
		return (internalValue.compareToIgnoreCase("true")==0) ? true : false;
	}
	
	/** Convert this to a primitive long. */
	public long longValue() {
		try {
			return (long)Double.parseDouble(internalValue);
		} catch (NumberFormatException nfe) {
			throw new ExceptionSemantic("RS0390: '" + internalValue + "' isn't a valid number.");
		}
	}
	
	/** Convert this to a primitive double. */
	public double doubleValue() {
		try {
			return Double.parseDouble(internalValue);
		} catch (NumberFormatException nfe) {
			throw new ExceptionSemantic("RS0391: '" + internalValue + "' isn't a valid number.");
		}
	}
	
	/** Convert this to a primitive String. */
	public String stringValue() {
		return internalValue;
	}

	public int hashCode() {
		return internalValue.hashCode();
	}

	public int compareTo(Value v) {
		return internalValue.compareTo(v.stringValue());
	}

	public String toString() {
		return internalValue;
	}

	public String toParsableString(Type type) {
		return "\"" + StringUtils.quote(internalValue) + "\"";
	}

}
