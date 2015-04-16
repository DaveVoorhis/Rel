/**
 * 
 */
package org.reldb.rel.v1.vm.instructions.possrep;

import org.reldb.rel.v1.values.ValueAlpha;
import org.reldb.rel.v1.vm.Context;
import org.reldb.rel.v1.vm.Instruction;

public final class OpPossrepSetComponent extends Instruction {

	private int offsetInValue;
	
	public OpPossrepSetComponent(int offsetInValue) {
		this.offsetInValue = offsetInValue;
	}
	
	public final void execute(Context context) {
		((ValueAlpha)context.pop()).setComponentValue(offsetInValue, context.pop());
	}
	
	public String toString() {
		return super.toString() + " to possrep offset " + offsetInValue;
	}
}