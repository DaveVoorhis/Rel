/**
 * 
 */
package org.reldb.rel.v0.vm.instructions.relation;

import org.reldb.rel.v0.values.*;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

public final class OpRelationDupRemove extends Instruction {

	public final void execute(Context context) {
		// Remove duplicates in the popped "relation".
		//
		// POP - ValueRelation
		// PUSH - ValueRelation
		ValueRelation source = (ValueRelation)context.pop();
		context.push(new ValueRelation(context.getGenerator()) {
			private static final long serialVersionUID = 1L;
			@Override
			public TupleIterator newIterator() {
				return new TupleIteratorUnique(source.iterator());
			}
			@Override
			public int hashCode() {
				return 0;
			}			
		});
	}
}