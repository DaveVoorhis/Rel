/**
 * 
 */
package org.reldb.rel.v1.vm.instructions.system;

import java.util.HashMap;

import org.reldb.rel.v1.storage.relvars.RelvarDefinition;
import org.reldb.rel.v1.vm.Context;
import org.reldb.rel.v1.vm.Instruction;
import org.reldb.rel.v1.vm.VirtualMachine;

public final class OpCreateExternalRelvar extends Instruction {

	private RelvarDefinition information;
	private HashMap<String, RelvarDefinition> pendingRelvars;
	
	public OpCreateExternalRelvar(HashMap<String, RelvarDefinition> pendingRelvars, RelvarDefinition information) {
		this.information = information;
		this.pendingRelvars = pendingRelvars;
	}
	
	public void execute(Context context) {
		VirtualMachine vm = context.getVirtualMachine();
		vm.getRelDatabase().createExternalRelvar(context.getGenerator(), information);
    	pendingRelvars.remove(information.getName());
	}
}
