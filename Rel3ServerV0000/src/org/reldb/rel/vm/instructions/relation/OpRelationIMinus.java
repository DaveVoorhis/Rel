package org.reldb.rel.vm.instructions.relation;

import org.reldb.rel.values.ValueRelation;
import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;

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
