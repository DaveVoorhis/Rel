/**
 * 
 */
package org.reldb.rel.vm.instructions.system;

import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.interpreter.Interpreter;
import org.reldb.rel.languages.tutoriald.parser.ParseException;
import org.reldb.rel.values.ValueCharacter;
import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;
import org.reldb.rel.vm.VirtualMachine;

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
