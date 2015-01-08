/**
 * 
 */
package org.reldb.rel.v0.vm.instructions.relvar;

import org.reldb.rel.exceptions.*;
import org.reldb.rel.v0.storage.relvars.RelvarGlobal;
import org.reldb.rel.v0.storage.relvars.RelvarHeading;
import org.reldb.rel.v0.types.Heading;
import org.reldb.rel.v0.values.ValueRelation;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

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
