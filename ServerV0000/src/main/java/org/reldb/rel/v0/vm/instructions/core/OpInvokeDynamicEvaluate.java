package org.reldb.rel.v0.vm.instructions.core;

import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.generator.OperatorDefinition;
import org.reldb.rel.v0.generator.OperatorSignature;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

public class OpInvokeDynamicEvaluate extends Instruction {

	private DynamicDispatch dispatcher;
	
	public OpInvokeDynamicEvaluate(Generator generator, OperatorSignature invocation) {
		dispatcher = new DynamicDispatch(generator, invocation, generator.getCurrentOperatorDefinition()) {
			void invoke(OperatorDefinition operator, Context context) {
				operator.evaluate(context);
			}
		};
	}
	
	public final void execute(Context context) {
		dispatcher.locateAndInvoke(context);
	}
}
