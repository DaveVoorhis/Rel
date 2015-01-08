package org.reldb.rel.v0.generator;

import org.reldb.rel.v0.types.Type;
import org.reldb.rel.v0.vm.CellMutableFactory;
import org.reldb.rel.v0.vm.instructions.core.OpVariableGet;
import org.reldb.rel.v0.vm.instructions.core.OpVariableInitialise;
import org.reldb.rel.v0.vm.instructions.core.OpVariableSet;

public class Variable extends SlotScoped {
	
	private static CellMutableFactory variableFactory = new CellMutableFactory();
	
	public Variable(int depth, int offset, Type type) {
		super(depth, offset, type);
	}

	public Variable(int depth, int offset) {
		super(depth, offset);
	}
	
	@Override
	public void compileGet(Generator generator) {
		// Compile retrieval of variable value
		generator.compileInstruction(new OpVariableGet(getDepth(), getOffset()));
	}

	@Override
	public void compileSet(Generator generator) {
		// compile assignment
		generator.compileInstruction(new OpVariableSet(getDepth(), getOffset()));
	}
	
	@Override
	public void compileInitialise(Generator generator) {
		generator.compileInstruction(new OpVariableInitialise(getDepth(), getOffset(), variableFactory));
	}

	@Override
	public boolean isParameter() {
		return false;
	}
}
