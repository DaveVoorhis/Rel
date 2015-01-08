/**
 * 
 */
package org.reldb.rel.vm.instructions.system;

import java.util.HashMap;

import org.reldb.rel.storage.relvars.RelvarDefinition;
import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;
import org.reldb.rel.vm.VirtualMachine;

public final class OpCreateRealRelvar extends Instruction {

	private RelvarDefinition information;
	private HashMap<String, RelvarDefinition> pendingRelvars;
	
	public OpCreateRealRelvar(HashMap<String, RelvarDefinition> pendingRelvars, RelvarDefinition information) {
		this.information = information;
		this.pendingRelvars = pendingRelvars;
	}
	
	public void execute(Context context) {
		VirtualMachine vm = context.getVirtualMachine();
		vm.getRelDatabase().createRealRelvar(context.getGenerator(), information);
    	pendingRelvars.remove(information.getName());
	}
}
