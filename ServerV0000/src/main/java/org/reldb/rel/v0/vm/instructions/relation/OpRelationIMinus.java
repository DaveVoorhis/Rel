package org.reldb.rel.v0.vm.instructions.relation;

import org.reldb.rel.v0.values.ValueRelation;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

public class OpRelationIMinus extends Instruction {

	@Override
	public void execute(Context context) {
	    // Relation I_MINUS.
	    // POP - ValueRelation
	    // POP - ValueRelation
	    // PUSH - ValueRelation
		ValueRelation v2 = (ValueRelation)context.pop();
		context.push(((ValueRelation)context.pop()).iminus(v2));
	}

}
