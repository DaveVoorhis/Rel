package org.reldb.rel.v1.vm;

import org.reldb.rel.v1.generator.Generator;
import org.reldb.rel.v1.values.Value;

/** A Value container, used to implement variables at run-time. */
public interface Cell {
	public void setValue(Generator generator, Value v);
	public Value getValue(Generator generator);
}
