package org.reldb.rel.v0.vm.instructions.relvar;

import org.reldb.rel.v0.types.Type;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

public class OpAlterVarRealChangeType extends Instruction {

	private String varname;
	private String attributeName;
	private Type newType;

	public OpAlterVarRealChangeType(String varname, String attributeName, Type newType) {
		this.varname = varname;
		this.attributeName = attributeName;
		this.newType = newType;
	}

	@Override
	public void execute(Context context) {
		// TODO - alter
		System.out.println("OpAlterVarRealChangeType: ALTER VAR " + varname + " REAL TYPE_OF " + attributeName + " TO " + newType.getSignature());
	}

}
