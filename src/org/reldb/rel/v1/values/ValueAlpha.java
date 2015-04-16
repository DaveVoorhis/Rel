package org.reldb.rel.v1.values;
 
import java.io.PrintStream;

import org.reldb.rel.exceptions.*;
import org.reldb.rel.v1.generator.Generator;
import org.reldb.rel.v1.storage.RelDatabase;
import org.reldb.rel.v1.types.Type;
import org.reldb.rel.v1.types.TypeAlpha;
import org.reldb.rel.v1.types.userdefined.Possrep;
import org.reldb.rel.v1.types.userdefined.PossrepComponent;
import org.reldb.rel.v1.vm.Context;

public class ValueAlpha extends ValueAbstract {

	private static final long serialVersionUID = 0;

	private String initialTypeName;
	private String typeName;
	private Value[] internal;
	private int possrepNumber;
	
	private volatile boolean hasBeenSerializablyCloned = false;
	
	// Create new user-defined value.
	public ValueAlpha(Generator generator, TypeAlpha type, Value v[], int possrepNumber) {
		super(generator);
		// TODO - rewrite this constructor to deal with multiple inheritance.
		initialTypeName = type.getTypeName();
		typeName = initialTypeName;		
		this.possrepNumber = possrepNumber;
		internal = new Value[type.getComponentCount()];
		Possrep possrep = type.getPossrep(possrepNumber);
		int possrepComponentCount = possrep.getComponentCount();
		if (v.length != possrepComponentCount)
			throw new ExceptionFatal("RS0384: select parameter count != POSSREP component count.");
		for (int componentIndex = 0; componentIndex < possrepComponentCount; componentIndex++)
			internal[possrep.getComponent(componentIndex).getComponentIndex()] = v[componentIndex];
	}

	// Create default user-defined value.
	public ValueAlpha(Generator generator, TypeAlpha type) {
		super(generator);
		initialTypeName = type.getTypeName();
		typeName = initialTypeName;
		internal = new Value[type.getComponentCount()];
		this.possrepNumber = -1;
	}

	/** Obtain a serializable clone of this value. */
	public Value getSerializableClone() {
		// TODO - this is vastly questionable; replace it.  Needed to prevent failures due (probably) to failing comparisons to self.
		if (hasBeenSerializablyCloned)
			return this;
		for (int i=0; i<internal.length; i++)
			internal[i] = (internal[i]==null) ? null : internal[i].getSerializableClone();
		hasBeenSerializablyCloned = true;
		return this;
	}
	
	public void loaded(Generator generator) {
		super.loaded(generator);
		for (Value v: internal)
			if (v != null)
				v.loaded(generator);
	}
	
	public void setMST(TypeAlpha mst) {
		if (!typeName.equals(mst.getTypeName())) {
			typeName = mst.getTypeName();
			possrepNumber = 0;
		}
	}

	public int hashCode() {
		int code = 0;
		for (Value v: internal)
			if (v != null)
				code += v.hashCode();
		return code;
	}
	
	@Override
	public String getTypeName() {
		return typeName;
	}
	
	public Value getComponentValue(int offsetInValue) {
		return internal[offsetInValue];
	}

	public void setComponentValue(int offsetInValue, Value value) {
		internal[offsetInValue] = value;
	}
	
	private void noConversions() {
		throw new ExceptionSemantic("RS0269: Cannot convert a " + typeName + " to a primitive value.");
	}
	
	/** Convert this to a primitive boolean. */
	public boolean booleanValue() {
		noConversions();
		return false;
	}
	
	/** Convert this to a primitive long. */
	public long longValue() {
		noConversions();
		return 0;
	}
	
	/** Convert this to a primitive double. */
	public double doubleValue() {
		noConversions();
		return 0.0;
	}
	 
	/** Convert this to a primitive string. */
	public String stringValue() {
		String out = null;
		for (int i=0; i<internal.length; i++)
			out = ((out == null) ? "" : out + ", ") + ((internal[i] == null) ? "null" : internal[i].toString());		
		return ((out == null) ? "" : out);
	}
	
	public boolean equals(Object o) {
		throw new ExceptionFatal("RS0385: Bogus 'equals' performed on ALPHA!");
	}
	
	public int compareTo(Value v) {
		// TODO - rewrite this compareTo to deal with multiple inheritance
		boolean allNull = true;
		int comparison = 0;
		for (int i=0; i<internal.length; i++) {
			Value v1 = internal[i];
			Value v2 = ((ValueAlpha)v).internal[i];
			if (v1 == null && v2 == null)
				comparison = 0;
			else if (v1 != null && v2 == null)
				comparison = 1;
			else if (v1 == null && v2 != null)
				comparison = -1;
			else {
				comparison = v1.compareTo(v2);
				allNull = false;
			}
			if (comparison != 0)
				break;
		}
		// Handle the case of types with no elements
		if (comparison == 0 && allNull)
			return getTypeName().compareTo(v.getTypeName());
		return comparison;
	}
	
	public String toString() {
		String out = "{";
		for (int i=0; i<internal.length; i++) {
			if (i>0)
				out += "|";
			out += (internal[i] == null) ? "<null>" : internal[i].toString();
		}
		return out + "}";
	}
	
	/** Return the MST of this value. */
	public Type getType(RelDatabase database) {
		Generator generator = getGenerator();
		Type mst = database.loadType(generator, typeName);
		if (mst == null) {
			mst = database.loadType(generator, initialTypeName);
			setMST((TypeAlpha)mst);
		}
		if (mst == null)
			throw new ExceptionFatal("RS0387: Unable to load MST " + initialTypeName);
		return mst;
	}
		
	/** Output this Value to a PrintStream. */
	public void toStream(Context context, Type contextualType, PrintStream p, int depth) {
		if (contextualType == null)
			throw new ExceptionFatal("RS0388: contextualType is null.");
		TypeAlpha udt;
		int possrepIndex = -1;
		Type type = getType(context.getVirtualMachine().getRelDatabase());
		if (type == null) {
			udt = (TypeAlpha)contextualType;
			if (udt.getPossrepCount() <= 0)
				possrepIndex = -1;
			else
				possrepIndex = 0;
		} else {
			udt = (TypeAlpha)type;
			possrepIndex = possrepNumber;
		}
		if (possrepIndex == -1)
			p.print(udt.getTypeName() + "()");
		else {
			Possrep possrep;
			while ((possrep = udt.getPossrep(possrepIndex)) == null) {
				TypeAlpha superudt = udt.getSupertype();
				if (superudt == null) {
					p.print(udt.getTypeName() + "()");
					return;
				}
				udt = superudt;
			}
			p.print(((possrep.getName() == null) ? udt.getTypeName() : possrep.getName()) + "(");
			for (int i=0; i < possrep.getComponentCount(); i++) {
				if (i > 0)
					p.print(", ");
				PossrepComponent component = possrep.getComponent(i);
				Value v = internal[component.getComponentIndex()];
				if (v != null)
					v.toStream(context, component.getType(), p, depth + 1);
				else {
					p.print("\"[null-" + udt.getTypeName() + "-null]\"");
					System.out.println("ValueAlpha: invalid component at " + component.getComponentIndex() + " size of internal is " + internal.length + " type is " + udt.getTypeName());
					for (int x=0; x<internal.length; x++)
						System.out.println("ValueAlpha:  internal[" + x + "] is " + internal[x]);
				}
			}
			p.print(")");
		}
	}
	
}
