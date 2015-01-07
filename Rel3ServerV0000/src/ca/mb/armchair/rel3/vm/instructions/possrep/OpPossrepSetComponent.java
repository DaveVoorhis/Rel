/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.possrep;

import ca.mb.armchair.rel3.values.ValueAlpha;
import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;

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