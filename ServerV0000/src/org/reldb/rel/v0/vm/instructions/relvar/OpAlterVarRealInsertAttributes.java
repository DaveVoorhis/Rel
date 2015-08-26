package org.reldb.rel.v0.vm.instructions.relvar;

import org.reldb.rel.v0.types.Heading;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

public class OpAlterVarRealInsertAttributes extends Instruction {

	private String varname;
	private Object heading;

	public OpAlterVarRealInsertAttributes(String varname, Heading heading) {
		this.varname = varname;
		this.heading = heading;
	}

	@Override
	public void execute(Context context) {
		// TODO - alter
		System.out.println("OpAlterVarRealInsertAttributes: ALTER VAR " + varname + " REAL INSERT " + heading.toString());
	}

}
