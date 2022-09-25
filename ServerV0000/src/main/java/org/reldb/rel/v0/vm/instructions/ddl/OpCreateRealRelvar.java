/**
 * 
 */
package org.reldb.rel.v0.vm.instructions.ddl;

import java.util.HashMap;

import org.reldb.rel.v0.storage.relvars.RelvarDefinition;
import org.reldb.rel.v0.vm.Context;
import org.reldb.rel.v0.vm.Instruction;
import org.reldb.rel.v0.vm.VirtualMachine;

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
