package org.reldb.rel.v1.values;
 
import java.io.PrintStream;
import java.io.Serializable;

import org.reldb.rel.v1.generator.Generator;
import org.reldb.rel.v1.types.*;
import org.reldb.rel.v1.vm.Context;

/** An abstract Value, that defines all possible operations on abstract ValueS.
 * 
 *  If an operation is not supported, throw SemanticException.
 */
public interface Value extends Serializable {
	
	/** Invoked on retrieval from database */
	public void loaded(Generator generator);
	
	/** Obtain the type name of this Value. */
	public String getTypeName();
	
	/** Output this Value, interpreted as the given Type, to a PrintStream. */
	public void toStream(Context context, Type type, PrintStream p, int depth);
	
	/** Write as parsable string. */
	public String toParsableString(Type type);
	
	/** Write as final string. */
	public String toString(Type type);
	
	/** Return this value's hashCode() */
	public int hashCode();
	
	/** Compare this value and another. */
	public int compareTo(Value v);

	/** Check for equality. */
	public boolean equals(Object o);

	/** Obtain a serializable clone of this value.  Used to obtain serializable RVA values in TempStorageTuples,
	 but may be needed elsewhere??? */
	// TODO - explore eliminating this, if possible
	public Value getSerializableClone();
	
	/** Convert this to a primitive boolean. */
	public boolean booleanValue();
	
	/** Convert this to a primitive long. */
	public long longValue();
	
	/** Convert this to a primitive double. */
	public double doubleValue();

	/** Convert this to a primitive string. */
	public String stringValue();
}
