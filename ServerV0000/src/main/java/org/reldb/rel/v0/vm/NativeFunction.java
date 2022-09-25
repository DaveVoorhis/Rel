package org.reldb.rel.v0.vm;

import org.reldb.rel.v0.values.Value;

public abstract class NativeFunction {
	public abstract Value evaluate(Value arguments[]);
}
