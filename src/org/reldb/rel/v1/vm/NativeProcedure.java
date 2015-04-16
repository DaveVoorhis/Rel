package org.reldb.rel.v1.vm;

import org.reldb.rel.v1.values.Value;

public abstract class NativeProcedure {
	public abstract void execute(Value arguments[]);
}
