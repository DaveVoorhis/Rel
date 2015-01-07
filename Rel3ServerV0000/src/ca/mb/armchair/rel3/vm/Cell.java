package ca.mb.armchair.rel3.vm;

import ca.mb.armchair.rel3.generator.Generator;
import ca.mb.armchair.rel3.values.Value;

/** A Value container, used to implement variables at run-time. */
public interface Cell {
	public void setValue(Generator generator, Value v);
	public Value getValue(Generator generator);
}
