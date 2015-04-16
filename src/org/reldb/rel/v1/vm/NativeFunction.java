package org.reldb.rel.v1.vm;

import org.reldb.rel.v1.values.Value;

public abstract class NativeFunction {
	public abstract Value evaluate(Value arguments[]);
}
