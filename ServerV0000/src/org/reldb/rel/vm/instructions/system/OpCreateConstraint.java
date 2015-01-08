/**
 * 
 */
package org.reldb.rel.vm.instructions.system;

import org.reldb.rel.generator.References;
import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;
import org.reldb.rel.vm.Operator;
import org.reldb.rel.vm.VirtualMachine;

public final class OpCreateConstraint extends Instruction {
	private String constraintName;
	private String sourceCode;
	private Operator operator;
	private String owner;
	private References references;
	
	public OpCreateConstraint(String constraintName, String sourceCode, Operator operator, String owner, References references) {
		this.constraintName = constraintName;
		this.sourceCode = sourceCode;
		this.operator = operator;
		this.owner = owner;
		this.references = references;
	}
	
	public void execute(Context context) {
		VirtualMachine vm = context.getVirtualMachine();
		vm.getRelDatabase().createConstraint(context.getGenerator(), vm, constraintName, sourceCode, operator, owner, references);
	}
}
