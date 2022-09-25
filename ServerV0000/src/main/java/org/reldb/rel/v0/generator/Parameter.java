package org.reldb.rel.v0.generator;

import org.reldb.rel.v0.types.Type;
import org.reldb.rel.v0.vm.CellMutableFactory;
import org.reldb.rel.v0.vm.instructions.core.OpParameterGet;
import org.reldb.rel.v0.vm.instructions.core.OpParameterSet;
import org.reldb.rel.v0.vm.instructions.core.OpVariableInitialise;

public class Parameter extends SlotScoped {
	
	private static CellMutableFactory variableFactory = new CellMutableFactory();
	
	public Parameter(int depth, int offset, Type type) {
		super(depth, offset, type);
	}

	@Override
	public void compileGet(Generator generator) {
		// Compile retrieval of parameter value
		generator.compileInstruction(new OpParameterGet(getDepth(), getOffset()));
	}

	@Override
	public void compileSet(Generator generator) {
		// compile assignment
		generator.compileInstruction(new OpParameterSet(getDepth(), getOffset()));
	}
		
	@Override
	public void compileInitialise(Generator generator) {
		generator.compileInstruction(new OpVariableInitialise(getDepth(), getOffset(), variableFactory));
	}

	@Override
	public boolean isParameter() {
		return true;
	}
}
