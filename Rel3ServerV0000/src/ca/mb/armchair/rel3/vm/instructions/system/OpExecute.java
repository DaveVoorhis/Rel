/**
 * 
 */
package ca.mb.armchair.rel3.vm.instructions.system;

import ca.mb.armchair.rel3.exceptions.ExceptionSemantic;
import ca.mb.armchair.rel3.vm.Context;
import ca.mb.armchair.rel3.vm.Instruction;
import ca.mb.armchair.rel3.vm.VirtualMachine;
import ca.mb.armchair.rel3.values.ValueCharacter;
import ca.mb.armchair.rel3.interpreter.Interpreter;
import ca.mb.armchair.rel3.languages.tutoriald.parser.ParseException;

public final class OpExecute extends Instruction {

	public final void execute(Context context) {
		String statement = ((ValueCharacter)context.pop()).stringValue();
		try {
			VirtualMachine vm = context.getVirtualMachine();
			Interpreter.executeStatement(vm.getRelDatabase(), statement, vm.getPrintStream());
		} catch (ParseException pe) {
			throw new ExceptionSemantic("RS0286: " + pe.getMessage());
		}
	}
}
