/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.relation;

import ca.mb.armchair.rel3.values.TupleIteration;
import ca.mb.armchair.rel3.values.ValueRelation;
import ca.mb.armchair.rel3.values.ValueTuple;
import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;

public final class OpRelationWrite extends Instruction {
	public final void execute(Context context) {
		ValueRelation relation = (ValueRelation)context.pop();
		System.out.println("RELATION {");
		(new TupleIteration(relation.iterator()) {
			public void process(ValueTuple tuple) {
				System.out.println("  " + tuple);				
			}
		}).run();
		System.out.println("}");
	}
}