package org.reldb.rel.vm.instructions.core;

import org.reldb.rel.vm.Cell;
import org.reldb.rel.vm.CellFactory;
import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;

public class OpVariableInitialise extends Instruction {

	private int depth;
	private int offset;
	private CellFactory cellFactory;
	
	public OpVariableInitialise(int depth, int offset, CellFactory cellFactory) {
		this.depth = depth;
		this.offset = offset;
		this.cellFactory = cellFactory;
	}

	// Create a new Cell, using the provided CellFactory.
	// POP - initial value to be stored in new cell.
	public final void execute(Context context) {
		Cell cell = cellFactory.getNewCell();
		cell.setValue(context.getGenerator(), context.pop());
		context.varSetCell(depth, offset, cell);
	}

	public String toString() {
		return getName() + " " + depth + " " + offset;
	}
}
