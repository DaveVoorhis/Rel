package org.reldb.rel.v0.vm.instructions.ddl;

import org.reldb.rel.v0.types.Heading;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;
import org.reldb.rel.v0.vm.VirtualMachine;

public class OpAlterVarRealInsertAttributes extends Instruction {

	private String varname;
	private Heading heading;

	public OpAlterVarRealInsertAttributes(String varname, Heading heading) {
		this.varname = varname;
		this.heading = heading;
	}

	@Override
	public void execute(Context context) {
		VirtualMachine vm = context.getVirtualMachine();
		vm.getRelDatabase().alterVarRealInsertAttributes(context.getGenerator(), varname, heading);
	}

}
