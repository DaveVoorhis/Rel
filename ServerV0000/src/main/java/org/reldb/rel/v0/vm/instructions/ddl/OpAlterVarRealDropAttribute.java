package org.reldb.rel.v0.vm.instructions.ddl;

import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;
import org.reldb.rel.v0.vm.VirtualMachine;

public class OpAlterVarRealDropAttribute extends Instruction {

	private String varname;
	private String attributeName;

	public OpAlterVarRealDropAttribute(String varname, String attributeName) {
		this.varname = varname;
		this.attributeName = attributeName;
	}

	@Override
	public void execute(Context context) {
		VirtualMachine vm = context.getVirtualMachine();
		vm.getRelDatabase().alterVarRealDropAttribute(context.getGenerator(), varname, attributeName);
	}

}
