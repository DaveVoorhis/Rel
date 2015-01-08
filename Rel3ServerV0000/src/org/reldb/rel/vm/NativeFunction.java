package org.reldb.rel.vm;

import org.reldb.rel.values.Value;

public abstract class NativeFunction {
	public abstract Value evaluate(Value arguments[]);
}
