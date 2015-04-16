package org.reldb.rel.v1.vm.instructions.core;

import org.reldb.rel.v1.values.ValueOperator;
import org.reldb.rel.v1.vm.Context;
import org.reldb.rel.v1.vm.Instruction;
import org.reldb.rel.v1.vm.Operator;

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
