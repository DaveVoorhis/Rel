package org.reldb.rel.vm;

import org.reldb.rel.values.Value;

public abstract class NativeProcedure {
	public abstract void execute(Value arguments[]);
}
