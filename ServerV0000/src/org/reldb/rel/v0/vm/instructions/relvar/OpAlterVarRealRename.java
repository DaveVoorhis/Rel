package org.reldb.rel.v0.vm.instructions.relvar;

import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

public class OpAlterVarRealRename extends Instruction {

	private String varname;
	private String oldAttributeName;
	private String newAttributeName;

	public OpAlterVarRealRename(String varname, String oldAttributeName, String newAttributeName) {
		this.varname = varname;
		this.oldAttributeName = oldAttributeName;
		this.newAttributeName = newAttributeName;
	}

	@Override
	public void execute(Context context) {
		// TODO - alter
		System.out.println("OpAlterVarRealRename: ALTER VAR " + varname + " REAL RENAME " + oldAttributeName + " TO " + newAttributeName);
	}

}
