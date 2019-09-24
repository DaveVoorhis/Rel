package org.reldb.rel.v0.vm.instructions.core;

import org.reldb.rel.v0.generator.Generator;
import org.reldb.rel.v0.generator.OperatorDefinition;
import org.reldb.rel.v0.generator.OperatorSignature;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;

public class OpInvokeDynamicCall extends Instruction {
	
	private DynamicDispatch dispatcher;
	
	public OpInvokeDynamicCall(Generator generator, OperatorSignature invocation) {
		dispatcher = new DynamicDispatch(generator, invocation, generator.getCurrentOperatorDefinition()) {
			void invoke(OperatorDefinition operator, Context context) {
				operator.call(context);
			}
		};
	}
	
	public final void execute(Context context) {
		dispatcher.locateAndInvoke(context);
	}
}
