/**
 * 
 */
package org.reldb.rel.vm.instructions.relation;

import org.reldb.rel.values.TupleIteration;
import org.reldb.rel.values.ValueRelation;
import org.reldb.rel.values.ValueTuple;
import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;

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