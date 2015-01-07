/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.relvar;

import ca.mb.armchair.rel3.types.Heading;
import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.storage.relvars.RelvarGlobal;
import ca.mb.armchair.rel3.storage.relvars.RelvarHeading;
import ca.mb.armchair.rel3.exceptions.ExceptionSemantic;

public final class OpRelvarGlobalGet extends Instruction {
	private Heading definedHeading;
//	private RelvarHeading definedKeyDefinition;
	private String relvarName;
	
	public OpRelvarGlobalGet(String relvarName, RelvarHeading headingDefinition) {
		this.relvarName = relvarName;
		definedHeading = headingDefinition.getHeading();
//		definedKeyDefinition = keydef;
	}
	
	public final void execute(Context context) {
		// Get value of relvar
		//
		// PUSH - ValueRelation
		RelvarGlobal relvar = context.getVirtualMachine().getRelDatabase().openGlobalRelvar(relvarName);
		if (relvar == null)
			throw new ExceptionSemantic("RS0281: Relation-valued variable " + relvarName + " no longer exists.");
		if (!definedHeading.canAccept(relvar.getHeadingDefinition().getHeading()))
			throw new ExceptionSemantic("RS0282: Relation-valued variable " + relvarName + " has heading " + relvar.getHeadingDefinition().getHeading() + " but " + definedHeading + " was expected.");
		// TODO - check for inconsistency between relvar.getKeyDefinition() and definedKeyDefinition.
		context.push(relvar.getValue(context.getGenerator()));
	}
	
	public String toString() {
		return getName() + " " + relvarName;
	}
}
