package org.reldb.rel.vm;

import org.reldb.rel.generator.Generator;
import org.reldb.rel.values.Value;

/** A Value container, used to implement variables at run-time. */
public interface Cell {
	public void setValue(Generator generator, Value v);
	public Value getValue(Generator generator);
}
