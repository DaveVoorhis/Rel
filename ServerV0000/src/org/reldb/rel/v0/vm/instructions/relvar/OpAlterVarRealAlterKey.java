package org.reldb.rel.v0.vm.instructions.relvar;

import org.reldb.rel.v0.storage.relvars.RelvarHeading;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;
import org.reldb.rel.v0.vm.VirtualMachine;

public class OpAlterVarRealAlterKey extends Instruction {

	private String varname;
	private RelvarHeading keydefs;

	public OpAlterVarRealAlterKey(String varname, RelvarHeading keydefs) {
		this.varname = varname;
		this.keydefs = keydefs;
	}

	@Override
	public void execute(Context context) {
		VirtualMachine vm = context.getVirtualMachine();
		vm.getRelDatabase().alterVarRealAlterKey(context.getGenerator(), varname, keydefs);
	}

}
