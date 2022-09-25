package org.reldb.rel.v0.vm;

import org.reldb.rel.v0.values.Value;

public abstract class NativeProcedure {
	public abstract void execute(Value arguments[]);
}
