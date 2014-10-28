/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.system;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.vm.Operator;
import ca.mb.armchair.rel3.generator.References;
import ca.mb.armchair.rel3.vm.VirtualMachine;

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
