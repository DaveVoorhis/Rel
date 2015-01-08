package org.reldb.rel.vm;

import org.reldb.rel.generator.Generator;
import org.reldb.rel.values.Value;

public class CellMutable implements Cell {

	private Value value;
	
	public Value getValue(Generator generator) {
		return value;
	}

	public void setValue(Generator generator, Value v) {
		value = v;
	}
}
