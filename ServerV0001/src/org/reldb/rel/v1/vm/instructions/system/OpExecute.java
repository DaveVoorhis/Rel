/**
 * 
 */
package org.reldb.rel.v1.vm.instructions.system;

import org.reldb.rel.exceptions.ExceptionSemantic;
import org.reldb.rel.v1.interpreter.Interpreter;
import org.reldb.rel.v1.languages.tutoriald.parser.ParseException;
import org.reldb.rel.v1.values.ValueCharacter;
import org.reldb.rel.v1.vm.Context;
import org.reldb.rel.v1.vm.Instruction;
import org.reldb.rel.v1.vm.VirtualMachine;

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
