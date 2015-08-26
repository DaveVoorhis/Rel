package org.reldb.rel.v0.vm.instructions.relvar;

import org.reldb.rel.v0.storage.relvars.RelvarHeading;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

public class OpAlterVarRealAlterKey extends Instruction {

	private String varname;
	private Object keydefs;

	public OpAlterVarRealAlterKey(String varname, RelvarHeading keydefs) {
		this.varname = varname;
		this.keydefs = keydefs;
	}

	@Override
	public void execute(Context context) {
		// TODO - alter
		System.out.println("OpAlterVarRealAlterKey: ALTER VAR " + varname + " ALTER " + keydefs.toString());
	}

}
