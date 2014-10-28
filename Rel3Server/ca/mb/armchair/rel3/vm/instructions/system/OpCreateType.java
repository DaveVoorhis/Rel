/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.system;

import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.generator.References;

public final class OpCreateType extends Instruction {

	private String typeName;
	private String src;
	private String owner;
	private String language;
	private References references;
	private String superTypeName;
	
	public OpCreateType(String typeName, String src, String owner, String language, References references, String superTypeName) {
		this.typeName = typeName;
		this.src = src;
		this.owner = owner;
		this.language = language;
		this.references = references;
		this.superTypeName = superTypeName;
	}
	
	public void execute(Context context) {
		context.getVirtualMachine().getRelDatabase().createType(context.getGenerator(), typeName, src, owner, language, references, superTypeName);
	}
}
