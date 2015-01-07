package ca.mb.armchair.rel3.vm;

import ca.mb.armchair.rel3.generator.Generator;
import ca.mb.armchair.rel3.values.Value;

public class CellMutable implements Cell {

	private Value value;
	
	public Value getValue(Generator generator) {
		return value;
	}

	public void setValue(Generator generator, Value v) {
		value = v;
	}
}
