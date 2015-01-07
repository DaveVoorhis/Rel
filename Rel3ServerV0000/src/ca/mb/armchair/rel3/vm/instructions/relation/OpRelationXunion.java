package ca.mb.armchair.rel3.vm.instructions.relation;

import ca.mb.armchair.rel3.values.ValueRelation;
import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;

public class OpRelationXunion extends Instruction {

	@Override
	public void execute(Context context) {
	    // Relation XUNION.
	    // POP - ValueRelation
	    // POP - ValueRelation
	    // PUSH - ValueRelation
		ValueRelation v2 = (ValueRelation)context.pop();
		context.push(((ValueRelation)context.pop()).xunion(v2));
	}

}
