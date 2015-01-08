package org.reldb.rel.v0.vm;

import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.values.Value;

/** A Value container, used to implement variables at run-time. */
public interface Cell {
	public void setValue(Generator generator, Value v);
	public Value getValue(Generator generator);
}
