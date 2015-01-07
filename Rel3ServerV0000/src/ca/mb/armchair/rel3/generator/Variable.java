package ca.mb.armchair.rel3.generator;

import ca.mb.armchair.rel3.types.Type;
import ca.mb.armchair.rel3.vm.CellMutableFactory;
import ca.mb.armchair.rel3.vm.instructions.core.OpVariableGet;
import ca.mb.armchair.rel3.vm.instructions.core.OpVariableInitialise;
import ca.mb.armchair.rel3.vm.instructions.core.OpVariableSet;

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
