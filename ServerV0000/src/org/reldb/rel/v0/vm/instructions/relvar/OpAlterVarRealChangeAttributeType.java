package org.reldb.rel.v0.vm.instructions.relvar;

import org.reldb.rel.v0.types.Type;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;
import org.reldb.rel.v0.vm.VirtualMachine;

public class OpAlterVarRealChangeAttributeType extends Instruction {

	private String varname;
	private String attributeName;
	private Type newType;

	public OpAlterVarRealChangeAttributeType(String varname, String attributeName, Type newType) {
		this.varname = varname;
		this.attributeName = attributeName;
		this.newType = newType;
	}

	@Override
	public void execute(Context context) {
		VirtualMachine vm = context.getVirtualMachine();
		vm.getRelDatabase().alterVarRealChangeAttributeType(context.getGenerator(), varname, attributeName, newType);
	}

}
