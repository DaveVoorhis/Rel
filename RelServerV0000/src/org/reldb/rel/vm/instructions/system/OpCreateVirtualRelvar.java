/**
 * 
 */
package org.reldb.rel.vm.instructions.system;

import java.util.HashMap;

import org.reldb.rel.storage.relvars.RelvarDefinition;
import org.reldb.rel.vm.Context;
import org.reldb.rel.vm.Instruction;
import org.reldb.rel.vm.VirtualMachine;

public final class OpCreateVirtualRelvar extends Instruction {

	private RelvarDefinition information;
	private HashMap<String, RelvarDefinition> pendingRelvars;
	
	public OpCreateVirtualRelvar(HashMap<String, RelvarDefinition> pendingRelvars, RelvarDefinition information) {
		this.information = information;
		this.pendingRelvars = pendingRelvars;
	}
	
	public void execute(Context context) {
		VirtualMachine vm = context.getVirtualMachine();
		vm.getRelDatabase().createVirtualRelvar(context.getGenerator(), information);
    	pendingRelvars.remove(information.getName());
	}
}
