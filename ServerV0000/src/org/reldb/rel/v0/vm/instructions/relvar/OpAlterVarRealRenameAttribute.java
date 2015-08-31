package org.reldb.rel.v0.vm.instructions.relvar;

import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;
import org.reldb.rel.v0.vm.VirtualMachine;

public class OpAlterVarRealRenameAttribute extends Instruction {

	private String varname;
	private String oldAttributeName;
	private String newAttributeName;

	public OpAlterVarRealRenameAttribute(String varname, String oldAttributeName, String newAttributeName) {
		this.varname = varname;
		this.oldAttributeName = oldAttributeName;
		this.newAttributeName = newAttributeName;
	}

	@Override
	public void execute(Context context) {
		VirtualMachine vm = context.getVirtualMachine();
		vm.getRelDatabase().alterVarRealRenameAttribute(context.getGenerator(), varname, oldAttributeName, newAttributeName);
	}

}
