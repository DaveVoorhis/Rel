/**
 * 
 */
package org.reldb.rel.v1.vm.instructions.relation;

import org.reldb.rel.v1.values.TupleIteration;
import org.reldb.rel.v1.values.ValueRelation;
import org.reldb.rel.v1.values.ValueTuple;
import org.reldb.rel.v1.vm.Context;
import org.reldb.rel.v1.vm.Instruction;

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