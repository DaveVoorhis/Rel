/**
 * 
 */
package org.reldb.rel.v1.vm.instructions.relvar;

import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.v1.storage.relvars.RelvarGlobal;
import org.reldb.rel.v1.storage.relvars.RelvarHeading;
import org.reldb.rel.v1.types.Heading;
import org.reldb.rel.v1.vm.Context;
import org.reldb.rel.v1.vm.Instruction;

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
