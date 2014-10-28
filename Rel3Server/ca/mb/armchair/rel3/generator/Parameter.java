package ca.mb.armchair.rel3.generator;

import ca.mb.armchair.rel3.types.Type;
import ca.mb.armchair.rel3.vm.CellMutableFactory;
import ca.mb.armchair.rel3.vm.instructions.core.OpParameterGet;
import ca.mb.armchair.rel3.vm.instructions.core.OpParameterSet;
import ca.mb.armchair.rel3.vm.instructions.core.OpVariableInitialise;

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
