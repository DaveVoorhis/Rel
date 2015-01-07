package ca.mb.armchair.rel3.vm.instructions.core;

import ca.mb.armchair.rel3.values.ValueOperator;
import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.vm.Operator;

public class OpInvokeAnonymousEvaluate extends Instruction {

	@Override
	public void execute(Context context) {
		ValueOperator operator = (ValueOperator)context.pop();
		Operator invocable = operator.getOperator(context.getGenerator());
		if (operator.getEnclosingContext() != null)
			context.call(invocable, operator.getEnclosingContext());
		else
			context.call(invocable);	
	}

}
