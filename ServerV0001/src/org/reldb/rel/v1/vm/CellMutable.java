package org.reldb.rel.v1.vm;

import org.reldb.rel.v1.generator.Generator;
import org.reldb.rel.v1.values.Value;

public class CellMutable implements Cell {

	private Value value;
	
	public Value getValue(Generator generator) {
		return value;
	}

	public void setValue(Generator generator, Value v) {
		value = v;
	}
}
