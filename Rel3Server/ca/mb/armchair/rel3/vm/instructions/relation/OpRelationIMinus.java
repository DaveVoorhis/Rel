package ca.mb.armchair.rel3.vm.instructions.relation;

import ca.mb.armchair.rel3.values.ValueRelation;
import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;

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
