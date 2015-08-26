package org.reldb.rel.v0.vm.instructions.relvar;

import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

public class OpAlterVarRealDropAttribute extends Instruction {

	private String varname;
	private String attributeName;

	public OpAlterVarRealDropAttribute(String varname, String attributeName) {
		this.varname = varname;
		this.attributeName = attributeName;
	}

	@Override
	public void execute(Context context) {
		// TODO - alter
		System.out.println("OpAlterVarRealDropAttribute: ALTER VAR " + varname + " REAL DROP " + attributeName);
	}

}
