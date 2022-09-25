package org.reldb.rel.v0.vm;

import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.values.Value;

public class CellMutable implements Cell {

	private Value value;
	
	public Value getValue(Generator generator) {
		return value;
	}

	public void setValue(Generator generator, Value v) {
		value = v;
	}
}
