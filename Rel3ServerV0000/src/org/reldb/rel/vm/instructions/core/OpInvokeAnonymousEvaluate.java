package org.reldb.rel.vm.instructions.core;

import org.reldb.rel.values.ValueOperator;
import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;
import org.reldb.rel.vm.Operator;

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
