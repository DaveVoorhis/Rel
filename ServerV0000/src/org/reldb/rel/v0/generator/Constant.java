package org.reldb.rel.v0.generator;

import org.reldb.rel.exceptions.*;
import org.reldb.rel.v0.types.Type;
import org.reldb.rel.v0.vm.CellMutableFactory;
import org.reldb.rel.v0.vm.instructions.core.OpVariableGet;
import org.reldb.rel.v0.vm.instructions.core.OpVariableInitialise;

public class Constant extends SlotScoped {

	private boolean initialised = false;

	private static CellMutableFactory variableFactory = new CellMutableFactory();
	
	public Constant(int depth, int offset, Type type) {
		super(depth, offset, type);
	}

	@Override
	public void compileGet(Generator generator) {
		if (!initialised)
			throw new ExceptionSemantic("RS0014: Attempt to access uninitialised constant.");
		generator.compileInstruction(new OpVariableGet(getDepth(), getOffset()));
	}

	@Override
	public void compileSet(Generator generator) {
		if (initialised)
			throw new ExceptionSemantic("RS0015: Attempt to assign a value to a constant.");
	}
	
	@Override
	public void compileInitialise(Generator generator) {
		generator.compileInstruction(new OpVariableInitialise(getDepth(), getOffset(), variableFactory));
		initialised = true;
	}
}
