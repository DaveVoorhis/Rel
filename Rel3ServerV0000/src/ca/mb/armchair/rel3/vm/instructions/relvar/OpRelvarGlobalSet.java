/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.relvar;

import ca.mb.armchair.rel3.types.Heading;
import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.storage.relvars.RelvarGlobal;
import ca.mb.armchair.rel3.storage.relvars.RelvarHeading;
import ca.mb.armchair.rel3.exceptions.*;
import ca.mb.armchair.rel3.values.ValueRelation;

public final class OpRelvarGlobalSet extends Instruction {
	private Heading definedHeading;
//	private RelvarHeading definedKeyDefinition;
	private String relvarName;
	
	public OpRelvarGlobalSet(String relvarName, RelvarHeading headingDefinition) {
		this.relvarName = relvarName;
		definedHeading = headingDefinition.getHeading();
//		definedKeyDefinition = keydef;
	}
	
	public final void execute(Context context) {
		// Set value of relvar
		//
		// POP - ValueRelation
		RelvarGlobal relvar = context.getVirtualMachine().getRelDatabase().openGlobalRelvar(relvarName);
		if (relvar == null)
			throw new ExceptionSemantic("RS0283: Relation-valued variable " + relvarName + " no longer exists.");
		if (!definedHeading.canAccept(relvar.getHeadingDefinition().getHeading()))
			throw new ExceptionSemantic("RS0284: Relation-valued variable " + relvarName + " has heading " + relvar.getHeadingDefinition().getHeading() + " but " + definedHeading + " was expected.");
		// TODO - check for inconsistency between relvar.getKeyDefinition() and definedKeyDefinition.
		relvar.setValue((ValueRelation)context.pop());
	}
	
	public String toString() {
		return getName() + " " + relvarName;
	}	
}
